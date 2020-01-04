FROM openjdk:8-alpine AS builder
# install npm
RUN apk add --update npm

WORKDIR /tmp
COPY package.json .
COPY package-lock.json .
COPY shadow-cljs.edn .
COPY src src
# install modules
RUN npm install
# build stuff
RUN npx shadow-cljs release app

# copy to nginx for a small image
FROM nginx:alpine
COPY public/index.html /usr/share/nginx/html/index.html
COPY --from=builder /tmp/public/ /usr/share/nginx/html
