package implementacion;

import interfaces.IMetodoGrafico;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;

public class MetodoGraficoImp implements IMetodoGrafico {

    private final List<Restriccion> restricciones;
    private String funcionObjetivo;
    private String resultadoOptimo;

    public MetodoGraficoImp() {
        this.restricciones = new ArrayList<>();
        this.resultadoOptimo = "Sin resultado";
    }

    // ===== Métodos de la interfaz =====
    @Override
    public void agregarRestriccion(String restriccion) {
        // Dividir la cadena por espacios
        String[] tokens = restriccion.split("\\s+(?=[+-]?\\d*x)");
        for (String t : tokens) {
            String limpia = t.trim();
            if (!limpia.isEmpty()) {
                try {
                    restricciones.add(new Restriccion(limpia));
                } catch (Exception ex) {
                    System.out.println("Error al agregar restricción: " + limpia + " -> " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public void setFuncionObjetivo(String funcion) {
        this.funcionObjetivo = funcion;
    }

    @Override
    public void resolver() {
        if (!restricciones.isEmpty()) {
            resultadoOptimo = "Máximo estimado"; // Simulación
        }
    }

    @Override
    public List<String> getRestricciones() {
        List<String> lista = new ArrayList<>();
        for (Restriccion r : restricciones) lista.add(r.toString());
        return lista;
    }

    @Override
    public String getFuncionObjetivo() {
        return funcionObjetivo;
    }

    @Override
    public JPanel getPanelGrafico() {
        return new PanelGrafico(this);
    }

    @Override
    public String getResultadoOptimo() {
        return resultadoOptimo;
    }

    // ===== Clase Restriccion =====
    public static class Restriccion {
        private final int a, b, rhs;
        private final String operador;
        private final Double xIntercept, yIntercept;

        public Restriccion(String expr) {
            expr = expr.replace(" ", "");

            String lhs, rhsPart;
            if (expr.contains("<=")) {
                operador = "<=";
                String[] parts = expr.split("<=");
                lhs = parts[0]; rhsPart = parts[1];
            } else if (expr.contains(">=")) {
                operador = ">=";
                String[] parts = expr.split(">=");
                lhs = parts[0]; rhsPart = parts[1];
            } else if (expr.contains("=")) {
                operador = "=";
                String[] parts = expr.split("=");
                lhs = parts[0]; rhsPart = parts[1];
            } else {
                throw new IllegalArgumentException("Restricción inválida: " + expr);
            }

            this.rhs = Integer.parseInt(rhsPart);

            Matcher mx = Pattern.compile("([+-]?\\d*)x").matcher(lhs);
            if (mx.find()) {
                String coef = mx.group(1);
                a = coef.equals("") || coef.equals("+") ? 1 : coef.equals("-") ? -1 : Integer.parseInt(coef);
            } else a = 0;

            Matcher my = Pattern.compile("([+-]?\\d*)y").matcher(lhs);
            if (my.find()) {
                String coef = my.group(1);
                b = coef.equals("") || coef.equals("+") ? 1 : coef.equals("-") ? -1 : Integer.parseInt(coef);
            } else b = 0;

            xIntercept = a != 0 ? (double) rhs / a : null;
            yIntercept = b != 0 ? (double) rhs / b : null;
        }

        @Override
        public String toString() {
            return a + "x + " + b + "y " + operador + " " + rhs;
        }

        public Double getXIntercept() { return xIntercept; }
        public Double getYIntercept() { return yIntercept; }
    }

    // ===== Panel gráfico =====
    public static class PanelGrafico extends JPanel {
        private final MetodoGraficoImp metodoGrafico;

        public PanelGrafico(MetodoGraficoImp metodoGrafico) {
            this.metodoGrafico = metodoGrafico;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(400, 400)); // Tamaño visible
        }

        private int transformarX(double x, int width, double maxX) {
            return (int)((x / maxX) * width);
        }

        private int transformarY(double y, int height, double maxY) {
            return height - (int)((y / maxY) * height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            System.out.println("Dibujando panel"); // Confirmación

            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            double maxX = 100;
            double maxY = 100;

            // Ejes
            g2.setColor(Color.BLACK);
            g2.drawLine(0, height - 1, width, height - 1); // eje X
            g2.drawLine(1, 0, 1, height); // eje Y

            // Dibujar restricciones
            g2.setColor(Color.RED);
            for (Restriccion r : metodoGrafico.restricciones) {
                if (r.getXIntercept() != null && r.getYIntercept() != null) {
                    int x1 = transformarX(r.getXIntercept(), width, maxX);
                    int y1 = transformarY(0, height, maxY);
                    int x2 = transformarX(0, width, maxX);
                    int y2 = transformarY(r.getYIntercept(), height, maxY);
                    g2.drawLine(x1, y1, x2, y2);
                }
            }

            // Dibujar función objetivo (opcional)
            if (metodoGrafico.getFuncionObjetivo() != null) {
                g2.setColor(Color.BLUE);
                // Aquí podrías dibujar la función objetivo si tenés lógica para eso
            }
        }
    }
    public void limpiarRestricciones() {
    restricciones.clear();
}
public Restriccion getUltimaRestriccion() {
    if (!restricciones.isEmpty()) {
        return restricciones.get(restricciones.size() - 1);
    }
    return null;
}

}
