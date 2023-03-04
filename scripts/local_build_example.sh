#!/bin/bash

mkdir "/tmp/natai" -p

rf -rf "/tmp/natai/*"

docker compose build api-php-fpm api-queue frontend

docker save natai/api-php-fpm > /tmp/natai/api-php-fpm.tar
docker save natai/api-queue > /tmp/natai/api-queue.tar
docker save natai/frontend > /tmp/natai/frontend.tar

rsync -avzh /tmp/natai/api-php-fpm.tar user@server:/home/deploy/natai/docker/

rsync -avzh /tmp/natai/api-queue.tar user@server:/home/deploy/natai/docker/

rsync -avzh /tmp/natai/frontend.tar user@server:/home/deploy/natai/docker/