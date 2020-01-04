FROM openjdk:8-alpine AS builder
# install npm
RUN apk add --update npm

WORKDIR /tmp
COPY package.json .
COPY package-lock.json .
COPY shadow-cljs.edn .
# install modules
RUN npm install
# build stuff
COPY src src
RUN npx shadow-cljs release app

# copy to nginx for a small image
FROM nginx:alpine
COPY public/index.html /usr/share/nginx/html/index.html
COPY resources/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /tmp/public/ /usr/share/nginx/html

# replace with port from heroku
CMD sed -i -e "s/HOSTPORT/$PORT/g" /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'
