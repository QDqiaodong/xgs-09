ARG DOCKER_REGISTRY=
FROM ${DOCKER_REGISTRY}node:20-alpine AS builder

WORKDIR /app

COPY .npmrc ./
COPY package*.json ./

RUN npm config set registry https://repo.huaweicloud.com/repository/npm/ && \
    npm ci --prefer-offline --no-audit --no-fund

COPY . .
RUN npm run build

ARG DOCKER_REGISTRY=
FROM ${DOCKER_REGISTRY}nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
