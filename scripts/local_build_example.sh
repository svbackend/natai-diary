#!/bin/bash

mkdir "/tmp/natai" -p

rf -rf "/tmp/natai/*"

docker compose build api-php-fpm frontend

docker save natai/api-php-fpm > /tmp/natai/api-php-fpm.tar
docker save natai/frontend > /tmp/natai/frontend.tar

rsync -avzh /tmp/natai/api-php-fpm.tar root@adspons:/home/deploy/natai/docker/

rsync -avzh /tmp/natai/frontend.tar root@adspons:/home/deploy/natai/docker/