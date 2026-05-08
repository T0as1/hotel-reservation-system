import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {
    public static Stage primaryStage;
    public static final String APP_NAME = "Vespera";

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1040, 680);
        scene.setFill(Color.web("#07090F"));
        scene.getStylesheets().add(getClass().getResource("/views/hotel.css").toExternalForm());

        stage.setTitle(APP_NAME + " — Twilight Resorts");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
