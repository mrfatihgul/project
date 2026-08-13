FROM node:22-alpine AS deps
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

FROM node:22-alpine AS builder
WORKDIR /app

COPY --from=deps /app/node_modules ./node_modules
COPY . .

ARG API_PROXY_TARGET=http://localhost:8082
ENV API_PROXY_TARGET=$API_PROXY_TARGET

RUN npm run build

FROM node:22-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
ARG API_PROXY_TARGET=http://localhost:8082
ENV API_PROXY_TARGET=$API_PROXY_TARGET

COPY --from=builder /app/package.json ./
COPY --from=builder /app/package-lock.json ./
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public
COPY --from=builder /app/next.config.ts ./

EXPOSE 3000
CMD ["npm", "run", "start", "--", "-H", "0.0.0.0", "-p", "3000"]

FROM builder AS test

CMD ["npm", "test", "--", "--run"]

FROM mcr.microsoft.com/playwright:v1.62.1-noble AS e2e

WORKDIR /app

COPY --from=builder /app/node_modules ./node_modules
COPY . .

CMD ["npx", "playwright", "test"]
