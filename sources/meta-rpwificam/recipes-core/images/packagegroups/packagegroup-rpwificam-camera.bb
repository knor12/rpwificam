SUMMARY = "RPWiFiCam camera and streaming support"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = " \
    v4l-utils \
    ffmpeg \
    libcamera \
    libcamera-apps \
    mediamtx \
"