/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import implementacion.MetodoGraficoImp;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import modelo.ModeloMetodoGrafico;
import modelo.ModeloVistaPrincipal;
import vista.VistaMetodoGrafico;

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

        // Asignar este controlador al JPanel que simula botón
        if (modelo.getBtnCalcular() != null) {
            modelo.getBtnCalcular().addMouseListener(this);
        }
    }

    // ===== MouseListener =====
   @Override
public void mouseClicked(MouseEvent e) {
    System.out.println("Click detectado en btnCalcular: " + e.getSource());
    if (e.getSource() == modelo.getBtnCalcular()) {
        System.out.println("Ejecutando calcularResultado()");
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
    public void mousePressed(MouseEvent e) {
        // opcional si quieres efecto al presionar
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // opcional si quieres efecto al soltar
    }

// ===== Método principal =====
private void calcularResultado() {
    System.out.println("Entró a calcularResultado()");

    if (modelo.getTxtRestriccion() != null) {
        String texto = modelo.getTxtRestriccion().getText().trim();
        System.out.println("Texto restricciones: " + texto);

        if (!texto.isEmpty()) {
            // Limpiar restricciones anteriores
            metodoGrafico.limpiarRestricciones();

            // Separar por salto de línea, coma o punto y coma
            String[] restricciones = texto.split("[\\n,;]+");
            StringBuilder resultadoTexto = new StringBuilder();

            for (String restriccion : restricciones) {
                restriccion = restriccion.trim();
                metodoGrafico.agregarRestriccion(restriccion);

                MetodoGraficoImp.Restriccion r = metodoGrafico.getUltimaRestriccion();
                resultadoTexto.append(r.toString()).append(" → ");

                boolean tieneX = r.getXIntercept() != null;
                boolean tieneY = r.getYIntercept() != null;

                if (tieneX && tieneY) {
                    resultadoTexto.append("(").append(r.getXIntercept()).append(", 0) y (0, ")
                                  .append(r.getYIntercept()).append(")");
                } else if (tieneX) {
                    resultadoTexto.append("(").append(r.getXIntercept()).append(", 0)");
                } else if (tieneY) {
                    resultadoTexto.append("(0, ").append(r.getYIntercept()).append(")");
                }

                resultadoTexto.append("\n");
            }

            metodoGrafico.resolver();
            modelo.setResultadoOptimo(metodoGrafico.getResultadoOptimo());

            if (modelo.getPanelGrafico() != null) {
                modelo.getPanelGrafico().repaint();
            }

            // Mostrar en txtCalcularRestricciones
            if (modelo.getTxtCalcularRestricciones() != null) {
                modelo.getTxtCalcularRestricciones().setText(resultadoTexto.toString());
            }

        } else {
            System.out.println("txtRestriccion está vacío");
        }
    } else {
        System.out.println("txtRestriccion es null");
    }
}


}