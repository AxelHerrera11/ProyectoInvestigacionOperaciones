/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import implementacion.MetodoGraficoImp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import modelo.ModeloMetodoGrafico;

/**
 *
 * @author javie
 */
public class ControladorMetodoGrafico implements MouseListener {
   private final ModeloMetodoGrafico modelo;
private final MetodoGraficoImp metodoGrafico;

public ControladorMetodoGrafico(ModeloMetodoGrafico modelo) {
    this.modelo = modelo;
    // Inicializar la implementación del método gráfico
    metodoGrafico = new MetodoGraficoImp();

    // Crear el panel gráfico personalizado y asignarlo al modelo
    JPanel panelGrafico = new MetodoGraficoImp.PanelGrafico(metodoGrafico);
    modelo.setPanelGrafico(panelGrafico);

    // Asignar este controlador al botón calcular
    if (modelo.getBtnCalcular() != null) {
        modelo.getBtnCalcular().addMouseListener(this);
    }
}

// ===== MouseListener =====
@Override
public void mouseClicked(MouseEvent e) {
    if (e.getSource() == modelo.getBtnCalcular()) {
        calcularResultado();
    }
}

@Override
public void mouseEntered(MouseEvent e) {
    e.getComponent().setBackground(e.getComponent().getBackground().brighter());
}

@Override
public void mouseExited(MouseEvent e) {
    e.getComponent().setBackground(e.getComponent().getBackground().darker());
}

@Override
public void mousePressed(MouseEvent e) {}
@Override
public void mouseReleased(MouseEvent e) {}

// ===== Método principal =====
private void calcularResultado() {
    // 1) Validaciones
    if (modelo.getTxtRestriccion() == null) {
        System.out.println("txtRestriccion es null");
        return;
    }
    String textoRestricciones = modelo.getTxtRestriccion().getText().trim();
    if (textoRestricciones.isEmpty()) {
        System.out.println("txtRestriccion está vacío");
        return;
    }

    // Limpiar restricciones anteriores
    metodoGrafico.limpiarRestricciones();

    // Procesar restricciones
    String[] restricciones = textoRestricciones.split("[\\n,;]+");
    StringBuilder resultadoTexto = new StringBuilder();
    for (String restriccion : restricciones) {
        restriccion = restriccion.trim();
        if (restriccion.isEmpty()) continue;

        metodoGrafico.agregarRestriccion(restriccion);

        // Mostrar intersecciones con ejes
        MetodoGraficoImp.Restriccion r = metodoGrafico.getUltimaRestriccion();
        resultadoTexto.append(r.toString()).append(" → ");
        boolean tieneX = r.getXIntercept() != null;
        boolean tieneY = r.getYIntercept() != null;
        if (tieneX && tieneY) {
            resultadoTexto.append("(").append(r.getXIntercept()).append(",0) y (0,")
                          .append(r.getYIntercept()).append(")");
        } else if (tieneX) {
            resultadoTexto.append("(").append(r.getXIntercept()).append(",0)");
        } else if (tieneY) {
            resultadoTexto.append("(0,").append(r.getYIntercept()).append(")");
        }
        resultadoTexto.append("\n");
    }

    // Mostrar restricciones procesadas
    if (modelo.getTxtCalcularRestricciones() != null) {
        modelo.getTxtCalcularRestricciones().setText(resultadoTexto.toString());
    }

    // 2) Obtener los vértices factibles
    List<double[]> vertices = metodoGrafico.getVerticesFactibles();

    // 3) Obtener función objetivo Z
    String funcionZ = "";
    if (modelo.getTxtFuncionObjetivo() != null) {
        funcionZ = modelo.getTxtFuncionObjetivo().getText().trim();
    }

    // 4) Calcular tabla de vértices con Z
    DefaultTableModel tablaVertices = metodoGrafico.calcularZEnVertices(funcionZ, vertices);
    modelo.setResultadoOptimo(metodoGrafico.getResultadoOptimo());

    // 5) Mostrar tabla y mensaje en panelTabla
    if (modelo.getPanelTabla() != null) {
        modelo.getPanelTabla().removeAll();
        JTable tabla = new JTable(tablaVertices);
        JScrollPane scroll = new JScrollPane(tabla);
        JLabel lblPuntoOptimo = new JLabel();
        lblPuntoOptimo.setHorizontalAlignment(SwingConstants.CENTER);

        // ===== Calcular punto óptimo según comboBox =====
        if (modelo.getComboBoxTipo() != null && vertices.size() > 0) {
            String tipo = (String) modelo.getComboBoxTipo().getSelectedItem();
            double mejorZ = tipo.equalsIgnoreCase("Maximizar") ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            double[] puntoOptimo = null;
            for (double[] p : vertices) {
                double[] coefs = metodoGrafico.extraerCoefsObjetivo(funcionZ);
                double z = coefs[0] * p[0] + coefs[1] * p[1];
                if (tipo.equalsIgnoreCase("Maximizar") && z > mejorZ) {
                    mejorZ = z;
                    puntoOptimo = p;
                } else if (tipo.equalsIgnoreCase("Minimizar") && z < mejorZ) {
                    mejorZ = z;
                    puntoOptimo = p;
                }
            }
            if (puntoOptimo != null) {
                String mensaje = String.format("Punto óptimo (%s) Z=%.2f → x=%.2f, y=%.2f",
                                               tipo, mejorZ, puntoOptimo[0], puntoOptimo[1]);
                lblPuntoOptimo.setText(mensaje);
            }
        }

        // Agregar tabla y etiqueta al panel
        modelo.getPanelTabla().setLayout(new BorderLayout());
        modelo.getPanelTabla().add(scroll, BorderLayout.CENTER);
        modelo.getPanelTabla().add(lblPuntoOptimo, BorderLayout.SOUTH);
        modelo.getPanelTabla().revalidate();
        modelo.getPanelTabla().repaint();
    }

    // 6) Repintar panel gráfico
    if (modelo.getPanelGrafico() != null) {
        modelo.getPanelGrafico().repaint();
    }
}
}