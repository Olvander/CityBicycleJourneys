import { Component } from '@angular/core';
import { ActivatedRoute, Params, Router, RouterModule } from '@angular/router';
import { BicycleStationService } from '../service/bicycle-station.service';
import { Station } from '../interfaces/station';
import { MatProgressSpinnerModule, ProgressSpinnerMode } from
    '@angular/material/progress-spinner';
import { NumberHelperService } from
    '../../other services/number-helper.service';
import { MonthSelectionMenuComponent } from
    '../../month-selection-menu/month-selection-menu.component';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    MonthSelectionMenuComponent,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    RouterModule,
  ],
  selector: 'station',
  templateUrl: './station.component.html',
  styleUrl: './station.component.css'
})
export class StationComponent {
  station?: Station;
  totalJourneysFrom?: number;
  totalJourneysTo?: number;
  avgDistanceFrom?: number;
  avgDistanceTo?: number;
  top5ReturnStationsStartingFrom?: Station[];
  top5DepartureStationsEndingAt?: Station[];
  departuresLoadOrHide: string = "Load";
  returnsLoadOrHide: string = "Load";

  id?: number;
  spinnerMode: ProgressSpinnerMode = "indeterminate";
  spinnerWidth: string = "3";
  spinnerDiameter: string = "40";
  displaySpinner: boolean;
  displaySpinner2: boolean;
  displaySpinner3: boolean;
  monthsSelected: number[];

  constructor(private activatedRoute: ActivatedRoute, private router: Router,
      private stationSvc: BicycleStationService,
      private numberHelperSvc: NumberHelperService) {
        
    this.displaySpinner = false;
    this.displaySpinner2 = false;
    this.displaySpinner3 = false;
    this.monthsSelected = [5, 6, 7];

    this.activatedRoute.params.subscribe((params: Params) => {
      let idAsStr: string = String(params['id']);
      
      if (!idAsStr.match(/^[0-9]+$/)) {
        this.router.navigate(['not-found']);
      
      } else {
        this.id = params['id'];
        this.hideTop5Stations();
      }
    });
  }


  disableButton(btn1: boolean | undefined, btn2: boolean | undefined): void {

    let btnReturn: HTMLButtonElement = document.getElementById("btnReturn") as
        HTMLButtonElement;

    let btnDeparture: HTMLButtonElement =
        document.getElementById("btnDeparture") as HTMLButtonElement;
    
    if (btnReturn !== null) {
      
      if (btn1 !== undefined) {
        btnReturn.disabled = btn1;
      }
    }
    
    if (btnDeparture !== null) {
      
      if (btn2 !== undefined) {
        btnDeparture.disabled = btn2;
      }
    }
  }

  monthSelected(selectedMonths: number[]): void {
    
    if (this.monthsSelected.length === 0 || !this.numberHelperSvc
        .haveSameValues(this.monthsSelected, selectedMonths)) {

      this.monthsSelected = selectedMonths;

      if (this.monthsSelected.length === 0) {
        this.monthsSelected = [5, 6, 7];
      }
      this.getStationData();
      this.updateTop5StationsIfLoadedPreviously();
    }
  }

  hideTop5Stations(): void {
    this.returnsLoadOrHide = "Load";
    this.departuresLoadOrHide = "Load";
    this.top5ReturnStationsStartingFrom = undefined;
    this.top5DepartureStationsEndingAt = undefined;
  }

  ngAfterContentInit(): void {
    this.getStationData();
  }

  initJourneyVariables(): void {
    this.avgDistanceFrom = undefined;
    this.avgDistanceTo = undefined;
    this.totalJourneysFrom = undefined;
    this.totalJourneysTo = undefined;
  }
  
  stopSpinnerIfAllReady(): void {
    
    if (this.canDisplayTable()) {
      this.displaySpinner = false;
    }
  }
  
