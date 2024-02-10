export interface BicycleJourney {
  id: number;
  departureStationId: string;
  returnStationId: string;
  coveredDistance: number;
  journeyDuration: number;
}