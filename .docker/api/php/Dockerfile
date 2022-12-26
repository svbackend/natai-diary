ARG PHP_VERSION=8.2

# PHP Base
FROM php:${PHP_VERSION}-fpm-alpine AS php

RUN set -eux; \
	apk add --no-cache --update \
	su-exec \
	; \
	apk add --virtual .build-deps \
	$PHPIZE_DEPS \
	icu-dev \
	libzip-dev \
	postgresql-dev \
	zlib-dev \
	libmcrypt-dev \
	; \
	docker-php-ext-configure zip; \
	docker-php-ext-install -j$(nproc) \
	intl \
	pdo_pgsql \
	zip \
	; \
	runDeps="$( \
	scanelf --needed --nobanner --format '%n#p' --recursive /usr/local/lib/php/extensions \
	| tr ',' '\n' \
	| sort -u \
	| awk 'system("[ -e /usr/local/lib/" $1 " ]") == 0 { next } { print "so:" $1 }' \
	)"; \
	apk add --virtual .api-phpexts-rundeps $runDeps; \
	apk del .build-deps; \
	rm -rf /var/cache/apk/*;

RUN set -ex; \
	php -r "copy('https://getcomposer.org/installer', '/tmp/composer-setup.php');"; \
	php /tmp/composer-setup.php --install-dir=/usr/local/bin --filename=composer; \
	php -r "unlink('/tmp/composer-setup.php');";

COPY usr/local/bin/ /usr/local/bin/
COPY usr/local/sbin/ /usr/local/sbin/
COPY usr/local/etc/php-fpm.d/ /usr/local/etc/php-fpm.d/
COPY usr/local/etc/php/conf.d/ /usr/local/etc/php/conf.d/

WORKDIR /app

RUN ls -lah /app
RUN ls -lah /srv

# RUN composer install --no-dev --no-autoloader --no-progress --optimize-autoloader

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["php-fpm"]