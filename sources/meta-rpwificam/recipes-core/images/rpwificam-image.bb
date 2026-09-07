SUMMARY = "RP WiFi Camera minimal image with SSH"
DESCRIPTION = "Minimal Raspberry Pi image with OpenSSH server support"

#this image is based on core-image-minimal
require recipes-core/images/core-image-minimal.bb

DISTRO_FEATURES = " systemd"

VIRTUAL-RUNTIME_init_manager = "systemd"

IMAGE_FEATURES:append = " ssh-server-dropbear"

#Make Dropbear start automatically
#SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_AUTO_ENABLE:pn-dropbear = "enable"

#Network / diagnostic utilities
IMAGE_INSTALL:append = " \
iproute2 \
iputils \
ethtool \
procps \
util-linux \
usbutils \
v4l-utils \
"


IMAGE_INSTALL:append = " \
bash \
nano \
less \
"

IMAGE_INSTALL:append = " packagegroup-rpwificam-camera"


IMAGE_FSTYPES = "tar.bz2 wic"

#image name
export IMAGE_BASENAME = "rpwificam-image"

# Allow root login over SSH
# Remove this if you want a more secure production configuration.
EXTRA_USERS_PARAMS:append = " \
    usermod -p '\$1\$rpwifi\$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' root; \
"