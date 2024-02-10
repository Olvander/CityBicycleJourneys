import { Component, AfterContentChecked, OnInit } from '@angular/core';
import { BicycleJourneyService } from '../service/bicycle-journey.service';
import { BicycleJourneyResponse } from '../interfaces/bicycle-journey-response';
import { PageEvent } from '@angular/material/paginator';
import { BicycleStationService } from
    '../../bicycle-stations/service/bicycle-station.service';
import { MatProgressSpinnerModule, ProgressSpinnerMode } from
    '@angular/material/progress-spinner';
import { NumberHelperService } from
    '../../other services/number-helper.service';
import { MonthSelectionMenuComponent } from
    '../../month-selection-menu/month-selection-menu.component';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatPaginatorModule } from '@angular/material/paginator';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MonthSelectionMenuComponent,
  ],
  selector: 'journey-list-view',
  templateUrl: './journey-list.component.html',
  styleUrl: './journey-list.component.css'
})
export class JourneyListComponent implements AfterContentChecked, OnInit {
  title: string = 'City Bicycle Journeys';
  spinnerMode: ProgressSpinnerMode = "indeterminate";
  spinnerWidth: string = "3";
  spinnerDiameter: string = "40";
  journeys?: any[] = [];
  bicycleJourneyResponse?: BicycleJourneyResponse;

  departureDescending: boolean;
  returnDescending: boolean;
  distanceDescending: boolean;
  durationDescending: boolean;

  fetchedJourneys: boolean;
  fetchedStations: boolean;
  startedFetching: boolean;

  sortDirections: string[] = ['', '', '', ''];
  sortDirectionsHover: string[] = ['', '', '', ''];
  sortedColumn: number = 0;
  
  pageSize: number;
  pageNo: number;
  displaySpinner: boolean;
  
  monthsSelected: number[];
  url: string;

  constructor(private bicycleJourneyService: BicycleJourneyService,
      private stationService: BicycleStationService,
      private numberHelperSvc: NumberHelperService) {

    this.departureDescending = false;
    this.returnDescending = false;
    this.distanceDescending = false;
    this.durationDescending = false;
    this.pageSize = 30;
    this.pageNo = 0;
    this.fetchedJourneys = false;
    this.fetchedStations = false;
    this.startedFetching = false;
    this.displaySpinner = false;
    this.monthsSelected = [];
    this.url = "";
  }

  ngOnInit(): void {
    
    if (!this.startedFetching && !this.fetchedStations) {
      this.getBicycleJourneyResponse();
    }
  }

  ngAfterContentChecked(): void {
    this.showOrHideHeaderArrowsOnHover();
  }

  showOrHideHeaderArrowsOnHover(): void {
    
    if (document.getElementById("th1") !== null) {
      let th1: HTMLElement = document.getElementById("th1")!;
      let th2: HTMLElement = document.getElementById("th2")!;
      let th3: HTMLElement = document.getElementById("th3")!;
      let th4: HTMLElement = document.getElementById("th4")!;
      th1.onmouseenter = () => {
        this.sortDirectionsHover[0] = (this.sortedColumn !== 1)
            ? (this.departureDescending ? "⭡" : "⭣") : "";
      }
      th1.onmouseleave = () => {
        this.sortDirectionsHover[0] = "";
      }
      th2.onmouseenter = () => {
        this.sortDirectionsHover[1] = (this.sortedColumn !== 2)
            ? (this.returnDescending ? "⭡" : "⭣") : "";
      }
      th2.onmouseleave = () => {
        this.sortDirectionsHover[1] = "";
      }
      th3.onmouseenter = () => {
        this.sortDirectionsHover[2] = (this.sortedColumn !== 3)
            ? (this.distanceDescending ? "⭡" : "⭣") : "";
      }
      th3.onmouseleave = () => {
        this.sortDirectionsHover[2] = "";
      }
      th4.onmouseenter = () => {
        this.sortDirectionsHover[3] = (this.sortedColumn !== 4)
            ? (this.durationDescending ? "⭡" : "⭣") : "";
      }
      th4.onmouseleave = () => {
        this.sortDirectionsHover[3] = "";
      }
    }
  }

  getKilometers(distance: number): string {
    return this.numberHelperSvc.getKilometers(distance);
  }

  getMinutes(duration: number): string {
    return this.numberHelperSvc.getMinutes(duration);
  }

  ngDoCheck(): void {
    
    if (!this.startedFetching && !this.fetchedStations) {
      this.getBicycleJourneyResponse();
    }
  }

  setPageNoToMaxIfItExceedsPageLimit(journeysCount: number): void {
    
    if (this.pageNo * this.pageSize > journeysCount) {
      this.pageNo = Math.floor(journeysCount / this.pageSize);
    }
  }

