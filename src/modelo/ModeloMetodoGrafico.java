/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import vista.VistaMetodoGrafico;

/**
 *
 * @author javie
 */
public class ModeloMetodoGrafico {
    VistaMetodoGrafico vistaMetodoGrafico;

    public ModeloMetodoGrafico() {
    }

    public ModeloMetodoGrafico(VistaMetodoGrafico vistaMetodoGrafico) {
        this.vistaMetodoGrafico = vistaMetodoGrafico;
    }
    

    public VistaMetodoGrafico getVistaMetodoGrafico() {
        return vistaMetodoGrafico;
    }

    public void setVistaMetodoGrafico(VistaMetodoGrafico vistaMetodoGrafico) {
        this.vistaMetodoGrafico = vistaMetodoGrafico;
    }
    
    
    
}
