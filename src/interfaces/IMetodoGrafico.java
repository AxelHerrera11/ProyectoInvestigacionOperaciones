package interfaces;

import java.util.List;
import javax.swing.JPanel;

public interface IMetodoGrafico {
       // Agregar restricciones del problema
    void agregarRestriccion(String restriccion);

    // Definir función objetivo
    void setFuncionObjetivo(String funcion);

    // Resolver el modelo (calcular intersecciones, región factible, etc.)
    void resolver();

    // Devolver las restricciones registradas
    List<String> getRestricciones();

    // Devolver la función objetivo
    String getFuncionObjetivo();

    // Retornar el panel donde se dibuja la gráfica
    JPanel getPanelGrafico();

    // Obtener el resultado óptimo (máximo o mínimo)
    String getResultadoOptimo();
}
