SUMMARY = "Automated FFmpeg Camera Streaming Service"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://ffmpeg-cam.service"

S = "${WORKDIR}"

inherit systemd

RDEPENDS:${PN} += "ffmpeg libcamera mediamtx"

SYSTEMD_SERVICE:${PN} = "ffmpeg-cam.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ffmpeg-cam.service ${D}${systemd_system_unitdir}/ffmpeg-cam.service
}

FILES:${PN} += "${systemd_system_unitdir}/ffmpeg-cam.service"