FROM php:8.2-fpm

# Images
#RUN apt-get update && apt-get install -y \
#		libfreetype6-dev \
#		libjpeg62-turbo-dev \
#		libpng-dev \
#	&& docker-php-ext-configure gd --with-freetype --with-jpeg \
#	&& docker-php-ext-install -j$(nproc) gd

RUN apt-get update && apt-get install -y \
		libzip-dev \
	&& docker-php-ext-install -j$(nproc) zip \
    && docker-php-ext-enable zip \
    && docker-php-ext-install -j$(nproc) pdo_pgsql \
    && docker-php-ext-enable pdo_pgsql

# Permissions
RUN usermod -u 1000 www-data \
    && groupmod -g 1000 www-data \
    && chown -R www-data:www-data /srv/api \
    && chmod -R 777 /srv/api/var

# Composer
RUN curl -sS https://getcomposer.org/installer | php -- --install-dir=/usr/local/bin --filename=composer \
    && composer install --no-dev --optimize-autoloader

# Clean
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && rm -rf /tmp/*