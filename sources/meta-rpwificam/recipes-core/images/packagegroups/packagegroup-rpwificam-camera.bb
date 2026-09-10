SUMMARY = "RPWiFiCam camera and streaming support"
LICENSE = "MIT"

PREFERRED_VERSION_ffmpeg = "7.0.2"

inherit packagegroup

RDEPENDS:${PN} = " \
    v4l-utils \
    ffmpeg \
    libcamera \
    libcamera-apps \
    mediamtx \
    onvif-simple-server \   
    ffmpeg-service \ 
"
#ffmpeg-service