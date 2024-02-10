import { Injectable } from '@angular/core';
import { BicycleJourney } from '../interfaces/bicycle-journey';
import { BicycleJourneyResponse } from '../interfaces/bicycle-journey-response';
import { HttpClient } from '@angular/common/http';
import { BicycleStationService } from
    '../../bicycle-stations/service/bicycle-station.service';
import { NumberHelperService } from
  '../../other services/number-helper.service';

@Injectable({
  providedIn: 'root',
})
export class BicycleJourneyService {
  journeys: BicycleJourney[];
  count: number;
  allStationsFetched: boolean;
  url: string;
  selectedMonths: number[];

  constructor(private http: HttpClient, private bicycleStationService:
      BicycleStationService, private numberHelperService: NumberHelperService) {

    this.journeys = [];
    this.allStationsFetched = false;
    this.url = "";
    this.count = 0;
    this.selectedMonths = [];
  }
    
  getJourneys(): BicycleJourney[] {
    return this.journeys;
  }

  getCount(response: (count: number) => void ): void {

    const countUrl: string = "http://localhost:8080/api/journeysCount/";

    this.http.get<number>(countUrl).subscribe(count => {
        this.count = count;
        response(count);
    });
  }

  returnBicycleJourneyResponse(slicedJourneys: BicycleJourney[],
      callback:(response: BicycleJourneyResponse) => void) : void {

    let resp: BicycleJourneyResponse = {count: 0, bicycleJourneys: []};
    
    this.getCount(count => {
    
      resp.count = count;
      
      slicedJourneys.forEach(journey => {
        
        let s1 = this.bicycleStationService.getStationByStationId(
            journey.departureStationId);

        let s2 = this.bicycleStationService.getStationByStationId(
            journey.returnStationId);
            
        resp.bicycleJourneys.push(
          {
            id: journey.id,
              departureStation: s1,
              returnStation: s2,
              coveredDistance: journey.coveredDistance,
              journeyDuration: journey.journeyDuration
          }
        );
      });

      callback(resp);
    });
  }

  getJourneysFromBackend(journeysUrl: string, selectedMonths: number[],
      response: (fetched: boolean) => void): void {

    this.selectedMonths = selectedMonths;
    this.http.get<BicycleJourney[]>(journeysUrl, {
        params: {selectedMonths: selectedMonths}}).subscribe(journeysResp => {

      this.journeys = journeysResp;
      
      response(true);
    });
  }

  getSlicedJourneys(pageNo: number, pageSize: number): BicycleJourney[] {
    
    pageNo = this.setPageNoToMaxIfItExceedsThePageLimit(pageNo, pageSize);
    
    let slicedJourneys: BicycleJourney[] = this.journeys.slice(
        (pageNo * pageSize), (pageNo * pageSize) + pageSize);

    return slicedJourneys;
  }

  setPageNoToMaxIfItExceedsThePageLimit(pageNo: number, pageSize: number)
      : number {

    if (pageNo * pageSize > this.journeys.length) {  
      pageNo = Math.floor(this.journeys.length / pageSize);
    }

    return pageNo;
  }

  sliceAndReturnJourneys(pageNo: number, pageSize: number, callback:
    (resp: BicycleJourneyResponse) => void ): void {

  let slicedJourneys: BicycleJourney[] = this.getSlicedJourneys(pageNo,
      pageSize);

  this.returnBicycleJourneyResponse(slicedJourneys, response => {
    
    let resp: BicycleJourneyResponse = response;

    if (resp.bicycleJourneys.length === slicedJourneys.length) {

      callback(resp);
    }
  });
}

  getJourneysPage(pageSize: number, pageNo: number, url: string, selectedMonths:
      number[], callback: (resp: BicycleJourneyResponse) => void): void {

    if (selectedMonths.length === 0) {
      selectedMonths = [5, 6, 7];
    }
    const journeysUrl: string = `http://localhost:8080/api/journeys/${url}`;
    
    if (journeysUrl !== this.url || this.journeys.length === 0 || 
        !this.numberHelperService.haveSameValues(selectedMonths,
        this.selectedMonths)) {
      
      this.url = journeysUrl;
      
      this.getJourneysFromBackend(journeysUrl, selectedMonths, fetched => {
        this.sliceAndReturnJourneys(pageNo, pageSize, callback);
      });
    
    } else {
      
      this.url = journeysUrl;
      this.sliceAndReturnJourneys(pageNo, pageSize, callback);
    }
  }
}