check:
	php --version && node --version && npm --version && npx --version && symfony -V && docker --version && docker compose version

install:
	cd api && docker compose up -d && composer install && bin/console doctrine:schema:update -f && cd ../frontend && npm install

up:
	make db-up && (make api-up & make frontend-up & make api-queue-up) > /dev/null 2>&1

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

dev-db:
	cd api && bin/console doctrine:schema:update --force -n

prod-build:
	./build.sh