import { Station } from "../../bicycle-stations/interfaces/station";

export interface BicycleJourneyAndStations {
  id: number;
  departureStation: Station | undefined;
  returnStation: Station | undefined;
  coveredDistance: number;
  journeyDuration: number;
}