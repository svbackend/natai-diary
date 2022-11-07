install:
	cd api && docker compose up -d && bin/console doctrine:migr:migr && composer install

up:
	cd api && docker compose up -d && symfony serve