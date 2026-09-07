#!/bin/bash

# === DEFAULTS ===
#DISTRO_VERSION="custom-weston"
IMAGE_NAME="rpwificam-image"
MACHINE_NAME="raspberrypi3-64"  # Default; override with --machine
BUILD_DIRECTORY="$PWD/build" 
TEMPLATES_DIRECTORY="$PWD/sources/meta-rpwificam/conf/templates/templates1/"
#DOWNLOAD_DIRECTORY="$PWD/downloads" are now defined in the template local.conf 
#SSTATE_DIRECTORY="$PWD/sstate-cache" are now defined in the template local.conf 



# === COLORS ===
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# === FUNCTION: run_command ===
function run_command() {
    local cmd="$1"

    echo -e "${YELLOW}Running:${NC} ${GREEN}$cmd${NC}"

    local start_time=$(date +%s%3N)  # milliseconds
    eval "$cmd"
    local exit_code=$?
    local end_time=$(date +%s%3N)    # milliseconds

    local elapsed_ms=$((end_time - start_time))
    local elapsed_sec=$(echo "scale=3; $elapsed_ms / 1000" | bc)
    local elapsed_min=$(echo "scale=2; $elapsed_sec / 60" | bc)
    local elapsed_hr=$(echo "scale=2; $elapsed_min / 60" | bc)

    echo -e "${GREEN}Time taken: ${elapsed_ms} ms | ${elapsed_sec} s | ${elapsed_min} min | ${elapsed_hr} hr${NC}"
    
    return $exit_code
}

# === FUNCTIONS ===

function source_env() {
    echo "Setting up build environment for machine: $MACHINE_NAME"

    if [[ ! -f "sources/poky/oe-init-build-env" ]]; then
        echo "Error: oe-init-build-env not found."
        return 1
    fi

    # Save our configuration values before Yocto changes the environment
    local machine_name="$MACHINE_NAME"
    local build_directory="$BUILD_DIRECTORY"

 

    # Re-apply our environment variables AFTER oe-init-build-env
    export MACHINE="$machine_name"
    export TEMPLATECONF="$TEMPLATES_DIRECTORY"
    #export DISTRO="$DISTRO_VERSION"


       # Initialize Yocto environment in CURRENT shell
    source sources/poky/oe-init-build-env "$build_directory"

    if [[ $? -ne 0 ]]; then
        echo "Error: failed to initialize Yocto build environment."
        return 1
    fi

    echo ""
    echo "========================================"
    echo " Yocto environment initialized"
    echo "========================================"
    echo "MACHINE       = $MACHINE"
    echo "DL_DIR        = defined in ${TEMPLATECONF}/local.conf.sample"
    echo "SSTATE_DIR    = defined in ${TEMPLATECONF}/local.conf.sample"
    echo "BUILD_DIR     = $BUILDDIR"
    echo "========================================"

}
function build_image() {
    #run_command "MACHINE=$MACHINE_NAME DISTRO=$DISTRO_VERSION bitbake $IMAGE_NAME"
    run_command "MACHINE=$MACHINE_NAME  bitbake $IMAGE_NAME"
}

function build_sdk() {
    #run_command "MACHINE=$MACHINE_NAME DISTRO=$DISTRO_VERSION bitbake -c populate_sdk $IMAGE_NAME"
    run_command "MACHINE=$MACHINE_NAME  bitbake -c populate_sdk $IMAGE_NAME"
}

function build_sd() {
    echo "Building SD card image: $IMAGE_NAME for machine: $MACHINE_NAME"
    local MYDIR
    MYDIR="$(pwd)"

    cd "$IMAGES_PATH" || exit 1

    run_command "$SD_CARD_IMAGE_CREATOR $SD_CARD_TSV_FILE"

    cd "$MYDIR" || exit 1
}


function backup_local_bblayer(){
    #backup bblayer.conf and local.conf from ${BUILD_DIRECTORY}/conf/ to ${TEMPLATES_DIRECTORY}
    #so the builds are reproucebale
        local CONF="$BUILD_DIRECTORY/conf"

    echo "Backing up Yocto configuration..."

    if [[ ! -f "$CONF/local.conf" || ! -f "$CONF/bblayers.conf" ]]; then
        echo -e "${RED}ERROR:${NC} local.conf or bblayers.conf not found in $CONF"
        return 1
    fi

    mkdir -p "$TEMPLATES_DIRECTORY" || return 1

    cp "$CONF/local.conf" "$TEMPLATES_DIRECTORY/local.conf.sample" &&
    cp "$CONF/bblayers.conf" "$TEMPLATES_DIRECTORY/bblayers.conf.sample" || {
        echo -e "${RED}ERROR:${NC} Failed to backup configuration files."
        return 1
    }

    echo -e "${GREEN}SUCCESS:${NC} Configuration backed up to:"
    echo "  $TEMPLATES_DIRECTORY"
    echo "  - local.conf.sample"
    echo "  - bblayers.conf.sample"
}

function usage() {
    echo "Usage: $0 [--machine <machine_name>] [--image <image_name>] [build|env|all|sd|sdk]"
    echo "  --machine <name>   Set the machine name (e.g., stm32mp157c-dk2)"
    echo "  --image <name>     Set the image to build (e.g., st-image-core)"
    echo "  env                - Source the environment only"
    echo "  build              - Source the environment and build the image"
    echo "  sd                 - Create the SD card image"
    echo "  all                - Same as 'build'"
    echo "  sdk                - Generate SDK"
    echo "  back               - to backup local.conf and bblayer.conf"
}

# === PARSE ARGS ===
POSITIONAL_ARGS=()

while [[ $# -gt 0 ]]; do
    case $1 in
        --machine)
            MACHINE_NAME="$2"
            shift 2
            ;;
        --image)
            IMAGE_NAME="$2"
            shift 2
            ;;
        env|build|all|sd|sdk|back)
            POSITIONAL_ARGS+=("$1")
            shift
            ;;
        *)
            usage
            exit 1
            ;;
    esac
done

ACTION="${POSITIONAL_ARGS[0]}"

# === MAIN ===
case "$ACTION" in
    env)
        source_env
        ;;
    sd)
        source_env
        build_sd
        ;;
    build|all)
        source_env
        build_image
        ;;
    sdk)
        source_env
        build_sdk
        ;;
    back)
        backup_local_bblayer
        ;;
    *)
        usage
        ;;
esac