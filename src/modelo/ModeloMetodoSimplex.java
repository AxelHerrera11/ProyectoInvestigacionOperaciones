package modelo;

import vista.VistaMetodoSimplex;

public class ModeloMetodoSimplex {
    private VistaMetodoSimplex vista;

    public ModeloMetodoSimplex(VistaMetodoSimplex vista) {
        this.vista = vista;
    }

    public VistaMetodoSimplex getVista() {
        return vista;
    }

    public void setVista(VistaMetodoSimplex vista) {
        this.vista = vista;
    }
}
