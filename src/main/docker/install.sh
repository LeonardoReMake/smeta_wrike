#!/usr/bin/env bash
docker network create smeta-network
docker run --name smeta-postgres -e POSTGRES_PASSWORD=Coh0Zie4 --restart=always -d --net=smeta-network postgres
docker run -d --restart=always --name smeta-web  --net=smeta-network -e "VIRTUAL_HOST=smeta.simplex-software.ru" -e "LETSENCRYPT_HOST=smeta.simplex-software.ru" -e "LETSENCRYPT_EMAIL=info@simplex-software.ru" wwwsimplexsoftwareru/smeta-web:v0.1


#!/usr/bin/env bash

or
docker run -d -p 80:80 --name nginx --restart=always -v /var/run/docker.sock:/tmp/docker.sock:ro --net=smeta-network  jwilder/nginx-proxy
docker network connect smeta-network nginx

#!/usr/bin/env bash
docker build -t wwwsimplexsoftwareru/smeta-web:v0.1 .




#!/usr/bin/env bash
docker logs -f smeta-web


docker run -d --name nginx-gen --volumes-from nginx  -v /root/nginx.tmpl:/etc/docker-gen/templates/nginx.tmpl:ro -v /var/run/docker.sock:/tmp/docker.sock:ro jwilder/docker-gen  -notify-sighup nginx -watch -wait 5s:30s /etc/docker-gen/templates/nginx.tmpl /etc/nginx/conf.d/default.conf

docker run -d --restart=always --name school-web  --net=school-network -e "VIRTUAL_HOST=school.simplex-software.ru" -e "LETSENCRYPT_HOST=school.simplex-software.ru" -e "LETSENCRYPT_EMAIL=info@simplex-software.ru" wwwsimplexsoftwareru/school-web:v0.22