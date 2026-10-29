package cl.duoc.maquinaria.repositorio;

import cl.duoc.maquinaria.model.CargadorFrontal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CargadorFrontalRepository {
    private final Path archivo = Path.of("cargadores_frontales.json");
    private final ObservableList<CargadorFrontal> cargadores = FXCollections.observableArrayList();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public CargadorFrontalRepository() throws IOException {
        cargar();
    }

    private void cargar() throws IOException {
        if (!Files.exists(archivo)) {
            Files.writeString(archivo, "[]");
            return;
        }
        String json = Files.readString(archivo);
        CargadorFrontal[] datos = gson.fromJson(json, CargadorFrontal[].class);
        if (datos != null) {
            cargadores.addAll(datos);
        }
    }

    public ObservableList<CargadorFrontal> obtenerTodos() {
        return cargadores;
    }

    public void agregar(CargadorFrontal cargador) throws IOException {
        cargadores.add(cargador);
        guardar();
    }

    public void eliminar(CargadorFrontal cargador) throws IOException {
        cargadores.remove(cargador);
        guardar();
    }

    private void guardar() throws IOException {
        Files.writeString(archivo, gson.toJson(cargadores));
    }
}