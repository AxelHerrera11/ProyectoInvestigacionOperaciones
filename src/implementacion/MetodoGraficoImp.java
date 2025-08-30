package implementacion;

import interfaces.IMetodoGrafico;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;


public class MetodoGraficoImp implements IMetodoGrafico {
     // Dejamos público porque así lo pediste
   public final List<Restriccion> restricciones;
private String funcionObjetivo;
private String resultadoOptimo;

// Si querés desactivar x>=0 e y>=0, poné esto en false
private boolean incluirNoNegatividad = true;

public MetodoGraficoImp() {
    this.restricciones = new ArrayList<>();
    this.resultadoOptimo = "Sin resultado";
}

// 👉 Si querés cambiar el comportamiento de no negatividad en runtime
public void setIncluirNoNegatividad(boolean incluir) {
    this.incluirNoNegatividad = incluir;
}

@Override
public void agregarRestriccion(String restriccion) {
    try {
        restricciones.add(new Restriccion(restriccion));
    } catch (Exception ex) {
        System.out.println("Error al agregar restricción: " + restriccion + " -> " + ex.getMessage());
    }
}

// 👉 Getter público por si lo necesitás en el controlador
public List<Restriccion> getRestriccionesInternas() {
    return restricciones;
}

@Override
public void setFuncionObjetivo(String funcion) {
    this.funcionObjetivo = funcion;
}

// ⚠️ Ya no usamos el óptimo aquí. Lo dejamos no-op para no romper tu interfaz.
@Override
public void resolver(String tipo) {
    this.resultadoOptimo = "Se está usando la tabla de vértices (no cálculo de óptimo).";
}

@Override
public List<String> getRestricciones() {
    List<String> lista = new ArrayList<>();
    for (Restriccion r : restricciones)
        lista.add(r.toString());
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

public void limpiarRestricciones() {
    restricciones.clear();
}

public Restriccion getUltimaRestriccion() {
    if (!restricciones.isEmpty())
        return restricciones.get(restricciones.size() - 1);
    return null;
}

// =========================================================
// ============= NUEVO: TABLA DE VÉRTICES ================
// =========================================================

// =========================================================
// ============= GETTER DE VÉRTICES FACTIBLES ===========
// =========================================================
public List<double[]> getVerticesFactibles() {
    return generarVerticesFactibles();
}

public DefaultTableModel calcularZEnVertices(String funcionObjetivo, List<double[]> vertices) {
    // 1) Parsear coeficientes de la función objetivo
    double[] coefs = extraerCoefsObjetivo(funcionObjetivo);
    double coefX = coefs[0], coefY = coefs[1];

    // 2) Construir tabla
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("x");
    model.addColumn("y");
    model.addColumn("Z = " + formatoObjetivo(coefX, coefY));

    for (double[] p : vertices) {
        double x = clampCero(p[0]);
        double y = clampCero(p[1]);
        double z = coefX * x + coefY * y; // calcular Z en cada punto
        model.addRow(new Object[]{ redondear(x), redondear(y), redondear(z) });
    }

    // guardamos un resumen en resultadoOptimo
    this.resultadoOptimo = "Se calcularon " + model.getRowCount() + " valores de Z.";
    return model;
}

// Genera todos los vértices factibles
public List<double[]> generarVerticesFactibles() {
    final double EPS = 1e-6;
    List<double[]> puntos = new ArrayList<>();

    // Copia de restricciones y añadir no negatividad si aplica
    List<Restriccion> todas = new ArrayList<>(restricciones);
    if (incluirNoNegatividad) {
        // x >= 0 -> 1x + 0y >= 0
        // y >= 0 -> 0x + 1y >= 0
        todas.add(new Restriccion(1, 0, ">=", 0));
        todas.add(new Restriccion(0, 1, ">=", 0));
    }

    // 1) Intersecciones entre pares de restricciones
    for (int i = 0; i < todas.size(); i++) {
        for (int j = i + 1; j < todas.size(); j++) {
            double[] p = interseccion(todas.get(i), todas.get(j));
            if (p != null && esFactible(p[0], p[1], todas)) {
                addUnique(puntos, p, EPS);
            }
        }
    }

    // 2) Intersecciones con ejes de cada restricción original
    for (Restriccion r : todas) {
        // y = 0 → x = rhs / a (si a != 0)
        if (Math.abs(r.a) > EPS) {
            double x = r.rhs / r.a;
            double y = 0;
            if (esFactible(x, y, todas)) addUnique(puntos, new double[]{x, y}, EPS);
        }
        // x = 0 → y = rhs / b (si b != 0)
        if (Math.abs(r.b) > EPS) {
            double x = 0;
            double y = r.rhs / r.b;
            if (esFactible(x, y, todas)) addUnique(puntos, new double[]{x, y}, EPS);
        }
    }

    // 3) Origen si es factible
    if (esFactible(0, 0, todas)) addUnique(puntos, new double[]{0, 0}, EPS);

    return puntos;
}

private void addUnique(List<double[]> lista, double[] p, double eps) {
    for (double[] q : lista) {
        if (Math.abs(q[0] - p[0]) < eps && Math.abs(q[1] - p[1]) < eps) return;
    }
    lista.add(p);
}

// =========================================================
// ============= UTILIDADES DE CÁLCULO ================
// =========================================================
private double[] interseccion(Restriccion r1, Restriccion r2) {
    double a1 = r1.a, b1 = r1.b, c1 = r1.rhs;
    double a2 = r2.a, b2 = r2.b, c2 = r2.rhs;
    double det = a1 * b2 - a2 * b1;
    if (Math.abs(det) < 1e-10) return null; // Paralelas o coincidentes
    double x = (c1 * b2 - c2 * b1) / det;
    double y = (a1 * c2 - a2 * c1) / det;
    return new double[]{x, y};
}

private boolean esFactible(double x, double y, List<Restriccion> lista) {
    final double EPS = 1e-6;
    for (Restriccion r : lista) {
        double lhs = r.a * x + r.b * y;
        switch (r.operador) {
            case "<=": if (lhs - r.rhs > EPS) return false; break;
            case ">=": if (lhs - r.rhs < -EPS) return false; break;
            case "=": if (Math.abs(lhs - r.rhs) > EPS) return false; break;
        }
    }
    return true;
}

public double[] extraerCoefsObjetivo(String funcion) {
    
    double cx = 0, cy = 0;
    if (funcion == null) return new double[]{cx, cy};
    String expr = funcion.replace(" ", "").toLowerCase(Locale.ROOT);
    if (expr.startsWith("z=")) expr = expr.substring(2);

    // Extraer coeficiente de x
    Matcher mx = Pattern.compile("([+-]?\\d*\\.?\\d*)x").matcher(expr);
    if (mx.find()) {
        String s = mx.group(1);
        cx = s.equals("") || s.equals("+") ? 1 : s.equals("-") ? -1 : Double.parseDouble(s);
    }

    // Extraer coeficiente de y
    Matcher my = Pattern.compile("([+-]?\\d*\\.?\\d*)y").matcher(expr);
    if (my.find()) {
        String s = my.group(1);
        cy = s.equals("") || s.equals("+") ? 1 : s.equals("-") ? -1 : Double.parseDouble(s);
    }

    return new double[]{cx, cy};
}

private String formatoObjetivo(double cx, double cy) {
    String sx = (Math.abs(cx - 1) < 1e-9) ? "x" : (Math.abs(cx + 1) < 1e-9) ? "-x" : cx + "x";
    String sy;
    if (Math.abs(cy) < 1e-9) sy = "";
    else if (Math.abs(cy - 1) < 1e-9) sy = " + y";
    else if (Math.abs(cy + 1) < 1e-9) sy = " - y";
    else sy = (cy > 0 ? " + " : " - ") + Math.abs(cy) + "y";
    return sx + sy;
}

private double clampCero(double v) { return Math.abs(v) < 1e-9 ? 0.0 : v; }

private double redondear(double v) { return Math.round(v * 100.0) / 100.0; } // 2 decimales

// ================== MODELO RESTRICCIÓN =================
public static class Restriccion {
    private final double a, b, rhs;
    private final String operador; // "<=", ">=", "="
    private final Double xIntercept, yIntercept;

    // Constructor por String (p.ej. "3x+2y<=60")
    public Restriccion(String expr) {
        expr = expr.replace(" ", "");
        String lhs, rhsPart;
        if (expr.contains("<=")) {
            operador = "<=";
            String[] parts = expr.split("<=");
            lhs = parts[0];
            rhsPart = parts[1];
        } else if (expr.contains(">=")) {
            operador = ">=";
            String[] parts = expr.split(">=");
            lhs = parts[0];
            rhsPart = parts[1];
        } else if (expr.contains("=")) {
            operador = "=";
            String[] parts = expr.split("=");
            lhs = parts[0];
            rhsPart = parts[1];
        } else {
            throw new IllegalArgumentException("Restricción inválida: " + expr);
        }
        rhs = Double.parseDouble(rhsPart);

        Matcher mx = Pattern.compile("([+-]?\\d*\\.?\\d*)x").matcher(lhs);
        double ta = 0, tb = 0;
        if (mx.find()) {
            String coef = mx.group(1);
            ta = coef.equals("") || coef.equals("+") ? 1 : coef.equals("-") ? -1 : Double.parseDouble(coef);
        }

        Matcher my = Pattern.compile("([+-]?\\d*\\.?\\d*)y").matcher(lhs);
        if (my.find()) {
            String coef = my.group(1);
            tb = coef.equals("") || coef.equals("+") ? 1 : coef.equals("-") ? -1 : Double.parseDouble(coef);
        }

        a = ta;
        b = tb;
        xIntercept = Math.abs(a) > 1e-12 ? rhs / a : null;
        yIntercept = Math.abs(b) > 1e-12 ? rhs / b : null;
    }

    // Constructor directo (útil para x>=0, y>=0)
    public Restriccion(double a, double b, String operador, double rhs) {
        this.a = a;
        this.b = b;
        this.operador = operador;
        this.rhs = rhs;
        this.xIntercept = Math.abs(a) > 1e-12 ? rhs / a : null;
        this.yIntercept = Math.abs(b) > 1e-12 ? rhs / b : null;
    }

    @Override
    public String toString() {
        return a + "x + " + b + "y " + operador + " " + rhs;
    }

    public Double getXIntercept() { return xIntercept; }
    public Double getYIntercept() { return yIntercept; }
}

// =========================================================
// ================== PANEL DE DIBUJO ====================
// =========================================================
public static class PanelGrafico extends JPanel {
    private final MetodoGraficoImp metodoGrafico;

    public PanelGrafico(MetodoGraficoImp metodoGrafico) {
        this.metodoGrafico = metodoGrafico;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 400));
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
        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        // 🔹 Calcular máximos dinámicos
        double maxX = 10, maxY = 10; // valores mínimos por defecto
        for (Restriccion r : metodoGrafico.restricciones) {
            if (r.getXIntercept() != null) maxX = Math.max(maxX, r.getXIntercept());
            if (r.getYIntercept() != null) maxY = Math.max(maxY, r.getYIntercept());
        }
        for (double[] v : metodoGrafico.getVerticesFactibles()) {
            maxX = Math.max(maxX, v[0]);
            maxY = Math.max(maxY, v[1]);
        }

        // 🔹 Agregar un 20% de margen
        maxX *= 1.2; maxY *= 1.2;

        // Ejes
        g2.setColor(Color.BLACK);
        g2.drawLine(0, height - 1, width, height - 1); // eje X
        g2.drawLine(1, 0, 1, height); // eje Y

        // Restricciones
        g2.setColor(Color.RED);
        for (Restriccion r : metodoGrafico.restricciones) {
            if (r.getXIntercept() != null && r.getYIntercept() != null) {
                int x1 = transformarX(r.getXIntercept(), width, maxX);
                int y1 = transformarY(0, height, maxY);
                int x2 = transformarX(0, width, maxX);
                int y2 = transformarY(r.getYIntercept(), height, maxY);
                g2.drawLine(x1, y1, x2, y2);
            } else if (r.getXIntercept() != null) {
                int x = transformarX(r.getXIntercept(), width, maxX);
                g2.drawLine(x, 0, x, height);
            } else if (r.getYIntercept() != null) {
                int y = transformarY(r.getYIntercept(), height, maxY);
                g2.drawLine(0, y, width, y);
            }
        }

        // 🔹 Opcional: dibujar los vértices factibles
        g2.setColor(Color.BLUE);
        for (double[] v : metodoGrafico.getVerticesFactibles()) {
            int px = transformarX(v[0], width, maxX);
            int py = transformarY(v[1], height, maxY);
            g2.fillOval(px - 3, py - 3, 6, 6);
        }
    }
}}