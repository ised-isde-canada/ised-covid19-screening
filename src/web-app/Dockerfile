FROM node:16

WORKDIR /app-ui

COPY package*.json ./

# RUN npm install -g @angular/cli @angular-devkit/build-angular && npm install

RUN npm install -g @angular/cli@12.2.12 && npm install

COPY . .

EXPOSE 4200

CMD npm run docker:start