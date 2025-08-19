package controlador;

import java.awt.BorderLayout;
import modelo.ModeloVistaPrincipal;
import vista.VistaMetodoGrafico;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ControladorVistaPrincipal implements MouseListener{
    ModeloVistaPrincipal modelo;

    public ControladorVistaPrincipal(ModeloVistaPrincipal modelo) {
        this.modelo = modelo;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        cambiarPanel(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    private void mostrarPanel(JPanel p) {
        p.setSize(1040, 720);
        p.setLocation(0, 0);

        modelo.getVista().contenedor.removeAll();
        modelo.getVista().contenedor.add(p, BorderLayout.CENTER);
        modelo.getVista().contenedor.revalidate();
        modelo.getVista().contenedor.repaint();
    }

    public void cambiarPanel(MouseEvent e){
        if(e.getComponent().equals(modelo.getVista().btnMetodoGrafico)){
            VistaMetodoGrafico vista = new VistaMetodoGrafico();
            mostrarPanel(vista);
        }
    }
}
