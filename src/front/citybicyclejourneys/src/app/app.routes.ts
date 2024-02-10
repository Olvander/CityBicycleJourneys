import { Routes } from '@angular/router';
import { PageNotFoundComponent } from
    './page-not-found/page-not-found.component';
import { LicenseComponent } from './license/license.component';
import { StationComponent } from './bicycle-stations/station/station.component';
import { StationListComponent } from
    './bicycle-stations/station-list/station-list.component';

export const routes: Routes = [
  {path: '', redirectTo: 'journeys', pathMatch: 'full'},
  {path: 'license', component: LicenseComponent},
  {path: "stations", component: StationListComponent, pathMatch: 'full'},
  {path: "stations/:id", component: StationComponent, pathMatch: 'full'},
  {path: '**', redirectTo: 'not-found'},
  {path: 'not-found', component: PageNotFoundComponent}
];