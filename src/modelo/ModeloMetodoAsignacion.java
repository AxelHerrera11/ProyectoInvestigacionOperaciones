package modelo;

import vista.VistaMetodoAsignacion;

public class ModeloMetodoAsignacion {
    VistaMetodoAsignacion vista;

    public ModeloMetodoAsignacion() {
    }

    public ModeloMetodoAsignacion(VistaMetodoAsignacion vista) {
        this.vista = vista;
    }

    public VistaMetodoAsignacion getVista() {
        return vista;
    }

    public void setVista(VistaMetodoAsignacion vista) {
        this.vista = vista;
    }
    
    
}
