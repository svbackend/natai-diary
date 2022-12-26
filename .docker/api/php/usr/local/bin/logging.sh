#!/bin/sh

mkfifo /var/log/stdout
mkfifo /var/log/stderr

chmod 0666 /var/log/stdout
chmod 0666 /var/log/stderr

tail -f /var/log/stdout > /dev/stdout &
tail -f /var/log/stderr > /dev/stderr &
