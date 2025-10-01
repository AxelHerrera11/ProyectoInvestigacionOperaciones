package controlador;

import implementacion.MetodoTransporteImp;
import interfaces.IMetodoTransporte;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import vista.VistaMetodoTransporte;

public class ControladorMetodoTransporte extends MouseAdapter {
    
    private final VistaMetodoTransporte vista;
    private final IMetodoTransporte metodo;
    
    public ControladorMetodoTransporte(VistaMetodoTransporte vista) {
        this.vista = vista;
        this.metodo = new MetodoTransporteImp();
        // CONEXIÓN VITAL: Conecta el controlador a la vista para que el botón funcione
        this.vista.setControlador(this); 
    }
    
    // --- LÓGICA DE PARSEO DE DATOS INTEGRADA ---
    
    // Convierte una línea de texto con números (separados por espacio/coma) a un array int[]
    private int[] parseLineToIntArray(String line) {
        // Divide por uno o más espacios o comas
        String[] tokens = line.trim().split("[\\s,]+"); 
        List<Integer> numbers = new ArrayList<>();
        
        for (String token : tokens) {
            if (!token.isEmpty()) {
                try {
                    numbers.add(Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Error de formato: '" + token + "' no es un número entero válido.");
                }
            }
        }
        return numbers.stream().mapToInt(i -> i).toArray();
    }
    
    // Procesa el texto completo y lo valida. Devuelve una lista con [Filas de Costos..., Oferta, Demanda]
    private List<int[]> obtenerDatosDeVista(String texto) throws Exception {
        String[] lineas = texto.split("\n");
        List<String> lineasValidas = new ArrayList<>();
        
        // Filtra las líneas vacías o de solo espacios
        for (String linea : lineas) {
            if (!linea.trim().isEmpty()) {
                lineasValidas.add(linea.trim());
            }
        }
        
        // VALIDACIÓN CRÍTICA: Mínimo 1 fila Costos + 1 Oferta + 1 Demanda = 3
        if (lineasValidas.isEmpty()) {
             throw new IllegalArgumentException("El área de datos está completamente vacía.");
        }
        if (lineasValidas.size() < 3) {
            throw new IllegalArgumentException("Faltan datos. Se requiere al menos 1 fila de Costos, la Oferta y la Demanda (mínimo 3 líneas).");
        }

        int numFilasCostos = lineasValidas.size() - 2;
        
        // 1. Obtener Oferta y Demanda (últimos dos elementos)
        int[] oferta = parseLineToIntArray(lineasValidas.get(numFilasCostos));
        int numFuentes = oferta.length; 

        int[] demanda = parseLineToIntArray(lineasValidas.get(numFilasCostos + 1));
        int numDestinos = demanda.length;
        
        if (numFuentes == 0 || numDestinos == 0) {
             throw new IllegalArgumentException("La Oferta o la Demanda están vacías.");
        }
        
        List<int[]> datosProcesados = new ArrayList<>();
        
        // 2. Validar y añadir las Filas de Costos (dimensiones)
        if (numFilasCostos != numFuentes) {
            throw new IllegalArgumentException("El número de filas de Costos (" + numFilasCostos + ") no coincide con el número de fuentes en la Oferta (" + numFuentes + ").");
        }

        for (int i = 0; i < numFuentes; i++) {
            int[] filaCostos = parseLineToIntArray(lineasValidas.get(i));
            if (filaCostos.length != numDestinos) {
                throw new IllegalArgumentException("La fila de Costos " + (i + 1) + " tiene " + filaCostos.length + " columnas, se esperaban " + numDestinos + " destinos.");
            }
            datosProcesados.add(filaCostos); // Agregamos la fila [int[]]
        }
        
        // 3. Añadir Oferta y Demanda al final
        datosProcesados.add(oferta); 
        datosProcesados.add(demanda);
        
        return datosProcesados;
    }
    
    // --- MANEJO DEL EVENTO DE CLIC ---

    @Override
    public void mouseClicked(MouseEvent e) {
       if (e.getSource() == vista.btnCalcular) {
        try {
            List<int[]> datos = obtenerDatosDeVista(vista.txtDatos.getText());
            
            // 1. PREPARAR DATOS Y HACER COPIAS (Necesario para el cálculo)
            int[] demandaCopia = datos.get(datos.size() - 1).clone();
            int[] ofertaCopia = datos.get(datos.size() - 2).clone();
            
            int numFilasCostos = datos.size() - 2;
            
            int[][] costosOriginales = new int[numFilasCostos][];
            for(int i = 0; i < numFilasCostos; i++) {
                // Reconstruye la matriz de costos ORIGINALES
                costosOriginales[i] = datos.get(i).clone();
            }
            
            // 2. LLAMADA CLAVE: MOSTRAR DATOS DE ENTRADA EN panelTabla
            // Usamos los arrays de la lista 'datos' que NO han sido modificados por los algoritmos.
            vista.mostrarDatosDeEntrada(costosOriginales, datos.get(datos.size() - 2), datos.get(datos.size() - 1));
            
            // 3. INICIAR CÁLCULO (El resto del código...)
            int[][] asignacion = null;
            String metodoSeleccionado = (String) vista.cmbTipo.getSelectedItem();

            switch (metodoSeleccionado) {
                // Los métodos usan las COPIAS (ofertaCopia, demandaCopia)
                case "Esquina Noroeste":
                    asignacion = metodo.esquinaNoroeste(costosOriginales, ofertaCopia, demandaCopia);
                    break;
                case "Costo Mínimo":
                    asignacion = metodo.costoMinimo(costosOriginales, ofertaCopia, demandaCopia);
                    break;
                case "Aproximación de Vogel":
                    asignacion = metodo.aproximacionVogel(costosOriginales, ofertaCopia, demandaCopia);
                    break;
                default:
                     throw new UnsupportedOperationException("Método no reconocido.");
            }
            
            // 4. MOSTRAR RESULTADOS
            if (asignacion != null) {
                vista.mostrarTabla(asignacion);
                vista.mostrarResultado(asignacion, costosOriginales); 
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, 
                    "Error de Datos: " + ex.getMessage(), 
                    "Error al Procesar Datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
}