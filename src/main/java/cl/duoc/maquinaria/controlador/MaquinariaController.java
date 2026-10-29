package cl.duoc.maquinaria.controlador;

import cl.duoc.maquinaria.model.CargadorFrontal;
import cl.duoc.maquinaria.model.Excavadora;
import cl.duoc.maquinaria.model.Maquina;
import cl.duoc.maquinaria.repositorio.CargadorFrontalRepository;
import cl.duoc.maquinaria.repositorio.ExcavadoraRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class MaquinariaController {
    @FXML private ComboBox<String> tipo;
    @FXML private TextField codigo;
    @FXML private TextField horas;
    @FXML private TextField potencia;
    @FXML private TextField peso;
    @FXML private CheckBox mantencion;
    @FXML private CheckBox certificacion;
    @FXML private TextField capacidad;
    @FXML private TableView<Maquina> tabla;
    @FXML private TableColumn<Maquina, String> columnaTipo;
    @FXML private TableColumn<Maquina, String> columnaCodigo;
    @FXML private TableColumn<Maquina, Integer> columnaHoras;
    @FXML private TableColumn<Maquina, Double> columnaPotencia;
    @FXML private TableColumn<Maquina, Double> columnaCosto;
    @FXML private Label mensaje;

    private ExcavadoraRepository excavadoraRepository;
    private CargadorFrontalRepository cargadorRepository;
    private final ObservableList<Maquina> maquinas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        tipo.getItems().addAll("Excavadora", "Cargador Frontal");
        tabla.setItems(maquinas);
        try {
            excavadoraRepository = new ExcavadoraRepository();
            cargadorRepository = new CargadorFrontalRepository();
            actualizarTabla();
            mostrarMensaje(" ", false);
        } catch (IOException ex) {
            mostrarMensaje("Error al leer los archivos: " + ex.getMessage(), true);
        }
        actualizarCamposTipo();
    }

    @FXML
    public void cambiarTipo() {
        actualizarCamposTipo();
    }

    private void actualizarCamposTipo() {
        boolean excavadoraSeleccionada = "Excavadora".equals(tipo.getValue());
        boolean cargadorSeleccionado = "Cargador Frontal".equals(tipo.getValue());
        peso.setDisable(!excavadoraSeleccionada);
        mantencion.setDisable(!excavadoraSeleccionada);
        certificacion.setDisable(!excavadoraSeleccionada);
        capacidad.setDisable(!cargadorSeleccionado);
    }

    @FXML
    public void registrar() {
        if (excavadoraRepository == null || cargadorRepository == null) {
            mostrarMensaje("No fue posible cargar los archivos de datos.", true);
            return;
        }
        if (tipo.getValue() == null) {
            mostrarMensaje("Tipo: debe seleccionar una opción.", true);
            return;
        }
        if (codigo.getText() == null || codigo.getText().trim().isEmpty()) {
            mostrarMensaje("Código: no puede estar vacío ni contener solo espacios.", true);
            return;
        }

        int horasUso;
        try {
            horasUso = Integer.parseInt(horas.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarMensaje("Horas de uso: debe ser un número entero entre 0 y 20000.", true);
            return;
        }
        double potenciaValor;
        try {
            potenciaValor = Double.parseDouble(potencia.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            mostrarMensaje("Potencia: debe ser un número mayor que cero.", true);
            return;
        }
        try {
            if ("Excavadora".equals(tipo.getValue())) {
                double pesoValor;
                try {
                    pesoValor = Double.parseDouble(peso.getText().trim().replace(',', '.'));
                } catch (NumberFormatException ex) {
                    mostrarMensaje("Peso: debe ser un número mayor que cero.", true);
                    return;
                }
                Excavadora excavadora = new Excavadora(codigo.getText().trim(), horasUso, potenciaValor, pesoValor, mantencion.isSelected());
                if (certificacion.isSelected()) {
                    excavadora.certificarSeguridad();
                }
                excavadoraRepository.agregar(excavadora);
                mostrarMensaje(excavadora.getCodigo() + " (Excavadora) registrada correctamente.", false);
            } else {
                double capacidadValor;
                try {
                    capacidadValor = Double.parseDouble(capacidad.getText().trim().replace(',', '.'));
                } catch (NumberFormatException ex) {
                    mostrarMensaje("Capacidad del balde: debe ser un número mayor que cero.", true);
                    return;
                }
                CargadorFrontal cargador = new CargadorFrontal(codigo.getText().trim(), horasUso, potenciaValor, capacidadValor);
                cargadorRepository.agregar(cargador);
                mostrarMensaje(cargador.getCodigo() + " (Cargador Frontal) registrada correctamente.", false);
            }
            actualizarTabla();
            limpiarFormulario();
        } catch (IllegalArgumentException ex) {
            mostrarMensaje(ex.getMessage(), true);
        } catch (IOException ex) {
            mostrarMensaje("Error al guardar los archivos: " + ex.getMessage(), true);
            actualizarTabla();
        }
    }

    @FXML
    public void eliminar() {
        Maquina seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Debe seleccionar una máquina de la tabla para eliminarla.", true);
            return;
        }
        try {
            if (seleccionada instanceof Excavadora) {
                excavadoraRepository.eliminar((Excavadora) seleccionada);
            } else if (seleccionada instanceof CargadorFrontal) {
                cargadorRepository.eliminar((CargadorFrontal) seleccionada);
            }
            actualizarTabla();
            mostrarMensaje(seleccionada.getCodigo() + " eliminada correctamente.", false);
        } catch (IOException ex) {
            mostrarMensaje("Error al guardar los archivos: " + ex.getMessage(), true);
        }
    }

    private void actualizarTabla() {
        maquinas.clear();
        if (excavadoraRepository != null) {
            maquinas.addAll(excavadoraRepository.obtenerTodas());
        }
        if (cargadorRepository != null) {
            maquinas.addAll(cargadorRepository.obtenerTodos());
        }
    }

    private void limpiarFormulario() {
        tipo.getSelectionModel().clearSelection();
        codigo.clear();
        horas.clear();
        potencia.clear();
        peso.clear();
        capacidad.clear();
        mantencion.setSelected(false);
        certificacion.setSelected(false);
        actualizarCamposTipo();
    }

    private void mostrarMensaje(String texto, boolean error) {
        mensaje.setText(texto);
        if (error) {
            mensaje.setStyle("-fx-text-fill: firebrick; -fx-font-weight: bold;");
        } else {
            mensaje.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }
}