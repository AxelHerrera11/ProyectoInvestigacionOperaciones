package modelo;

import vista.VistaPrincipal;

public class ModeloVistaPrincipal {
    private VistaPrincipal vista;

    public ModeloVistaPrincipal(VistaPrincipal vista) {
        this.vista = vista;
    }

    public VistaPrincipal getVista() {
        return vista;
    }

    public void setVista(VistaPrincipal vista) {
        this.vista = vista;
    }
}
