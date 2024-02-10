import { Routes } from '@angular/router';
import { PageNotFoundComponent } from
    './page-not-found/page-not-found.component';

export const routes: Routes = [
  {path: '**', redirectTo: 'not-found'},
  {path: 'not-found', component: PageNotFoundComponent}
];