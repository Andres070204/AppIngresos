package co.edu.unipiloto.ingreso.Clients;

public class Cargo {
    private String userName;
    private String description;
    private double weight;
    private double volume;
    private String origin;
    private String destination;

    private String driverId;
    private String truckId;

    public Cargo() {
        // Constructor vacío requerido por Firebase
    }

    public Cargo(String userName, String description, double weight, double volume, String destination, String origin, String driverId, String truckId) {
        this.userName = userName;
        this.description = description;
        this.weight = weight;
        this.volume = volume;
        this.destination = destination;
        this.origin = origin;
        this.driverId = driverId;
        this.truckId = truckId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }
}
