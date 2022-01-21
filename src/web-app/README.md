# Covid Screening Frontend

[![DEV Deployment](https://img.shields.io/badge/Access%20Application-DEV-gray?logo=angular&logoColor=red&style=for-the-badge&labelColor=blue)](https://master.d22xb5r394bpkg.amplifyapp.com/)
[![Prod Deployment](https://img.shields.io/badge/Access%20Application-Prod-gray?logo=angular&logoColor=red&style=for-the-badge&labelColor=blue)](https://c19s.ised-isde.canada.ca/)

[![DEV API](https://img.shields.io/badge/Access%20API-DEV-gray?logo=aws-amplify&logoColor=yellow&style=for-the-badge&labelColor=purple)](https://25pdaq309i.execute-api.ca-central-1.amazonaws.com/dev/covidscreening)
[![Prod API](https://img.shields.io/badge/Access%20API-Prod-gray?logo=aws-amplify&logoColor=yellow&style=for-the-badge&labelColor=purple)](https://j6qbziqrgc.execute-api.ca-central-1.amazonaws.com/prod/covidscreening)

## Compatibility

- This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 12.2.11.
- **Browsers supported:** IE11, Chrome, Firefox, Edge

## Run Dev Environment Using Docker

1. Run `docker-compose up`
2. Navigate to `http://localhost:4200/` in the browser

## Run Dev Environment Locally

1. Install angular cli using: `@angular/cli@12.2.12`
2. run `npm install`
3. run dev server `start:development`
4. Navigate to `http://localhost:4200/` in the browser

## Run Development Environment on Server

This will build the project using default settings and use `environment.ts` for env variables

1. Run `npm build:development` to build the project. The build artifacts will be stored in the `dist/` directory.
2. Run `npm start:development` to start the project.

## Run Production Environment on Server

This will build and optimize the project. It will use `environment.prod.ts` for env variables

1. Run `npm build:production` to build the project. The build artifacts will be stored in the `dist/` directory.
2. Run `npm start:production` to start the project.

## Helpful tools

## Linting

run `npm run lint` to clean up code styling and check or linting errors.

### Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

### Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

### Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice. To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.
