package implementacion;

import javax.swing.table.DefaultTableModel;
import java.util.*;

public class MetodoAsignacionImp {

    public static class ResultadoAsignacion {
        public List<DefaultTableModel> tablasProceso;
        public List<int[]> asignacionesPaso;
        public DefaultTableModel tablaResultado;
        public int[] asignacionFinal;

        public ResultadoAsignacion(List<DefaultTableModel> t, List<int[]> a, DefaultTableModel r, int[] f) {
            this.tablasProceso = t;
            this.asignacionesPaso = a;
            this.tablaResultado = r;
            this.asignacionFinal = f;
        }
    }

    public ResultadoAsignacion calcularAsignacion(int[][] matrizCostos, String[] nombresFilas, String[] nombresColumnas) {
        List<DefaultTableModel> tablas = new ArrayList<>();
        List<int[]> asignacionesPaso = new ArrayList<>();

        int n = matrizCostos.length;
        int m = matrizCostos[0].length;
        int[][] costos = copiarMatriz(matrizCostos);

        // --- PASO 1: Resta mínima por fila ---
        for (int i = 0; i < n; i++) {
            int min = Arrays.stream(costos[i]).min().orElse(0);
            for (int j = 0; j < m; j++) costos[i][j] -= min;
        }
        tablas.add(crearModeloTabla(costos, nombresFilas, nombresColumnas, "Paso 1: Resta mínima por fila"));
        int[] asign = asignarOptimamente(costos);
        asignacionesPaso.add(asign.clone());

        // --- PASO 2: Resta mínima por columna ---
        for (int j = 0; j < m; j++) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) if (costos[i][j] < min) min = costos[i][j];
            for (int i = 0; i < n; i++) costos[i][j] -= min;
        }
        tablas.add(crearModeloTabla(costos, nombresFilas, nombresColumnas, "Paso 2: Resta mínima por columna"));
        asign = asignarOptimamente(costos);
        asignacionesPaso.add(asign.clone());

        // --- PASOS ADICIONALES ---
        while (!asignacionCompleta(asign)) {
            int[] lineas = cubrirCeros(costos, asign);
            int minNoCubierto = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (lineas[i] == 0) { // fila no cubierta
                    for (int j = 0; j < m; j++) {
                        if (lineas[n + j] == 0 && costos[i][j] < minNoCubierto) {
                            minNoCubierto = costos[i][j];
                        }
                    }
                }
            }

            // Ajuste de matriz
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (lineas[i] == 0 && lineas[n + j] == 0) costos[i][j] -= minNoCubierto;
                    else if (lineas[i] == 1 && lineas[n + j] == 1) costos[i][j] += minNoCubierto;
                }
            }

            tablas.add(crearModeloTabla(costos, nombresFilas, nombresColumnas, "Ajuste adicional"));
            asign = asignarOptimamente(costos);
            asignacionesPaso.add(asign.clone());
        }

        // --- RESULTADO FINAL ---
        DefaultTableModel tablaFinal = crearModeloTabla(costos, nombresFilas, nombresColumnas, "Resultado final");
        aplicarResaltadoAsignaciones(tablaFinal, asign);

        return new ResultadoAsignacion(tablas, asignacionesPaso, tablaFinal, asign);
    }

    // ========================== AUXILIARES ============================= //

    private int[][] copiarMatriz(int[][] original) {
        int[][] copia = new int[original.length][];
        for (int i = 0; i < original.length; i++) copia[i] = original[i].clone();
        return copia;
    }

    private boolean asignacionCompleta(int[] asignacion) {
        for (int a : asignacion) if (a == -1) return false;
        return true;
    }

    private int[] asignarOptimamente(int[][] matriz) {
        int n = matriz.length;
        int m = matriz[0].length;
        int[] asignacion = new int[n];
        Arrays.fill(asignacion, -1);

        boolean[] columnasUsadas = new boolean[m];

        // Buscar ceros únicos por fila (más seguro que el original)
        for (int i = 0; i < n; i++) {
            List<Integer> ceros = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                if (matriz[i][j] == 0 && !columnasUsadas[j]) ceros.add(j);
            }
            if (ceros.size() == 1) {
                int col = ceros.get(0);
                asignacion[i] = col;
                columnasUsadas[col] = true;
            }
        }

        // Asignar los ceros restantes si quedan filas libres
        for (int i = 0; i < n; i++) {
            if (asignacion[i] == -1) {
                for (int j = 0; j < m; j++) {
                    if (matriz[i][j] == 0 && !columnasUsadas[j]) {
                        asignacion[i] = j;
                        columnasUsadas[j] = true;
                        break;
                    }
                }
            }
        }

        return asignacion;
    }

    // Cubre los ceros según el algoritmo húngaro
    private int[] cubrirCeros(int[][] matriz, int[] asignacion) {
        int n = matriz.length, m = matriz[0].length;
        int[] lineas = new int[n + m]; // filas + columnas
        boolean[] filaMarcada = new boolean[n];
        boolean[] colMarcada = new boolean[m];

        for (int i = 0; i < n; i++)
            if (asignacion[i] == -1) filaMarcada[i] = true;

        boolean cambio;
        do {
            cambio = false;
            for (int i = 0; i < n; i++) {
                if (filaMarcada[i]) {
                    for (int j = 0; j < m; j++) {
                        if (matriz[i][j] == 0 && !colMarcada[j]) {
                            colMarcada[j] = true;
                            cambio = true;
                        }
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (colMarcada[j] && asignacion[i] == j && !filaMarcada[i]) {
                        filaMarcada[i] = true;
                        cambio = true;
                    }
                }
            }
        } while (cambio);

        for (int i = 0; i < n; i++) lineas[i] = filaMarcada[i] ? 0 : 1;
        for (int j = 0; j < m; j++) lineas[n + j] = colMarcada[j] ? 1 : 0;

        return lineas;
    }

    private DefaultTableModel crearModeloTabla(int[][] matriz, String[] filas, String[] columnas, String titulo) {
        String[] nombres = new String[columnas.length + 1];
        nombres[0] = titulo;
        System.arraycopy(columnas, 0, nombres, 1, columnas.length);
        DefaultTableModel modelo = new DefaultTableModel(nombres, 0);
        for (int i = 0; i < filas.length; i++) {
            Object[] fila = new Object[columnas.length + 1];
            fila[0] = filas[i];
            for (int j = 0; j < columnas.length; j++) fila[j + 1] = matriz[i][j];
            modelo.addRow(fila);
        }
        return modelo;
    }

    private void aplicarResaltadoAsignaciones(DefaultTableModel modelo, int[] asignaciones) {
        for (int i = 0; i < asignaciones.length; i++) {
            int col = asignaciones[i];
            if (col != -1) {
                Object valor = modelo.getValueAt(i, col + 1);
                modelo.setValueAt("[" + valor + "]", i, col + 1);
            }
        }
    }
}
