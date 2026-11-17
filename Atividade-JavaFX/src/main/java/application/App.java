package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import persistence.JpaUtil;

import java.io.IOException;

/** Ponto de entrada da aplicação JavaFX. */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/view/LivroView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1050, 680);
        primaryStage.setTitle("Sistema de Gerenciamento de Biblioteca Cultural");
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        JpaUtil.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
