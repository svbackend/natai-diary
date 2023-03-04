#!/bin/bash

REPO="/home/deploy/natai/repo"
DOCKER_IMAGES="/home/deploy/natai/docker"
DB_BACKUP="/home/deploy/natai/backup/natai_db.sql"

cd $REPO && git pull origin main

cd $REPO && docker compose exec db bash -c "pg_dump -U natai_user -h localhost -d natai_db > /tmp/backup/natai_db.sql"

cd $REPO && docker system prune -af

cd $DOCKER_IMAGES && docker load < frontend.tar
cd $DOCKER_IMAGES && docker load < api-php-fpm.tar
cd $DOCKER_IMAGES && docker load < api-queue.tar

cd $REPO && docker compose exec api-php-fpm bin/console messenger:stop-workers > /dev/null 2>&1
cd $REPO && docker compose exec api-php-fpm composer install --no-dev --optimize-autoloader > /dev/null 2>&1
cd $REPO && docker compose exec api-php-fpm bin/console d:m:m -n > /dev/null 2>&1

cd $REPO && docker compose up -d --force-recreate

cd $REPO && docker compose exec api-php-fpm composer install --no-dev --optimize-autoloader
cd $REPO && docker compose exec api-php-fpm bin/console d:m:m -n