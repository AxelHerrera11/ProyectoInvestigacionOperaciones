package controlador;

import implementacion.MetodoAsignacionImp;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import modelo.ModeloMetodoAsignacion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class ControladorMetodoAsignacion implements MouseListener {

    private final ModeloMetodoAsignacion modelo;
    private MetodoAsignacionImp implementacion;

    public ControladorMetodoAsignacion(ModeloMetodoAsignacion modelo) {
        this.modelo = modelo;
        this.implementacion = new MetodoAsignacionImp();

        // Registrar eventos
        modelo.getVista().btnGenerarTabla.addMouseListener(this);
        modelo.getVista().btnCalcular.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object src = e.getComponent();
        if (src.equals(modelo.getVista().btnGenerarTabla)) {
            generarTablaEntrada();
        } else if (src.equals(modelo.getVista().btnCalcular)) {
            ejecutarAsignacion();
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // ---------------- LÓGICA DE DATOS ---------------- //
    private void generarTablaEntrada() {
        try {
            int filas = Integer.parseInt(modelo.getVista().txtFilas.getText().trim());
            int columnas = Integer.parseInt(modelo.getVista().txtColumnas.getText().trim());

            if (filas <= 0 || columnas <= 0) {
                JOptionPane.showMessageDialog(modelo.getVista(), "Debe ingresar números mayores que cero.");
                return;
            }

            // Encabezados dinámicos
            String[] columnasHeader = new String[columnas + 1];
            columnasHeader[0] = " ";
            for (int i = 1; i <= columnas; i++) {
                columnasHeader[i] = "T" + i; // "T" de "Tarea"
            }

            DefaultTableModel model = new DefaultTableModel(columnasHeader, filas);
            JTable tabla = new JTable(model);

            // Nombres dinámicos de empleados
            for (int i = 0; i < filas; i++) {
                model.setValueAt("E" + (i + 1), i, 0);
            }

            // Mostrar tabla en la vista
            modelo.getVista().panelTabla.removeAll();
            modelo.getVista().panelTabla.add(new JScrollPane(tabla), BorderLayout.CENTER);
            modelo.getVista().panelTabla.revalidate();
            modelo.getVista().panelTabla.repaint();

            modelo.getVista().tablaDatos = tabla;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(modelo.getVista(),
                    "Debe ingresar números válidos para filas y columnas.");
        }
    }

    private void ejecutarAsignacion() {
        try {
            if (modelo.getVista().tablaDatos == null) {
                JOptionPane.showMessageDialog(modelo.getVista(),
                        "Primero genere y llene la tabla de entrada.");
                return;
            }

            JTable tabla = modelo.getVista().tablaDatos;
            int filas = tabla.getRowCount();
            int columnas = tabla.getColumnCount() - 1;

            int[][] matrizCostos = new int[filas][columnas];
            String[] encabezadosFilas = new String[filas];
            String[] encabezadosColumnas = new String[columnas];

            // Obtener encabezados de columnas
            for (int i = 0; i < columnas; i++) {
                encabezadosColumnas[i] = tabla.getColumnName(i + 1);
            }

            // Obtener datos de la tabla
            for (int i = 0; i < filas; i++) {
                encabezadosFilas[i] = tabla.getValueAt(i, 0).toString();
                for (int j = 0; j < columnas; j++) {
                    Object val = tabla.getValueAt(i, j + 1);
                    if (val == null || val.toString().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(modelo.getVista(),
                                "La celda (" + (i + 1) + ", " + (j + 1) + ") está vacía. Complete todos los valores.");
                        return;
                    }
                    matrizCostos[i][j] = Integer.parseInt(val.toString());
                }
            }

            // --- Llamar a la implementación ---
            MetodoAsignacionImp.ResultadoAsignacion resultado =
                    implementacion.calcularAsignacion(matrizCostos, encabezadosFilas, encabezadosColumnas);

            // --- Mostrar resultados ---
            mostrarTablasProceso(resultado.tablasProceso);
            mostrarResultadoAsignacion(resultado.tablaResultado);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(modelo.getVista(),
                    "Asegúrese de que todos los valores en la tabla sean números enteros válidos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(modelo.getVista(),
                    "Error durante la asignación: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ---------------- MOSTRAR RESULTADOS ---------------- //
    private void mostrarTablasProceso(List<DefaultTableModel> tablasProceso) {
        modelo.getVista().panelProceso.removeAll();
        modelo.getVista().panelProceso.setLayout(new BoxLayout(modelo.getVista().panelProceso, BoxLayout.Y_AXIS));

        int paso = 1;
        for (DefaultTableModel model : tablasProceso) {
            JTable tabla = new JTable(model);
            JPanel contenedor = new JPanel(new BorderLayout());
            contenedor.setBorder(BorderFactory.createTitledBorder("Paso " + paso));
            contenedor.add(new JScrollPane(tabla), BorderLayout.CENTER);

            modelo.getVista().panelProceso.add(contenedor);
            paso++;
        }

        modelo.getVista().panelProceso.revalidate();
        modelo.getVista().panelProceso.repaint();
    }

    private void mostrarResultadoAsignacion(DefaultTableModel modeloResultado) {
        modelo.getVista().panelResultado.removeAll();
        JTable tabla = new JTable(modeloResultado);
        modelo.getVista().panelResultado.add(new JScrollPane(tabla), BorderLayout.CENTER);
        modelo.getVista().panelResultado.revalidate();
        modelo.getVista().panelResultado.repaint();
    }
}
