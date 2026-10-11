FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY server.js ./
COPY MotoApp ./MotoApp
ENV PORT=4000
EXPOSE 4000
CMD ["node", "server.js"]
