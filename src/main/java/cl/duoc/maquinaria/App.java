package cl.duoc.maquinaria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader cargador = new FXMLLoader(App.class.getResource("/cl/duoc/maquinaria/view/maquinaria-view.fxml"));
        Parent raiz = cargador.load();
        Scene escena = new Scene(raiz);
        stage.setTitle("Arriendo de Maquinaria");
        stage.setScene(escena);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }
}