package modelo;

// Clase para encapsular los datos de entrada
public class DatosTransporte {
    private final int[][] costos;
    private final int[][] costosOriginales; // Una copia, ya que los arrays se modifican en el cálculo
    private final int[] oferta;
    private final int[] demanda;
    
    // Constructor (recibe arrays que ya deberían estar validados)
    public DatosTransporte(int[][] costos, int[] oferta, int[] demanda) {
        this.costos = costos;
        this.oferta = oferta;
        this.demanda = demanda;
        // Se recomienda hacer una copia defensiva si los arrays se van a modificar
        this.costosOriginales = new int[costos.length][];
        for (int i = 0; i < costos.length; i++) {
            this.costosOriginales[i] = costos[i].clone();
        }
    }

    // Getters
    public int[][] getCostos() { return costos; }
    public int[][] getCostosOriginales() { return costosOriginales; }
    public int[] getOferta() { return oferta; }
    public int[] getDemanda() { return demanda; }
}