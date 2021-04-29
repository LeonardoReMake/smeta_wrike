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



docker run -d -p 80:80  -p 443:443 --name nginx --restart=always \
-v /etc/nginx/conf.d  \
  -v /etc/nginx/vhost.d \
  -v /usr/share/nginx/html \
  -v /nginx/certs:/etc/nginx/certs:ro \
  -v /nginx/templates:/etc/docker-gen/templates:rw \
-v /var/run/docker.sock:/tmp/docker.sock:ro \
 --net=smeta-network  jwilder/nginx-proxy


docker run -d \
  --name nginx-gen \
  --net=smeta-network \
  --volumes-from nginx \
  -v /nginx/templates:/etc/docker-gen/templates:ro \
  -v /var/run/docker.sock:/tmp/docker.sock:ro \
  --label com.github.jrcs.letsencrypt_nginx_proxy_companion.docker_gen \
  jwilder/docker-gen \
  -notify-sighup nginx -watch -wait 5s:30s /etc/docker-gen/templates/nginx.tmpl /etc/nginx/conf.d/default.conf

docker run -d \
  --name nginx-letsencrypt \
  --volumes-from nginx \
  -v /nginx/certs:/etc/nginx/certs:rw \
  -v /var/run/docker.sock:/var/run/docker.sock:ro \
  jrcs/letsencrypt-nginx-proxy-companion