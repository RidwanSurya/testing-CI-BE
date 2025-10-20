# Gunakan image Node.js untuk build aplikasi

FROM node:18-alpine AS build

# Set direktori kerja di container

WORKDIR /app

# Salin file package.json dan package-lock.json

COPY package*.json ./

# Install dependencies

RUN npm install

# Salin semua source code ke container

COPY . .

# Build project React

RUN npm run build

# Gunakan Nginx untuk menyajikan hasil build

FROM nginx:stable-alpine

# Salin hasil build React ke folder default Nginx

COPY --from=build /app/dist /usr/share/nginx/html

# Ekspos port 3000

EXPOSE 3000

# Jalankan Nginx

CMD ["nginx", "-g", "daemon off;"]