  getBicycleJourneyResponse(): void {
    this.displaySpinner = true;
    this.startedFetching = true;
      
    this.stationService.getAllStations(fetched => {
      this.fetchedStations = true;
      this.bicycleJourneyService.getJourneysPage(this.pageSize, this.pageNo,
          this.url, this.monthsSelected, journeysResp => {

        if (this.url === "") {
          this.clearColumnSorting();
        }

        this.setPageNoToMaxIfItExceedsPageLimit(journeysResp.count);
        this.bicycleJourneyResponse = journeysResp;
        this.fetchedJourneys = true;
        this.displaySpinner = false;
        this.startedFetching = false;
      });
    });
  }
  
  monthSelected(selectedMonths: number[]): void {
    if (!this.numberHelperSvc.haveSameValues(this.monthsSelected,
        selectedMonths)) {
          
      this.monthsSelected = selectedMonths;
      this.getBicycleJourneyResponse();
    }
  }

  clearColumnSorting(): void {
    this.setSortDirectionsAsFalse();
    this.clearSortDirections();
    this.sortedColumn = 0;
  }

  clearSortDirections(): void {
    this.sortDirections = ['', '', '', ''];
  }

  setSortDirectionsAsFalse(): void {
    this.departureDescending = false;
    this.returnDescending = false;
    this.distanceDescending = false;
    this.durationDescending = false;
  }

  pgChg(event: PageEvent): void {
    this.displaySpinner = true;
    this.pageSize = event.pageSize;
    this.pageNo = event.pageIndex;
    let url: string = this.getUrlForPgChg();
    this.bicycleJourneyService.getJourneysPage(event.pageSize, event.pageIndex,
        url, this.monthsSelected, response => {
          
      this.bicycleJourneyResponse = response;
      this.displaySpinner = false;
    });
  }

  getUrlForPgChg(): string {

    if (this.sortedColumn === 1) {
      
      if (this.departureDescending) {
        return "departureDesc/";
      
      } else {
        return "departureAsc/";
      }
    
    } else if (this.sortedColumn === 2) {
      
      if (this.returnDescending) {
        return "returnDesc/";
      
      } else {
        return "returnAsc/";
      }
    
    } else if (this.sortedColumn === 3) {
      
      if (this.distanceDescending) {
        return "distanceDesc/";
      
      } else {
        return "distanceAsc/";
      }
    
    } else if (this.sortedColumn === 4) {
      
      if (this.durationDescending) {
        return "durationDesc/";
      
      } else {
        return "durationAsc/";
      }
    
    } else {
      return "";
    }
  }
    
  clearOtherArrowsThan(sortedColumn: number, direction: string): void {

    for (let i = 0; i < this.sortDirections.length; i++) {
      
      if (sortedColumn === i) {
        this.sortDirections[i] = direction;
      
      } else {
        this.sortDirections[i] = "";
      }
    }
  }

  setOtherSortDirectionsAsFalseThan(sortedColumn: number,
      sortDescending: boolean): void {
        
    this.departureDescending = sortedColumn === 1 ? sortDescending : false;
    this.returnDescending = sortedColumn === 2 ? sortDescending : false;
    this.distanceDescending = sortedColumn === 3 ? sortDescending : false;
    this.durationDescending = sortedColumn === 4 ? sortDescending : false;
  }

  sortBy(type: string): void {
    let descending = false;
    this.url = type;

    if (type === "departure") {
      this.sortedColumn = 1;
      this.departureDescending = !this.departureDescending; 
      this.url += this.departureDescending ? "Desc/" : "Asc/";
      descending = this.departureDescending;
      this.setOtherSortDirectionsAsFalseThan(this.sortedColumn,
      this.departureDescending);
    
    } else if (type === "return") {
      this.sortedColumn = 2;
      this.returnDescending = !this.returnDescending;
      this.url += this.returnDescending ? "Desc/" : "Asc/";
      descending = this.returnDescending;
      this.setOtherSortDirectionsAsFalseThan(this.sortedColumn,
        this.returnDescending);

    } else if (type === "distance") {
      this.sortedColumn = 3;
      this.distanceDescending = !this.distanceDescending;
      this.url += this.distanceDescending ? "Desc/" : "Asc/";
      descending = this.distanceDescending;
      this.setOtherSortDirectionsAsFalseThan(this.sortedColumn,
        this.distanceDescending);

    } else if (type === "duration") {
      this.sortedColumn = 4;
      this.durationDescending = !this.durationDescending;
      this.url += this.durationDescending ? "Desc/" : "Asc/";
      descending = this.durationDescending;
      this.setOtherSortDirectionsAsFalseThan(this.sortedColumn,
        this.durationDescending);
    }

    this.clearOtherArrowsThan((this.sortedColumn - 1), descending ? "⭣"
        : "⭡");

    this.displaySpinner = true;
    this.bicycleJourneyService.getJourneysPage(this.pageSize, this.pageNo,
        this.url, this.monthsSelected, journeysResponse => {
    
      this.bicycleJourneyResponse = journeysResponse;
      this.displaySpinner = false;
    });
  }
}