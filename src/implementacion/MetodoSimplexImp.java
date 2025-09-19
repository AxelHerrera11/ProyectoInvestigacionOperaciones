package implementacion;

import interfaces.IMetodoSimplex;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class MetodoSimplexImp implements IMetodoSimplex{

    private double[][] tableau;
    private String[] baseVars;
    private int numConstraints;
    private int numVars;

    // 🔹 Método principal
    public List<DefaultTableModel> resolver(String funcionObjetivo, String restricciones) {
        double[] c = parseFuncionObjetivo(funcionObjetivo);
        RestriccionesData data = parseRestricciones(restricciones, c.length);
        double[][] A = data.A;
        double[] b = data.b;

        inicializarTableau(A, b, c);

        List<DefaultTableModel> tablas = new ArrayList<>();
        tablas.add(toTableModel()); // tabla inicial

        while (iteracion()) {
            tablas.add(toTableModel()); // tabla por iteración
        }

        return tablas;
    }

    // =====================================================
    // 🔹 Parsear función objetivo
    private double[] parseFuncionObjetivo(String funcion) {
        funcion = funcion.replace(" ", "").replace("-", "+-");
        String[] partes = funcion.split("\\+");
        int maxIdx = 0;

        for (String term : partes) {
            if (term.isEmpty()) continue;
            int idx = Integer.parseInt(term.split("x")[1]);
            maxIdx = Math.max(maxIdx, idx);
        }

        double[] coef = new double[maxIdx];
        for (String term : partes) {
            if (term.isEmpty()) continue;
            String[] split = term.split("x");
            double valor = Double.parseDouble(split[0]);
            int idx = Integer.parseInt(split[1]) - 1;
            coef[idx] = valor;
        }
        return coef;
    }

    // =====================================================
    // 🔹 Parsear restricciones y crear solo holguras positivas
    private RestriccionesData parseRestricciones(String restricciones, int numVars) {
        String[] restr = restricciones.split(";");
        List<double[]> AList = new ArrayList<>();
        List<Double> bList = new ArrayList<>();

        for (String r : restr) {
            r = r.trim().replace(" ", "");

            // Ignorar restricciones de no negatividad xi >= 0
            if (r.matches("x\\d+>=0")) continue;

            boolean esMayor = false;
            if (r.contains("<=")) {
                esMayor = false;
            } else if (r.contains(">=")) {
                esMayor = true;
                r = r.replace(">=", "<="); // convertimos a <= multiplicando por -1 después
            } else {
                throw new IllegalArgumentException("Restricción inválida: " + r);
            }

            r = r.replace("-", "+-");
            String[] lados = r.split("<=");
            if (lados.length != 2) throw new IllegalArgumentException("Restricción mal formada: " + r);

            String izquierda = lados[0];
            double rhs = Double.parseDouble(lados[1]);

            // Si era >=, multiplicamos todo por -1 para que RHS sea positivo y holgura positiva
            if (esMayor) {
                rhs *= -1;
                String[] terminos = izquierda.split("\\+");
                StringBuilder nuevaIzq = new StringBuilder();
                for (String t : terminos) {
                    if (t.isEmpty()) continue;
                    if (t.startsWith("-")) nuevaIzq.append(t.substring(1));
                    else nuevaIzq.append("-").append(t);
                    nuevaIzq.append("+");
                }
                izquierda = nuevaIzq.toString();
                if (izquierda.endsWith("+")) izquierda = izquierda.substring(0, izquierda.length() - 1);
            }

            double[] fila = new double[numVars];
            String[] terminos = izquierda.split("\\+");
            for (String term : terminos) {
                if (term.isEmpty()) continue;
                String[] split = term.split("x");
                double valor = Double.parseDouble(split[0]);
                int idx = Integer.parseInt(split[1]) - 1;
                fila[idx] = valor;
            }

            AList.add(fila);
            bList.add(rhs);
        }

        // Convertir listas a arrays
        double[][] A = new double[AList.size()][numVars];
        double[] b = new double[bList.size()];
        for (int i = 0; i < AList.size(); i++) {
            A[i] = AList.get(i);
            b[i] = bList.get(i);
        }

        return new RestriccionesData(A, b);
    }

    // =====================================================
    // 🔹 Inicializar tableau con holguras positivas
    private void inicializarTableau(double[][] A, double[] b, double[] c) {
        numConstraints = b.length;
        numVars = c.length;
        int width = numVars + numConstraints + 2; // Z + vars + holguras + RHS
        int height = numConstraints + 1;

        tableau = new double[height][width];
        baseVars = new String[height];
        baseVars[0] = "Z";

        // Fila Z
        tableau[0][0] = 1;
        for (int j = 0; j < numVars; j++) tableau[0][j + 1] = -c[j];

        // Restricciones
        for (int i = 0; i < numConstraints; i++) {
            for (int j = 0; j < numVars; j++) tableau[i + 1][j + 1] = A[i][j];
            tableau[i + 1][numVars + 1 + i] = 1; // holgura positiva
            tableau[i + 1][width - 1] = b[i];
            baseVars[i + 1] = "x" + (numVars + 1 + i); // base inicial
        }
    }

    // =====================================================
    // 🔹 Iteración Simplex
    private boolean iteracion() {
        int colPivot = -1;
        double min = 0;
        for (int j = 1; j < tableau[0].length - 1; j++) {
            if (tableau[0][j] < min) {
                min = tableau[0][j];
                colPivot = j;
            }
        }
        if (colPivot == -1) return false;

        int rowPivot = -1;
        double minRatio = Double.MAX_VALUE;
        for (int i = 1; i < tableau.length; i++) {
            double rhs = tableau[i][tableau[0].length - 1];
            double colVal = tableau[i][colPivot];
            if (colVal > 0) {
                double ratio = rhs / colVal;
                if (ratio < minRatio) {
                    minRatio = ratio;
                    rowPivot = i;
                }
            }
        }
        if (rowPivot == -1) throw new RuntimeException("Solución no acotada");

        baseVars[rowPivot] = getColName(colPivot);
        pivot(rowPivot, colPivot);
        return true;
    }

    private void pivot(int row, int col) {
        double pivot = tableau[row][col];
        for (int j = 0; j < tableau[0].length; j++) tableau[row][j] /= pivot;

        for (int i = 0; i < tableau.length; i++) {
            if (i != row) {
                double factor = tableau[i][col];
                for (int j = 0; j < tableau[0].length; j++)
                    tableau[i][j] -= factor * tableau[row][j];
            }
        }
    }

    private String getColName(int col) {
        if (col == 0) return "Z";
        if (col <= numVars) return "x" + col;
        return "x" + col;
    }

    // =====================================================
    // 🔹 Convertir tableau a DefaultTableModel
    public DefaultTableModel toTableModel() {
        int cols = tableau[0].length;
        String[] colNames = new String[cols + 1];
        colNames[0] = "Variable Básica";
        colNames[1] = "Z";
        for (int j = 1; j <= numVars; j++) colNames[j + 1] = "x" + j;
        for (int j = 0; j < numConstraints; j++)
            colNames[numVars + 2 + j] = "x" + (numVars + 1 + j);
        colNames[cols] = "Lado Derecho";

        Object[][] data = new Object[tableau.length][cols + 1];
        for (int i = 0; i < tableau.length; i++) {
            data[i][0] = baseVars[i];
            for (int j = 0; j < cols; j++) data[i][j + 1] = String.format("%.2f", tableau[i][j]);
        }

        return new DefaultTableModel(data, colNames);
    }

    // =====================================================
    // 🔹 Tabla final de variables
    public DefaultTableModel tablaFinal() {
        int totalVars = numVars + numConstraints;
        String[] colNames = new String[totalVars + 1];
        for (int i = 0; i < numVars; i++) colNames[i] = "x" + (i + 1);
        for (int i = 0; i < numConstraints; i++) colNames[numVars + i] = "x" + (numVars + 1 + i);
        colNames[totalVars] = "Z";

        Object[][] data = new Object[1][totalVars + 1];
        for (int i = 0; i < totalVars; i++) data[0][i] = 0.0;

        for (int i = 1; i < baseVars.length; i++) {
            String var = baseVars[i];
            int idx = Integer.parseInt(var.substring(1)) - 1;
            data[0][idx] = String.format("%.2f", tableau[i][tableau[0].length - 1]);
        }

        data[0][totalVars] = String.format("%.2f",tableau[0][tableau[0].length - 1]);
        return new DefaultTableModel(data, colNames);
    }

    // 🔹 Prueba de función objetivo
    public double probarFuncionObjetivo(String funcionObjetivo) {
        double[] c = parseFuncionObjetivo(funcionObjetivo);
        double z = 0;
        for (int i = 0; i < c.length; i++) {
            double xi = 0;
            for (int j = 1; j < baseVars.length; j++) {
                if (baseVars[j].equals("x" + (i + 1))) {
                    xi = tableau[j][tableau[0].length - 1];
                    break;
                }
            }
            z += c[i] * xi;
        }
        return z;
    }

    // =====================================================
    private static class RestriccionesData {
        double[][] A;
        double[] b;
        RestriccionesData(double[][] A, double[] b) { this.A = A; this.b = b; }
    }
}