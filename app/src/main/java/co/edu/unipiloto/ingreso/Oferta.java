package co.edu.unipiloto.ingreso;

public class Oferta {
    private String conductor;
    private String camion;
    private String origen;
    private String destino;
    private String estado; // Nuevo campo para el estado de la oferta

    public Oferta() {
        // Constructor vacío requerido para Firebase
    }

    public Oferta(String conductor, String camion, String origen, String destino) {
        this.conductor = conductor;
        this.camion = camion;
        this.origen = origen;
        this.destino = destino;
        this.estado = "Pendiente"; // Estado inicial
    }

    // Getters y setters
    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getCamion() {
        return camion;
    }

    public void setCamion(String camion) {
        this.camion = camion;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Oferta{" +
                "conductor='" + conductor + '\'' +
                ", camion='" + camion + '\'' +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
