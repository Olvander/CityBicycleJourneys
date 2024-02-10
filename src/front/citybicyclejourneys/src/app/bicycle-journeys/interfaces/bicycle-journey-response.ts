import { BicycleJourneyAndStations } from "./bicycle-journey-and-stations";

export interface BicycleJourneyResponse {
  count: number;
  bicycleJourneys: BicycleJourneyAndStations[];
}