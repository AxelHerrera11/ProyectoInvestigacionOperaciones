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
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

    // 2) Procesar restricciones (acepta separadores: nueva línea, coma o punto y coma)
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
            resultadoTexto.append("(").append(r.getXIntercept())
                          .append(",0) y (0,").append(r.getYIntercept()).append(")");
        } else if (tieneX) {
            resultadoTexto.append("(").append(r.getXIntercept()).append(",0)");
        } else if (tieneY) {
            resultadoTexto.append("(0,").append(r.getYIntercept()).append(")");
        }
        resultadoTexto.append("\n");
    }

    // Mostrar resumen de restricciones en el área correspondiente
    if (modelo.getTxtCalcularRestricciones() != null) {
        modelo.getTxtCalcularRestricciones().setText(resultadoTexto.toString());
    }

    // 3) Obtener vértices factibles
    List<double[]> vertices = metodoGrafico.getVerticesFactibles();

    // 4) Función objetivo Z
    String funcionZ = "";
    if (modelo.getTxtFuncionObjetivo() != null) {
        funcionZ = modelo.getTxtFuncionObjetivo().getText().trim();
    }
    double[] coefs = metodoGrafico.extraerCoefsObjetivo(funcionZ);

    // 5) Construir tabla de evaluación Z en vértices
    DefaultTableModel tablaVertices = metodoGrafico.calcularZEnVertices(funcionZ, vertices);
    modelo.setResultadoOptimo(metodoGrafico.getResultadoOptimo());

    // 6) Mostrar tabla y resultado en el panel de tabla
    if (modelo.getPanelTabla() != null) {
        modelo.getPanelTabla().removeAll();
        JTable tabla = new JTable(tablaVertices);
        JScrollPane scroll = new JScrollPane(tabla);
        JLabel lblPuntoOptimo = new JLabel();
        lblPuntoOptimo.setHorizontalAlignment(SwingConstants.CENTER);
        lblPuntoOptimo.setFont(lblPuntoOptimo.getFont().deriveFont(Font.PLAIN, 12f));
        lblPuntoOptimo.setForeground(Color.DARK_GRAY);

        // ===== Calcular punto óptimo según comboBox (con detección de NO ACOTADO) =====
        if (modelo.getComboBoxTipo() != null && !funcionZ.isEmpty()) {
            String tipo = (String) modelo.getComboBoxTipo().getSelectedItem();

            // --- NUEVO: detectar no acotado ---
            if (metodoGrafico.esNoAcotado(tipo, funcionZ)) {
                lblPuntoOptimo.setText("Problema " + tipo + ": NO ACOTADO");
            } else if (vertices.size() > 0) {
                double mejorZ = tipo.equalsIgnoreCase("Maximizar")
                                ? Double.NEGATIVE_INFINITY
                                : Double.POSITIVE_INFINITY;
                double[] puntoOptimo = null;

                for (double[] p : vertices) {
                    double z = coefs[0] * p[0] + coefs[1] * p[1];
                    if (tipo.equalsIgnoreCase("Maximizar") && z > mejorZ) {
                        mejorZ = z; puntoOptimo = p;
                    } else if (tipo.equalsIgnoreCase("Minimizar") && z < mejorZ) {
                        mejorZ = z; puntoOptimo = p;
                    }
                }

                if (puntoOptimo != null) {
                    String mensaje = String.format(
                        "Punto óptimo (%s) Z=%.2f → x=%.2f, y=%.2f",
                        tipo, mejorZ, puntoOptimo[0], puntoOptimo[1]
                    );
                    lblPuntoOptimo.setText(mensaje);
                } else {
                    lblPuntoOptimo.setText("No hay vértices factibles.");
                }
            } else {
                lblPuntoOptimo.setText("No hay vértices factibles.");
            }
        }

        // Agregar tabla y etiqueta al panel
        modelo.getPanelTabla().setLayout(new BorderLayout());
        modelo.getPanelTabla().add(scroll, BorderLayout.CENTER);
        modelo.getPanelTabla().add(lblPuntoOptimo, BorderLayout.SOUTH);
        modelo.getPanelTabla().revalidate();
        modelo.getPanelTabla().repaint();
    }

    // 7) Repintar panel gráfico
    if (modelo.getPanelGrafico() != null) {
        modelo.getPanelGrafico().repaint();
    }
}

}