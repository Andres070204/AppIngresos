package co.edu.unipiloto.ingreso.Proprietary;

public class Camion {
    public String placa;
    public String modelo;
    public String color;
    public String motor;
    public String propietarioId; // Nuevo campo para el ID del propietario

    public Camion(){

    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(String propietarioId) {
        this.propietarioId = propietarioId;
    }

    public Camion(String placa, String modelo, String color, String motor, String propietarioId) {
        this.placa = placa;
        this.modelo = modelo;
        this.color = color;
        this.motor = motor;
        this.propietarioId = propietarioId; // Asignar el ID del propietario
    }
}
