package controlador;

import implementacion.MetodoSimplexImp;
import modelo.ModeloMetodoSimplex;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class ControladorMetodoSimplex implements MouseListener {
    ModeloMetodoSimplex modelo;

    MetodoSimplexImp implementacion = new MetodoSimplexImp();
    public ControladorMetodoSimplex(ModeloMetodoSimplex modelo) {
        this.modelo = modelo;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent().equals(modelo.getVista().btnCalcular)){
            resolverMetodoSimplexMaximizacion();
        }
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

    private void ajustarTamañoTabla(JTable tabla) {
        int rowHeight = tabla.getRowHeight();
        int rowCount = tabla.getRowCount();
        int headerHeight = tabla.getTableHeader().getPreferredSize().height;

        int alturaTotal = (rowHeight * rowCount) + headerHeight;
        tabla.setPreferredScrollableViewportSize(new Dimension(tabla.getPreferredSize().width, alturaTotal));
    }

    private void resolverMetodoSimplexMaximizacion(){
        String funcionObjetivo = modelo.getVista().txtZ.getText();
        String restricciones = modelo.getVista().txtRestricciones.getText();

        List<DefaultTableModel> tablas = implementacion.resolver(funcionObjetivo, restricciones);

        JTable tablaMetodoSimplex = new JTable(tablas.get(0));
        ajustarTamañoTabla(tablaMetodoSimplex);
        JScrollPane scrollMS = new JScrollPane(tablaMetodoSimplex);
        modelo.getVista().panelTablaFormaSimplex.add(scrollMS);
        modelo.getVista().panelTablaFormaSimplex.revalidate();
        modelo.getVista().panelTablaFormaSimplex.repaint();

        for (int i = 1; i < tablas.size(); i++) {
            JTable tablaPruebaOptimalidad = new JTable(tablas.get(i));
            ajustarTamañoTabla(tablaPruebaOptimalidad);
            JScrollPane scrollPO = new JScrollPane(tablaPruebaOptimalidad);
            modelo.getVista().panelTablaPruebaOptimalidad.add(scrollPO);
            modelo.getVista().panelTablaPruebaOptimalidad.revalidate();
            modelo.getVista().panelTablaPruebaOptimalidad.repaint();
        }
    }

}
