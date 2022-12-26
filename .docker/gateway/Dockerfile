FROM nginx:1.23-alpine

COPY ./common/nginx/snippets /etc/nginx/snippets
COPY ./production/nginx/conf.d /etc/nginx/conf.d

WORKDIR /app