import { Injectable, OnInit } from '@angular/core';
import { Station } from '../interfaces/station';
import { HttpClient } from '@angular/common/http';
import { BicycleStationsResponse } from
    '../interfaces/bicycle-stations-response';

@Injectable({
  providedIn: 'root'
})
export class BicycleStationService implements OnInit {
  stations?: Station[];
  stationsUrl: string = "http://localhost:8080/api/stations/";

  constructor(private http: HttpClient) {}

  ngOnInit(): void {

    this.getAllStations(fetched => {
    });
  }

  getStationsPage(pgNo: number, callback:
      (stationsResponse: BicycleStationsResponse) => void): void {

    let pgSize: number = 25;
    let start: number = ((pgNo + 1) * pgSize) - pgSize;
    let end: number = start + pgSize;

    this.http.get<Station[]>(this.stationsUrl).subscribe(stationsResp => {
      this.stations = stationsResp;
      
      callback({
        count: this.stations!.length,
        stations: this.stations!.slice(start, end)
      });
    });
  }

  getAllStations(callback: (fetched: boolean) => void): void {
    this.http.get<Station[]>(this.stationsUrl).subscribe(stations => {
      this.stations = stations;
      
      callback(true);
    });
  }

  getStation(id: number, callback: (station: Station) => void): void {
    let url: string = `${this.stationsUrl}${id}/`;
    
    this.http.get<Station>(url).subscribe(station => {
      
      callback(station);
    });
  }


  getStationByStationId(id: string): Station {
    let s: Station;

    if (this.stations !== undefined || this.stations!.length === 0) {

      for (let station of this.stations!) {

        if (station.stationId === id) {
          s = station;
          break;
        }
      }
    }
    
    return s!;
  }

  getTotalJourneysStartingFromStation(id: number, months: number[],
      callback: (total: number) => void): void {

    let url: string = `${this.stationsUrl}${id}/totalJourneysFrom/`;

    this.http.get<number>(url, {params: {selectedMonths: months}})
        .subscribe(total => {
    
      callback(total);
    });
  }
  getTotalJourneysEndingAtStation(id: number, months: number[],
      callback: (total: number) => void): void {

    let url: string = `${this.stationsUrl}${id}/totalJourneysTo/`;
    
    this.http.get<number>(url, {params: {selectedMonths: months}})
        .subscribe(total => {

      callback(total);
    });
  }

  getAverageDistanceFrom(id: number, months: number[],
      callback: (avg: number) => void): void {
    
    let url: string = `${this.stationsUrl}${id}/averageDistanceFrom/`;
    
    this.http.get<number>(url, {params: {selectedMonths: months}})
       .subscribe(avg => {

      callback(avg);
    });
  }

  getAverageDistanceTo(id: number, months: number[],
      callback: (avg: number) => void): void {

    let url: string = `${this.stationsUrl}${id}/averageDistanceTo/`;
    
    this.http.get<number>(url, {params: {selectedMonths: months}})
        .subscribe(avg => {
    
      callback(avg);
    });
  }

  getMostPopularReturnStationsStartingFrom(id: number, months: number[],
      callback: (ids: Station[]) => void): void {

    let url: string =
        `${this.stationsUrl}${id}/top5ReturnStationsStartingFrom/`;
    
    this.http.get<Station[]>(url, {params: {selectedMonths: months}})
        .subscribe(stations => {
    
      callback(stations);
    });
  }

  getMostPopularDepartureStationsEndingAt(id: number, months: number[],
      callback: (ids: Station[]) => void): void {

    let url: string = `${this.stationsUrl}${id}/top5DepartureStationsEndingAt/`;
    
    this.http.get<Station[]>(url, {params: {selectedMonths: months}})
        .subscribe(stations => {
    
      callback(stations);
    });
  }
}