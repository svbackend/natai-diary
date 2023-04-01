check:
	php --version && node --version && npm --version && npx --version && symfony -V && docker --version && docker compose version

install:
	cd api && docker compose up -d && composer install && bin/console doctrine:schema:update -f && cd ../frontend && npm install

up:
	make db-up && (make api-up & make frontend-up & make api-queue-up)

db-up:
	cd api && docker compose up -d

api-up:
	cd api && symfony serve

api-queue-up:
	cd api && bin/console messenger:consume async

frontend-up:
	cd frontend && npm run dev

frontend-build:
	cd frontend && npm run build

test:
	cd api && bin/phpunit

tunnel-api:
	ngrok http 8000

tunnel-frontend:
	ngrok http 3000

frontend-schema:
	cd frontend && npx openapi-codegen gen api

dev-db-migration:
	cd api && bin/console make:migration

dev-db-schema:
	cd api && bin/console doctrine:schema:update --force -n

dev-db-reset:
	cd api && bin/console messenger:stop-workers
	cd api && bin/console doctrine:database:drop --force -n && bin/console doctrine:database:create -n && bin/console d:m:m -n

prod-build:
	./build.sh

# requires to change ./api/.env.local (DATABASE_URL) and ./frontend/.env.local (NEXT_PUBLIC_API_URL)
# Site will be available on http://172.22.0.7:80 (This ip can be found http://localhost:8080/dashboard/#/http/services/gateway-natai@docker)
prod-up:
	docker compose -f docker-compose.local.yml up db api-php-fpm -d
	docker compose -f docker-compose.local.yml exec api-php-fpm php bin/console doctrine:migrations:migrate -n
	docker compose -f docker-compose.local.yml up

prod-up-build:
	docker compose -f docker-compose.local.yml stop
	docker compose -f docker-compose.local.yml up --build --force-recreate
