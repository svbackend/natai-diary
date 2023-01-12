## How to deploy to production

### Server requirements

* Debian / Ubuntu OS
* SSH access
* Docker & docker compose plugin
* https://github.com/svbackend/vps-traefik - Traefik reverse proxy needs to be up and running
* rsync
* Domain A record pointing to the server IP

### First time server setup

* `mkdir natai && mkdir natai/docker && mkdir natai/backup`
* `cd natai && git clone git@github.com:svbackend/natai-diary.git repo`
* `cd repo && cp .env.dist .env` (edit .env file)
* `cd api && cp .env .env.local` (edit .env.local file)

