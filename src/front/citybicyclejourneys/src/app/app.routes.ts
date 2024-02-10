import { Routes } from '@angular/router';
import { PageNotFoundComponent } from
    './page-not-found/page-not-found.component';
import { LicenseComponent } from './license/license.component';

export const routes: Routes = [
  {path: 'license', component: LicenseComponent},
  {path: '**', redirectTo: 'not-found'},
  {path: 'not-found', component: PageNotFoundComponent}
];