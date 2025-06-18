FROM node:22-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm install -g @angular/cli@19
RUN npm ci

COPY . .

RUN npx ng test --browsers=ChromeHeadless --watch=false --code-coverage

RUN npx ng build --configuration=production

FROM nginx:alpine

RUN rm /etc/nginx/conf.d/default.conf

COPY nginx.conf /etc/nginx/conf.d/

COPY --from=build /app/dist/credit-challenge-webapp/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]