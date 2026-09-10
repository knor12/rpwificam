SUMMARY = "Automated FFmpeg Camera Streaming Service"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://ffmpeg-cam.service"

# Plain file fetchers unpack directly into UNPACKDIR (${WORKDIR} in Scarthgap)
S = "${WORKDIR}"

inherit systemd

RDEPENDS:${PN} += " \
    ffmpeg \
    mediamtx \
"

SYSTEMD_SERVICE:${PN} = "ffmpeg-cam.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ffmpeg-cam.service ${D}${systemd_system_unitdir}/ffmpeg-cam.service
}
# Force Yocto to produce the RPM package even without compiled binaries
ALLOW_EMPTY:${PN} = "1"


FILES:${PN} += "${systemd_system_unitdir}/ffmpeg-cam.service"