#!/bin/sh

images=keking/kkfileview:4.4.0

docker build -t $images . -f  Dockerfile

docker rm -f qt-fileview
docker run -d --restart=always --name qt-fileview -p 1107:8012 \
-e KK_FILE_DIR='/opt/kkFileView' \
-e KK_LOCAL_PREVIEW_DIR='/opt/kkFileView' \
-e WATERMARK_TXT='Quectel ST' \
-e KK_CONTEXT_PATH='/fileview' \
-e KK_BASE_URL='https://stres.quectel.com:8139/fileview' \
$images