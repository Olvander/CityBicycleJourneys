package fi.tuni.olvander.citybicyclejourneys.stations;

public class Station {
    private int id;
    private String stationId;
    private String name;
    private String address;
    private double x;
    private double y;

    public Station() {}

    public Station(String stationId, String name, String address, double x,
                   double y) {

        this.stationId = stationId;
        this.name = name;
        this.address = address;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
