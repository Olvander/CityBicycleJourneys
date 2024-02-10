import { Component, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { BicycleStationService } from '../service/bicycle-station.service';
import { BicycleStationsResponse } from
    '../interfaces/bicycle-stations-response';

import { MatPaginatorModule } from '@angular/material/paginator';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatPaginatorModule,
  ],
  selector: 'station-list',
  templateUrl: './station-list.component.html',
  styleUrl: './station-list.component.css'
})
export class StationListComponent implements OnInit {
  stationResponse?: BicycleStationsResponse;
  fetching: boolean;
  fetched: boolean;

  constructor(private stationSvc: BicycleStationService) {
    this.fetching = false;
    this.fetched = false;
  }

  ngOnInit(): void {
    
    if (!this.fetching && !this.fetched) {
      this.fetchStations();
    }
  }

  async fetchStations(): Promise<BicycleStationsResponse | void> {
    this.fetching = true;
    
    this.stationSvc.getStationsPage(0, stations => {
      this.stationResponse = stations;
      this.fetched = true;
      
      return stations;
    });
  }
  
  pgChg(event: PageEvent): void {
    
    this.stationSvc.getStationsPage(event.pageIndex, stations => {
      this.stationResponse = stations;
    });
  }
}