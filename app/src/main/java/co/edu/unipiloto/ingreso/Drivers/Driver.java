package co.edu.unipiloto.ingreso.Drivers;

public class Driver {

    private String name;
    private String email;
    private String truckId; // Campo para almacenar el ID del camión asignado al conductor

    // Constructor vacío requerido por Firestore
    public Driver() {
    }

    public Driver(String name, String email, String truckId) {
        this.name = name;
        this.email = email;
        this.truckId = truckId;
    }

    // Métodos getter y setter para acceder a los campos privados

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }

    @Override
    public String toString() {
        return name;
    }
}
