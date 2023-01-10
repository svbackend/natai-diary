#!/bin/bash

docker compose build api-php-fpm

docker save natai/api-php-fpm > /tmp/api-php-fpm.tar

rsync -avzh /tmp/api-php-fpm.tar root@adspons:/home/deploy/natai/docker/