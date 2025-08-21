package implementacion;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;

public class MetodoGraficoImp {
    
    // ===== Clase interna Restriccion =====
    public static class Restriccion {
        private int a;   // coeficiente de x
        private int b;   // coeficiente de y
        private int rhs; // lado derecho
        private Double xIntercept;
        private Double yIntercept;

        public Restriccion(String expr) {
            expr = expr.replace(" ", "");

            String lhs, rhsPart;
            if (expr.contains("<=")) {
                String[] parts = expr.split("<=");
                lhs = parts[0];
                rhsPart = parts[1];
            } else if (expr.contains(">=")) {
                String[] parts = expr.split(">=");
                lhs = parts[0];
                rhsPart = parts[1];
            } else {
                String[] parts = expr.split("=");
                lhs = parts[0];
                rhsPart = parts[1];
            }

            this.rhs = Integer.parseInt(rhsPart);

            // Coeficiente de x
            Matcher mx = Pattern.compile("([+-]?\\d*)x").matcher(lhs);
            if (mx.find()) {
                String coef = mx.group(1);
                if (coef.equals("") || coef.equals("+")) this.a = 1;
                else if (coef.equals("-")) this.a = -1;
                else this.a = Integer.parseInt(coef);
            } else {
                this.a = 0;
            }

            // Coeficiente de y
            Matcher my = Pattern.compile("([+-]?\\d*)y").matcher(lhs);
            if (my.find()) {
                String coef = my.group(1);
                if (coef.equals("") || coef.equals("+")) this.b = 1;
                else if (coef.equals("-")) this.b = -1;
                else this.b = Integer.parseInt(coef);
            } else {
                this.b = 0;
            }

            // Intersecciones
            this.xIntercept = (this.a != 0) ? (double) this.rhs / this.a : null;
            this.yIntercept = (this.b != 0) ? (double) this.rhs / this.b : null;
        }

        public int getA() { return a; }
        public int getB() { return b; }
        public int getRhs() { return rhs; }
        public Double getXIntercept() { return xIntercept; }
        public Double getYIntercept() { return yIntercept; }

        @Override
        public String toString() {
            return "Restricción: " + a + "x + " + b + "y = " + rhs +
                   "\nIntersección X: (" + (xIntercept != null ? xIntercept : "N/A") + ", 0)" +
                   "\nIntersección Y: (0, " + (yIntercept != null ? yIntercept : "N/A") + ")";
        }
    }

        // ===== Lista de restricciones =====
    private List<Restriccion> restricciones;

    public MetodoGraficoImp() {
        this.restricciones = new ArrayList<>();
    }

    // Agregar restricción desde texto
    public void agregarRestriccion(String expr) {
        Restriccion r = new Restriccion(expr);
        restricciones.add(r);
    }

    // Obtener todas las restricciones
    public List<Restriccion> getRestricciones() {
        return restricciones;
    }

    // Mostrar restricciones en consola
    public void mostrarRestricciones() {
        for (Restriccion r : restricciones) {
            System.out.println(r);
        }
    }
    
    public class PanelGrafico extends JPanel {
    private MetodoGraficoImp metodoGrafico;

    public PanelGrafico(MetodoGraficoImp metodoGrafico) {
        this.metodoGrafico = metodoGrafico;
        setBackground(Color.WHITE); // fondo blanco
    }

    private int transformarX(double x, int panelWidth, double maxX) {
        return (int)((x / maxX) * panelWidth);
    }

    private int transformarY(double y, int panelHeight, double maxY) {
        return panelHeight - (int)((y / maxY) * panelHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        double maxX = 20; // Escala máxima (puedes ajustar según tus restricciones)
        double maxY = 20;

        // Dibujar ejes
        g2.setColor(Color.BLACK);
        g2.drawLine(0, height - 1, width, height - 1); // Eje X
        g2.drawLine(1, 0, 1, height);                  // Eje Y

        // Dibujar restricciones
        g2.setColor(Color.RED);
        for (MetodoGraficoImp.Restriccion r : metodoGrafico.getRestricciones()) {
            if (r.getXIntercept() != null && r.getYIntercept() != null) {
                int x1 = transformarX(r.getXIntercept(), width, maxX);
                int y1 = transformarY(0, height, maxY);
                int x2 = transformarX(0, width, maxX);
                int y2 = transformarY(r.getYIntercept(), height, maxY);

                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }
    }

}
