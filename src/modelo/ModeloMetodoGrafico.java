/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import vista.VistaMetodoGrafico;

/**
 *
 * @author javie
 */

public class ModeloMetodoGrafico {

    // Vista asociada
    private VistaMetodoGrafico vista;

    // Resultado óptimo
    private String resultadoOptimo;

    // Componentes de la vista
    private JPanel btnCalcular;         
    private JTextField txtRestriccion;  
    private JPanel panelGrafico;    
    private JTextField txtCalcularRestricciones;
    private JTextField txtFuncionObjetivo; // nuevo campo para Z
    private JComboBox<String> comboBoxTipo; 
    private JPanel panelTabla; 

    // ===== Constructores =====
    public ModeloMetodoGrafico() {}

    // Constructor que recibe la vista y asigna los componentes
    public ModeloMetodoGrafico(VistaMetodoGrafico vista) {
        this.vista = vista;

        // Inicializar los componentes desde la vista
        this.txtRestriccion = vista.txtRestricciones;
        this.btnCalcular = vista.btnCalcular;
        this.panelGrafico = vista.panelGrafico;
        this.txtCalcularRestricciones = vista.txtCalcularRestricciones;
        this.txtFuncionObjetivo = vista.txtZ; // nuevo
        this.comboBoxTipo = vista.cmbTipo; 
        this.panelTabla = vista.panelTabla; 
    }

    // ===== Getters y setters =====
    public VistaMetodoGrafico getVista() { return vista; }
    public void setVista(VistaMetodoGrafico vista) { this.vista = vista; }

    public JPanel getBtnCalcular() { return btnCalcular; }
    public void setBtnCalcular(JPanel btnCalcular) { this.btnCalcular = btnCalcular; }

    public JTextField getTxtRestriccion() { return txtRestriccion; }
    public void setTxtRestriccion(JTextField txtRestriccion) { this.txtRestriccion = txtRestriccion; }

    public JPanel getPanelGrafico() { return panelGrafico; }
    public void setPanelGrafico(JPanel panelGrafico) { this.panelGrafico = panelGrafico; }

    public JTextField getTxtCalcularRestricciones() { return txtCalcularRestricciones; }
    public void setTxtCalcularRestricciones(JTextField txtCalcularRestricciones) { this.txtCalcularRestricciones = txtCalcularRestricciones; }

    public JTextField getTxtFuncionObjetivo() { return txtFuncionObjetivo; }
    public void setTxtFuncionObjetivo(JTextField txtFuncionObjetivo) { this.txtFuncionObjetivo = txtFuncionObjetivo; }

    public JComboBox<String> getComboBoxTipo() { return comboBoxTipo; }
    public void setComboBoxTipo(JComboBox<String> comboBoxTipo) { this.comboBoxTipo = comboBoxTipo; }

    public JPanel getPanelTabla() { return panelTabla; }
    public void setPanelTabla(JPanel panelTabla) { this.panelTabla = panelTabla; }

    public String getResultadoOptimo() { return resultadoOptimo; }
    public void setResultadoOptimo(String resultadoOptimo) { this.resultadoOptimo = resultadoOptimo; }
}
