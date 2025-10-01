/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import vista.VistaMetodoTransporte;

/**
 *
 * @author gerso
 */
public class ModeloMetodoTransporte {
    private VistaMetodoTransporte vista;

    public ModeloMetodoTransporte(VistaMetodoTransporte vista) {
        this.vista = vista;
    }

    public VistaMetodoTransporte getVista() {
        return vista;
    }

    public void setVista(VistaMetodoTransporte vista) {
        this.vista = vista;
    }
}
