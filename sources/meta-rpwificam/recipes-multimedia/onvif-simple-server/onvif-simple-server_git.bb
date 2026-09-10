LICENSE = "GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://ezxml/license.txt;md5=d4cda95c0365c4d0c092701007508eab"

# Add missing dependencies here
DEPENDS = "json-c libtomcrypt zlib"

# Updated to use https protocol
SRC_URI = "git://github.com/roleoroleo/onvif_simple_server.git;protocol=https;branch=master \
           file://onvif_simple_server.conf \
           file://onvif-simple-server.default \
           file://onvif-simple-server@.service \
          "

# Modify these as desired
PV = "1.0+git"
SRCREV = "95c17f7ef00f16dcaef938951cdf3e28357247f8"

S = "${WORKDIR}/git"

#Enable systemd integration
inherit systemd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
    onvif-simple-server@csi.service \
    onvif-simple-server@webcam1.service \
    onvif-simple-server@webcam2.service \
    onvif-simple-server@webcam3.service \
"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

# Prevent pre-stripping binaries
INSANE_SKIP:${PN} += "already-stripped"

# Pass Yocto toolchain parameters explicitly
TARGET_CC_ARCH += "${LDFLAGS}"

do_configure () {
    :
}

do_compile () {
    oe_runmake CC="${CC}" CFLAGS="${CFLAGS} ${LDFLAGS}" LDFLAGS="${LDFLAGS}"
}

do_install () {
    # Install Binary
    install -d ${D}${bindir}
    install -m 0755 ${S}/onvif_simple_server ${D}${bindir}/

    # Install Configuration Directory
    install -d ${D}${sysconfdir}/onvif_simple_server
    
    # Generate 4 distinct configuration files from base template
    install -m 0644 ${WORKDIR}/onvif_simple_server.conf ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_csi.conf
    install -m 0644 ${WORKDIR}/onvif_simple_server.conf ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_webcam1.conf
    install -m 0644 ${WORKDIR}/onvif_simple_server.conf ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_webcam2.conf
    install -m 0644 ${WORKDIR}/onvif_simple_server.conf ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_webcam3.conf

    # Customize ONVIF ports & RTSP endpoints per camera instance
    sed -i 's/port = 10000/port = 10000/' ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_csi.conf
    sed -i 's/rtsp:\/\/127.0.0.1:8554\/stream0/rtsp:\/\/127.0.0.1:8554\/csi/' ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_csi.conf

    sed -i 's/port = 10000/port = 10001/' ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_webcam1.conf
    sed -i 's/rtsp:\/\/127.0.0.1:8554\/stream0/rtsp:\/\/127.0.0.1:8554\/webcam1/' ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_webcam1.conf

    sed -i 's/port = 10000/port = 10002/' ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_webcam2.conf
    sed -i 's/rtsp:\/\/127.0.0.1:8554\/stream0/rtsp:\/\/127.0.0.1:8554\/webcam2/' ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_webcam2.conf

    sed -i 's/port = 10000/port = 10003/' ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_webcam3.conf
    sed -i 's/rtsp:\/\/127.0.0.1:8554\/stream0/rtsp:\/\/127.0.0.1:8554\/webcam3/' ${D}${sysconfdir}/onvif_simple_server/onvif_simple_server_webcam3.conf

    # Install Environment File
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/onvif-simple-server.default ${D}${sysconfdir}/default/onvif-simple-server

    # Install Systemd Service Template
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/onvif-simple-server@.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += " \
    ${sysconfdir}/onvif_simple_server/* \
    ${sysconfdir}/default/onvif-simple-server \
    ${systemd_system_unitdir}/onvif-simple-server@.service \
"