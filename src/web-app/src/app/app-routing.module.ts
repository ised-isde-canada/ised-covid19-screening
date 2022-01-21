import { CovidScreeningComponent } from './covid-screening/covid-screening.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: ':lang', component: CovidScreeningComponent },
  { path: '', redirectTo: '/en', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
