install:
	cd api && docker compose up -d && bin/console doctrine:migr:migr && composer install