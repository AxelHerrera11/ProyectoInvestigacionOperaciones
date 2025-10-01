package implementacion;

import interfaces.IMetodoTransporte;
import java.util.Arrays;

public class MetodoTransporteImp implements IMetodoTransporte {

    // Método Auxiliar: Copia Profunda de Matriz de Costos
    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    @Override
    public int[][] esquinaNoroeste(int[][] costos, int[] oferta, int[] demanda) {
        // Asume que 'oferta' y 'demanda' ya son copias recibidas del controlador.
        int m = oferta.length;
        int n = demanda.length;
        int[][] asignacion = new int[m][n];

        int i = 0, j = 0;
        while (i < m && j < n) {
            int min = Math.min(oferta[i], demanda[j]);
            asignacion[i][j] = min;
            
            oferta[i] -= min;
            demanda[j] -= min;

            if (oferta[i] == 0) i++;
            if (demanda[j] == 0) j++;
        }
        return asignacion;
    }

    @Override
    public int[][] costoMinimo(int[][] costos, int[] oferta, int[] demanda) {
        // Asume que 'oferta' y 'demanda' ya son copias recibidas del controlador.
        int m = oferta.length;
        int n = demanda.length;
        int[][] asignacion = new int[m][n];
        
        // El algoritmo opera directamente sobre las copias de oferta y demanda
        
        while (true) {
            int minCosto = Integer.MAX_VALUE;
            int mejorI = -1, mejorJ = -1;

            // Encontrar el mínimo costo disponible (solo celdas con oferta/demanda pendiente)
            for (int i = 0; i < m; i++) {
                if (oferta[i] > 0) {
                    for (int j = 0; j < n; j++) {
                        if (demanda[j] > 0) {
                            if (costos[i][j] < minCosto) {
                                minCosto = costos[i][j];
                                mejorI = i;
                                mejorJ = j;
                            }
                        }
                    }
                }
            }

            if (mejorI == -1) break; // Termina si no hay más asignaciones

            int cantidad = Math.min(oferta[mejorI], demanda[mejorJ]);
            asignacion[mejorI][mejorJ] = cantidad;
            oferta[mejorI] -= cantidad;
            demanda[mejorJ] -= cantidad;
        }

        return asignacion;
    }

   // Dentro de MetodoTransporteImp.java (Paquete implementacion)

@Override
public int[][] aproximacionVogel(int[][] costos, int[] oferta, int[] demanda) {
    int m = oferta.length;
    int n = demanda.length;
    int[][] asignacion = new int[m][n];

    // Crea una copia profunda de los costos para trabajar y modificar (necesario para VAM)
    int[][] costosTrabajo = deepCopy(costos);
    final int INF = Integer.MAX_VALUE;
    
    // Condición de bucle: Continúa mientras quede oferta O demanda por satisfacer
    // (Aunque si el problema está balanceado, deberían agotarse al mismo tiempo)
    while (Arrays.stream(oferta).sum() > 0 && Arrays.stream(demanda).sum() > 0) {
        int[] penalizacionFila = new int[m];
        int[] penalizacionColumna = new int[n];
        
        // 1. Calcular Penalizaciones de Fila
        for (int i = 0; i < m; i++) {
            if (oferta[i] > 0) {
                int c1 = INF, c2 = INF;
                for (int j = 0; j < n; j++) {
                    if (demanda[j] > 0) {
                        if (costosTrabajo[i][j] < c1) {
                            c2 = c1;
                            c1 = costosTrabajo[i][j];
                        } else if (costosTrabajo[i][j] < c2) {
                            c2 = costosTrabajo[i][j];
                        }
                    }
                }
                // Si solo hay un costo disponible (c2 sigue siendo INF), la penalización es 0.
                penalizacionFila[i] = (c2 != INF) ? (c2 - c1) : 0;
            }
        }

        // 2. Calcular Penalizaciones de Columna
        for (int j = 0; j < n; j++) {
            if (demanda[j] > 0) {
                int c1 = INF, c2 = INF;
                for (int i = 0; i < m; i++) {
                    if (oferta[i] > 0) {
                        if (costosTrabajo[i][j] < c1) {
                            c2 = c1;
                            c1 = costosTrabajo[i][j];
                        } else if (costosTrabajo[i][j] < c2) {
                            c2 = costosTrabajo[i][j];
                        }
                    }
                }
                penalizacionColumna[j] = (c2 != INF) ? (c2 - c1) : 0;
            }
        }

        // 3. Encontrar la Máxima Penalización
        int maxPenalizacion = -1;
        boolean esFila = false;
        int indice = -1; // Índice de la fila o columna seleccionada
        
        // Buscar en filas
        for (int i = 0; i < m; i++) {
            if (penalizacionFila[i] > maxPenalizacion) {
                maxPenalizacion = penalizacionFila[i];
                esFila = true;
                indice = i;
            }
        }
        // Buscar en columnas (si hay empate, la columna gana, aunque la regla de desempate es arbitraria)
        for (int j = 0; j < n; j++) {
            if (penalizacionColumna[j] > maxPenalizacion) { // Usar > para dar prioridad al mayor
                maxPenalizacion = penalizacionColumna[j];
                esFila = false;
                indice = j;
            }
        }
        
        // Condición de parada explícita (si ya no hay penalizaciones válidas)
        if (maxPenalizacion == -1) break; 

        // 4. Realizar la Asignación: Encontrar el mínimo costo en la línea seleccionada
        int mejorI = -1, mejorJ = -1;
        int costoMinimo = INF;

        if (esFila) { // Seleccionó una Fila
            for (int j = 0; j < n; j++) {
                if (demanda[j] > 0 && oferta[indice] > 0 && costosTrabajo[indice][j] < costoMinimo) {
                    costoMinimo = costosTrabajo[indice][j];
                    mejorI = indice;
                    mejorJ = j;
                }
            }
        } else { // Seleccionó una Columna
            for (int i = 0; i < m; i++) {
                if (oferta[i] > 0 && demanda[indice] > 0 && costosTrabajo[i][indice] < costoMinimo) {
                    costoMinimo = costosTrabajo[i][indice];
                    mejorI = i;
                    mejorJ = indice;
                }
            }
        }

        // 5. Asignar y Ajustar Oferta/Demanda
        if (mejorI != -1 && mejorJ != -1) {
            int cantidad = Math.min(oferta[mejorI], demanda[mejorJ]);
            asignacion[mejorI][mejorJ] = cantidad;
            oferta[mejorI] -= cantidad;
            demanda[mejorJ] -= cantidad;

            // Marcar la línea agotada: Esto previene que se seleccione nuevamente en el cálculo de penalizaciones
            if (oferta[mejorI] == 0) {
                // Si la fila se agota, marcamos toda la fila de costos como INF para las penalizaciones futuras
                for (int j = 0; j < n; j++) {
                    costosTrabajo[mejorI][j] = INF;
                }
            }
            if (demanda[mejorJ] == 0) {
                // Si la columna se agota, marcamos toda la columna de costos como INF
                for (int i = 0; i < m; i++) {
                    costosTrabajo[i][mejorJ] = INF;
                }
            }
        } else {
             // Debería ser muy difícil llegar aquí si el problema está bien balanceado,
             // pero es una seguridad para evitar el bucle si algo falla.
             break; 
        }
    }
    
    return asignacion;
}
}