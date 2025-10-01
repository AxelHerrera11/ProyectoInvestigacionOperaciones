/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

/**
 *
 * @author gerso
 */
public interface IMetodoTransporte {
    // Retorna la matriz de asignación
    int[][] esquinaNoroeste(int[][] costos, int[] oferta, int[] demanda);
    
    // Retorna la matriz de asignación
    int[][] costoMinimo(int[][] costos, int[] oferta, int[] demanda);
    
    // **NUEVO MÉTODO**
    // Retorna la matriz de asignación para la Aproximación de Vogel
    int[][] aproximacionVogel(int[][] costos, int[] oferta, int[] demanda);
}