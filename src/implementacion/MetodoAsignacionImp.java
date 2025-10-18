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
        int[] asign = intentarAsignar(costos);
        tablas.add(crearModeloTabla(costos, nombresFilas, nombresColumnas, "Paso 1: Resta mínima por fila"));
        asignacionesPaso.add(asign.clone());

        // --- PASO 2: Resta mínima por columna ---
        for (int j = 0; j < m; j++) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) if (costos[i][j] < min) min = costos[i][j];
            for (int i = 0; i < n; i++) costos[i][j] -= min;
        }
        asign = intentarAsignar(costos);
        tablas.add(crearModeloTabla(costos, nombresFilas, nombresColumnas, "Paso 2: Resta mínima por columna"));
        asignacionesPaso.add(asign.clone());

        // --- PASOS SIGUIENTES: hasta poder asignar todos ---
        while (!asignacionCompleta(asign)) {
            boolean[] filaMarcada = new boolean[n];
            boolean[] columnaMarcada = new boolean[m];
            marcarLineasMinimas(costos, asign, filaMarcada, columnaMarcada);

            int minNoCubierto = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (!filaMarcada[i]) {
                    for (int j = 0; j < m; j++) {
                        if (!columnaMarcada[j] && costos[i][j] < minNoCubierto)
                            minNoCubierto = costos[i][j];
                    }
                }
            }

            // Ajuste de matriz
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (!filaMarcada[i] && !columnaMarcada[j]) costos[i][j] -= minNoCubierto;
                    else if (filaMarcada[i] && columnaMarcada[j]) costos[i][j] += minNoCubierto;
                }
            }

            asign = intentarAsignar(costos);
            tablas.add(crearModeloTabla(costos, nombresFilas, nombresColumnas, "Ajuste adicional"));
            asignacionesPaso.add(asign.clone());
        }

        // --- PASO FINAL: tabla de resultado ---
        DefaultTableModel tablaFinal = crearModeloTabla(costos, nombresFilas, nombresColumnas, "Resultado final");
        aplicarResaltadoAsignaciones(tablaFinal, asign);

        return new ResultadoAsignacion(tablas, asignacionesPaso, tablaFinal, asign);
    }

    // ---------------------------------------------------------------------
    // 🧮 MÉTODOS AUXILIARES
    // ---------------------------------------------------------------------

    private int[][] copiarMatriz(int[][] original) {
        int[][] copia = new int[original.length][];
        for (int i = 0; i < original.length; i++)
            copia[i] = original[i].clone();
        return copia;
    }

    // Intenta asignar ceros únicos en cada fila sin repetir columnas
    private int[] intentarAsignar(int[][] matriz) {
        int n = matriz.length;
        int m = matriz[0].length;
        int[] asignacion = new int[n];
        Arrays.fill(asignacion, -1);
        boolean[] columnasOcupadas = new boolean[m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (matriz[i][j] == 0 && !columnasOcupadas[j]) {
                    asignacion[i] = j;
                    columnasOcupadas[j] = true;
                    break;
                }
            }
        }
        return asignacion;
    }

    private boolean asignacionCompleta(int[] asignacion) {
        for (int x : asignacion) if (x == -1) return false;
        return true;
    }

    private void marcarLineasMinimas(int[][] matriz, int[] asignacion, boolean[] filaMarcada, boolean[] columnaMarcada) {
        int n = matriz.length;
        int m = matriz[0].length;

        // Marcar filas sin asignación
        for (int i = 0; i < n; i++) if (asignacion[i] == -1) filaMarcada[i] = true;

        boolean cambio;
        do {
            cambio = false;
            for (int i = 0; i < n; i++) {
                if (filaMarcada[i]) {
                    for (int j = 0; j < m; j++) {
                        if (matriz[i][j] == 0 && !columnaMarcada[j]) {
                            columnaMarcada[j] = true;
                            cambio = true;
                        }
                    }
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (columnaMarcada[j] && asignacion[i] == j && !filaMarcada[i]) {
                        filaMarcada[i] = true;
                        cambio = true;
                    }
                }
            }
        } while (cambio);
    }

    // Crea una tabla visual para cada paso
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

    // Resalta visualmente las celdas asignadas (para la tabla final)
    private void aplicarResaltadoAsignaciones(DefaultTableModel modelo, int[] asignaciones) {
        for (int i = 0; i < asignaciones.length; i++) {
            int col = asignaciones[i];
            if (col != -1) {
                Object valor = modelo.getValueAt(i, col + 1);
                modelo.setValueAt("[" + valor + "]", i, col + 1); // Agrega corchetes para marcar asignado
            }
        }
    }
}