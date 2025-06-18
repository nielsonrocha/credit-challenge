FROM node:22-alpine AS dependencies

WORKDIR /app
COPY package*.json ./
RUN npm install -g @angular/cli@19
RUN npm ci

FROM dependencies AS test
RUN apk add --no-cache chromium
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true
COPY . .
RUN npx ng test --browsers=ChromeHeadless --watch=false --code-coverage

FROM dependencies AS build
COPY . .
RUN npx ng build --configuration=production

FROM nginx:alpine AS production
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/
COPY --from=build /app/dist/credit-challenge-webapp/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]