  getStationData(): void {

    this.activatedRoute.params.subscribe((params: Params) => {
      
      this.id = params['id'];
      this.displaySpinner = true;
      
      if (this.id! > 0) {
        this.initJourneyVariables();
        
        this.stationSvc.getStation(this.id!, station => {

          this.station = station;

          this.stationSvc.getTotalJourneysStartingFromStation(this.id!,
              this.monthsSelected, total => {
            
            this.totalJourneysFrom = total;
            this.stopSpinnerIfAllReady();
          });
          
          this.stationSvc.getTotalJourneysEndingAtStation(this.id!,
              this.monthsSelected, total => {
            
            this.totalJourneysTo = total;
            this.stopSpinnerIfAllReady();
          });
          
          this.stationSvc.getAverageDistanceFrom(this.id!, this.monthsSelected,
              avg => {
            
            this.avgDistanceFrom = avg;
            this.stopSpinnerIfAllReady();
          });
          
          this.stationSvc.getAverageDistanceTo(this.id!, this.monthsSelected,
              avg => {

            this.avgDistanceTo = avg;
            this.stopSpinnerIfAllReady();
          })
        });
      }
    });
  }

  canDisplayTable(): boolean {
    
    return (this.avgDistanceFrom !== undefined &&
        this.avgDistanceTo !== undefined &&
        this.totalJourneysFrom !== undefined &&
        this.totalJourneysTo !== undefined);
  }

  updateTop5StationsIfLoadedPreviously(): void {
    
    if (this.returnsLoadOrHide === "Hide") {
      this.disableButton(true, undefined);
      this.top5ReturnStationsStartingFrom = undefined;
      this.getMostPopularReturnStationsStartingFrom();
    }
    
    if (this.departuresLoadOrHide === "Hide") {
      this.disableButton(undefined, true);
      this.top5DepartureStationsEndingAt = undefined;
      this.getMostPopularDepartureStationsEndingAt();
    }
  }
  
  getMostPopularReturnStationsStartingFrom(): void {
    
    if (this.top5ReturnStationsStartingFrom !== undefined) {
      this.top5ReturnStationsStartingFrom = undefined;
      this.returnsLoadOrHide = "Load";
      this.disableButton(false, undefined);
    
    } else {
      this.disableButton(true, undefined);
      
      if (this.id! > 0) {
        
        if (this.stationSvc.stations === undefined) {
          this.stationSvc.getStationsPage(0, stations => {
            this.saveTop5ReturnStations();
          });
        
        } else {
          this.saveTop5ReturnStations();
        }
      }
    }
  }
  getMostPopularDepartureStationsEndingAt(): void {
    
    if (this.top5DepartureStationsEndingAt !== undefined) {
      this.top5DepartureStationsEndingAt = undefined;
      this.departuresLoadOrHide = "Load";
      this.disableButton(undefined, false);
    
    } else {
      this.disableButton(undefined, true);
      
      if (this.id! > 0) {
        
        if (this.stationSvc.stations === undefined) {
          this.stationSvc.getStationsPage(0, stations => {
            this.saveTop5DepartureStations();
          });
      
      } else {
          this.saveTop5DepartureStations();
        }
      }
    }
  }


  saveTop5ReturnStations(): void {
    this.displaySpinner2 = true;
    
    this.stationSvc.getMostPopularReturnStationsStartingFrom(this.id!,
        this.monthsSelected, stations => {
      
      if (this.top5ReturnStationsStartingFrom === undefined) {
        this.top5ReturnStationsStartingFrom = [];
        
        for (let station of stations) {
          this.top5ReturnStationsStartingFrom!.push(station);
        }
        
        this.displaySpinner2 = false;
        this.returnsLoadOrHide = "Hide";
        this.disableButton(false, undefined);
      }
    });
  }


  saveTop5DepartureStations(): void {
    this.displaySpinner3 = true;
    
    this.stationSvc.getMostPopularDepartureStationsEndingAt(this.id!,
        this.monthsSelected, stations => {
      
      if (this.top5DepartureStationsEndingAt === undefined) {
        this.top5DepartureStationsEndingAt = [];
        
        for (let station of stations) {
            this.top5DepartureStationsEndingAt!.push(station);
        }
        
        this.displaySpinner3 = false;
        this.departuresLoadOrHide = "Hide";
        this.disableButton(undefined, false);
      }
    });
  }
}
