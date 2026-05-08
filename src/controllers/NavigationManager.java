package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NavigationManager {

    public static void navigateTo(Stage stage, String fxmlPath, String title) throws Exception {
        boolean maximised = stage.isMaximized();
        boolean fullScreen = stage.isFullScreen();
        double w = stage.getWidth();
        double h = stage.getHeight();
        double x = stage.getX();
        double y = stage.getY();

        FXMLLoader loader = new FXMLLoader(
                NavigationManager.class.getResource("/" + fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root, w, h);
        scene.setFill(Color.web("#07090F"));
        scene.getStylesheets().add(
                NavigationManager.class.getResource("/views/hotel.css").toExternalForm());

        stage.setTitle(title);
        stage.setScene(scene);

        if (fullScreen) {
            stage.setFullScreen(true);
        } else if (maximised) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(w);
            stage.setHeight(h);
            stage.setX(x);
            stage.setY(y);
        }

        // Smooth fade + subtle scale-in
        root.setOpacity(0);
        root.setScaleX(0.985);
        root.setScaleY(0.985);

        FadeTransition ft = new FadeTransition(Duration.millis(320), root);
        ft.setFromValue(0); ft.setToValue(1);

        ScaleTransition st = new ScaleTransition(Duration.millis(320), root);
        st.setFromX(0.985); st.setFromY(0.985);
        st.setToX(1); st.setToY(1);

        ParallelTransition pt = new ParallelTransition(ft, st);
        pt.setInterpolator(Interpolator.EASE_OUT);
        pt.play();

        stage.show();
    }

    public static Stage stageFrom(Node node) {
        return (Stage) node.getScene().getWindow();
    }
}
