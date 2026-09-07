SUMMARY = "MediaMTX RTSP/RTMP/SRT/WebRTC media server"
DESCRIPTION = "Prebuilt MediaMTX media server for Raspberry Pi"
HOMEPAGE = "https://github.com/bluenviron/mediamtx"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=77fd2623bd5398430be5ce60489c2e81"

PV = "1.19.3"

SRC_URI = " \
    https://github.com/bluenviron/mediamtx/releases/download/v1.19.3/mediamtx_v1.19.3_linux_arm64.tar.gz \
    file://mediamtx.service \
    file://mediamtx.yml \
"

SRC_URI[sha256sum] = "9e5b38a5b5fcab1916341b024031b2fc5dc6a2059baed9ba3f3b0d3768d231a8"

S = "${WORKDIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "mediamtx.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/mediamtx \
        ${D}${bindir}/mediamtx

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/mediamtx.yml \
        ${D}${sysconfdir}/mediamtx.yml

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/mediamtx.service \
        ${D}${systemd_system_unitdir}/mediamtx.service
}

FILES:${PN} += " \
    ${sysconfdir}/mediamtx.yml \
    ${systemd_system_unitdir}/mediamtx.service \
"