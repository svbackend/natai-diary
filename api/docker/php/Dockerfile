FROM php:8.1-fpm-alpine3.17

RUN apk add --no-cache postgresql-dev \
    && docker-php-ext-configure pgsql -with-pgsql=/usr/local/pgsql \
    && docker-php-ext-install pdo_pgsql opcache

RUN apk add --no-cache \
    zip unzip libzip-dev php-zip freetype libpng \
    libjpeg-turbo freetype-dev libpng-dev libjpeg-turbo-dev && \
  docker-php-ext-configure gd --with-freetype --with-jpeg && \
  docker-php-ext-configure zip && \
  NPROC=$(grep -c ^processor /proc/cpuinfo 2>/dev/null || 1) && \
  docker-php-ext-install -j${NPROC} gd && \
  docker-php-ext-install zip && \
  docker-php-ext-install exif && \
  apk del --no-cache freetype-dev libpng-dev libjpeg-turbo-dev libzip-dev openssl-dev

RUN mv $PHP_INI_DIR/php.ini-production $PHP_INI_DIR/php.ini

WORKDIR /app

ENV COMPOSER_ALLOW_SUPERUSER 1

RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/bin --filename=composer --quiet \
    && rm -rf /root/.composer/cache

COPY /docker/php/conf.d /usr/local/etc/php/conf.d
COPY ./ ./

RUN chown www-data:www-data ./var -R