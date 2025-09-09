package controlador;

import java.awt.BorderLayout;
import java.awt.Color;
import modelo.ModeloVistaPrincipal;
import vista.VistaMetodoGrafico;
import vista.VistaMetodoSimplex;

import javax.swing.*;
import java.awt.event.*;
import vista.VistaMetodoHola;

public class ControladorVistaPrincipal implements MouseListener, MouseMotionListener, WindowListener {

    ModeloVistaPrincipal modelo;

    // Colores principales
    Color fondoGeneral = new Color(0xF4, 0xF6, 0xF8); // #F4F6F8
    Color fondoCabecera = new Color(0x1B, 0x26, 0x3B); // #1B263B
    Color azulAcento = new Color(0x15, 0x65, 0xC0); // #1565C0
    Color azulHover = new Color(0x0D, 0x47, 0xA1); // #0D47A1
    Color doradoElegante = new Color(0xD4, 0xAF, 0x37); // #D4AF37
    Color doradoHover = new Color(0xB3, 0x8F, 0x2B); // #B38F2B
    Color grisCarbon = new Color(0x21, 0x25, 0x29); // #212529
    Color grisMedio = new Color(0x6C, 0x75, 0x7D); // #6C757D
    Color tablaEncabezado = new Color(0xDE, 0xE2, 0xE6); // #DEE2E6
    Color tablaFilaPar = new Color(0xF8, 0xF9, 0xFA); // #F8F9FA
    Color rojoPrincipal = new Color(0xE6, 0x39, 0x46); // normal
    Color rojoHover     = new Color(0xD6, 0x28, 0x28); // hover
    Color rojoPressed   = new Color(0xB7, 0x1C, 0x1C); // pressed
    Color textoBlanco   = new Color(0xFF, 0xFF, 0xFF); // texto sobre rojo
    Color grisOscuroHex = new Color(0x33, 0x33, 0x33);

    public ControladorVistaPrincipal(ModeloVistaPrincipal modelo) {
        this.modelo = modelo;
    }

    private JPanel btnCerrarPrograma;
    private JPanel btnMetodoGrafico;
    private JPanel btnMetodoSimplex;
    private JPanel btnMetodoHola;
    private int xMouse;
    private int yMouse;

    @Override
    public void mouseClicked(MouseEvent e) {
        cambiarPanel(e);
        if (e.getComponent().equals(btnCerrarPrograma)) {
            System.exit(0);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getComponent().equals(modelo.getVista().barraMovimiento)) {
            xMouse = e.getX();
            yMouse = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        colorFondoPanel(btnCerrarPrograma, rojoPrincipal, e);
        colorFondoPanel(btnMetodoGrafico, azulHover, e);
        colorFondoPanel(btnMetodoSimplex, azulHover, e);
        colorFondoPanel(btnMetodoHola, azulHover, e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        colorFondoPanel(btnCerrarPrograma, grisOscuroHex, e);
        colorFondoPanel(btnMetodoGrafico, azulAcento, e);
        colorFondoPanel(btnMetodoSimplex, azulAcento, e);
        colorFondoPanel(btnMetodoHola, azulAcento, e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getComponent().equals(modelo.getVista().barraMovimiento)) {
            int x = e.getXOnScreen();
            int y = e.getYOnScreen();
            modelo.getVista().setLocation(x - xMouse, y - yMouse);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    private void mostrarPanel(JPanel p) {
        p.setSize(1040, 720);
        p.setLocation(0, 0);

        modelo.getVista().contenedor.removeAll();
        modelo.getVista().contenedor.add(p, BorderLayout.CENTER);
        modelo.getVista().contenedor.revalidate();
        modelo.getVista().contenedor.repaint();
    }

    public void cambiarPanel(MouseEvent e) {
        if (e.getComponent().equals(btnMetodoGrafico)) {
            VistaMetodoGrafico vista = new VistaMetodoGrafico();
            mostrarPanel(vista);
        } else if (e.getComponent().equals(btnMetodoSimplex)) {
            VistaMetodoSimplex vista = new VistaMetodoSimplex();
            mostrarPanel(vista);
        } else if (e.getComponent().equals(btnMetodoHola)) {
            VistaMetodoHola vista = new VistaMetodoHola();
            mostrarPanel(vista);
        }
    }

    private void colorFondoPanel(JPanel panel, Color color, MouseEvent e) {
        if(e.getComponent().equals(panel)){
            panel.setBackground(color);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        btnCerrarPrograma = modelo.getVista().btnCerrarPrograma;
        btnMetodoGrafico = modelo.getVista().btnMetodoGrafico;
        btnMetodoSimplex = modelo.getVista().btnMetodoSimplex;
        btnMetodoHola = modelo.getVista().btnMetodoHola;
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
