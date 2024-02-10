import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NumberHelperService {

  constructor() {}

  haveSameValues(a: number[], b: number[]): boolean {
    
    if (a.length === b.length) {
      
      for (let i = 0; i < a.length; i++) {
        
        if (a[i] !== b[i]) {
          
          return false;
        }
      }
    
    } else {
      
      return false;
    }
    
    return true;
  }

  getMinutes(seconds: number): string {
    let mins: number = seconds / 60;
    let minutes: string = mins.toFixed(1);
    
    return minutes;
  }

  getKilometers(meters: number): string {
    let kms: number = meters / 1000;
    let kilometers: string = kms.toFixed(2);
    
    return kilometers;
  }
}
