package utils;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;


public class ToastManager {

    public enum Type { SUCCESS, ERROR, INFO, WARNING }

    // How long (ms) the toast stays on screen before exiting.
    private static final int DISPLAY_DURATION_MS = 3600;

    public static void show(Window owner, String message, Type type) {
        Popup popup = new Popup();
        popup.setAutoHide(false);

        //Visual config per toast type
        String icon, bgColor, borderColor, accentColor, titleText, textColor, progressColor;
        switch (type) {
            case SUCCESS:
                icon          = "✓";
                bgColor       = "linear-gradient(from 0% 0% to 100% 100%, #062017, #0A2E1E)";
                borderColor   = "#1A5C3A";
                accentColor   = "#34D399";
                titleText     = "SUCCESS";
                textColor     = "#A7F3D0";
                progressColor = "#34D399";
                break;
            case ERROR:
                icon          = "✕";
                bgColor       = "linear-gradient(from 0% 0% to 100% 100%, #1F0808, #2E0D0D)";
                borderColor   = "#7F1D1D";
                accentColor   = "#F87171";
                titleText     = "ERROR";
                textColor     = "#FECACA";
                progressColor = "#F87171";
                break;
            case WARNING:
                icon          = "!";
                bgColor       = "linear-gradient(from 0% 0% to 100% 100%, #1E1408, #2A1C0A)";
                borderColor   = "#78350F";
                accentColor   = "#FBBF24";
                titleText     = "WARNING";
                textColor     = "#FDE68A";
                progressColor = "#FBBF24";
                break;
            default: // INFO
                icon          = "i";
                bgColor       = "linear-gradient(from 0% 0% to 100% 100%, #0B1226, #0E1530)";
                borderColor   = "#1A3050";
                accentColor   = "#00E5A8";
                titleText     = "NOTICE";
                textColor     = "#E2E8F0";
                progressColor = "#00E5A8";
                break;
        }

        //Root container
        VBox root = new VBox(0);
        root.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-radius: 12px;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-width: 1.5px;" +
                        "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.65),22,0.35,0,6);"
        );
        root.setMinWidth(300);
        root.setMaxWidth(380);
        root.setPrefWidth(340);

        //Accent top bar
        Region topBar = new Region();
        topBar.setPrefHeight(3);
        topBar.setMaxWidth(Double.MAX_VALUE);
        topBar.setStyle("-fx-background-color: " + accentColor + "; -fx-background-radius: 12 12 0 0;");

        //Main content row
        HBox content = new HBox(14);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(14, 18, 12, 16));

        // Icon circle
        StackPane iconWrapper = new StackPane();
        iconWrapper.setMinSize(34, 34);
        iconWrapper.setMaxSize(34, 34);
        iconWrapper.setStyle(
                "-fx-background-color: " + accentColor + "22;" +
                        "-fx-background-radius: 50px;" +
                        "-fx-border-color: " + accentColor + "66;" +
                        "-fx-border-radius: 50px;" +
                        "-fx-border-width: 1.5px;"
        );
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + accentColor + ";"
        );
        iconWrapper.getChildren().add(iconLabel);

        // Text column
        VBox textCol = new VBox(3);
        textCol.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(titleText);
        title.setStyle(
                "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + accentColor + ";" +
                        "-fx-letter-spacing: 1.5px;"
        );

        Label msgLabel = new Label(message);
        msgLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(260);

        textCol.getChildren().addAll(title, msgLabel);
        HBox.setHgrow(textCol, Priority.ALWAYS);
        content.getChildren().addAll(iconWrapper, textCol);

        //Progress bar
        StackPane progressContainer = new StackPane();
        progressContainer.setPrefHeight(3);
        progressContainer.setMaxWidth(Double.MAX_VALUE);
        progressContainer.setStyle("-fx-background-radius: 0 0 12 12;");

        Region progressTrack = new Region();
        progressTrack.setMaxWidth(Double.MAX_VALUE);
        progressTrack.setPrefHeight(3);
        progressTrack.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 0 0 12 12;");

        Region progressBar = new Region();
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefWidth(340);
        progressBar.setPrefHeight(3);
        progressBar.setStyle("-fx-background-color: " + progressColor + "88; -fx-background-radius: 0 0 12 12;");
        StackPane.setAlignment(progressBar, Pos.CENTER_LEFT);

        progressContainer.getChildren().addAll(progressTrack, progressBar);

        //Assemble
        root.getChildren().addAll(topBar, content, progressContainer);
        popup.getContent().add(root);

        //Position (top-right, cascading for multiple toasts)
        double x = owner.getX() + Math.max(10, owner.getWidth() - 398);
        double y = owner.getY() + 16;
        popup.show(owner, x, y);

        //Slide in from right
        root.setTranslateX(70);
        root.setOpacity(0);

        ParallelTransition enter = new ParallelTransition(
                createTranslate(root, 70, 0, 320),
                createFade(root, 0, 1, 320)
        );
        enter.setInterpolator(Interpolator.EASE_OUT);

        //Progress bar drain animation
        Timeline progressDrain = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(progressBar.prefWidthProperty(), 340)),
                new KeyFrame(Duration.millis(DISPLAY_DURATION_MS),
                        new KeyValue(progressBar.prefWidthProperty(), 0, Interpolator.LINEAR))
        );

        //Hold + drain
        PauseTransition hold = new PauseTransition(Duration.millis(DISPLAY_DURATION_MS));

        //Slide out
        ParallelTransition exit = new ParallelTransition(
                createTranslate(root, 0, 70, 260),
                createFade(root, 1, 0, 260)
        );
        exit.setInterpolator(Interpolator.EASE_IN);
        exit.setOnFinished(e -> popup.hide());

        //Play full sequence
        enter.setOnFinished(e -> progressDrain.play());

        SequentialTransition full = new SequentialTransition(enter, hold, exit);
        full.play();
    }

    //Convenience Overloads
    public static void success(Window owner, String msg) { show(owner, msg, Type.SUCCESS); }
    public static void error  (Window owner, String msg) { show(owner, msg, Type.ERROR);   }
    public static void info   (Window owner, String msg) { show(owner, msg, Type.INFO);    }
    public static void warning(Window owner, String msg) { show(owner, msg, Type.WARNING); }

    //Internal helpers
    private static TranslateTransition createTranslate(VBox node, double fromX, double toX, int ms) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), node);
        tt.setFromX(fromX);
        tt.setToX(toX);
        return tt;
    }

    private static FadeTransition createFade(VBox node, double from, double to, int ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), node);
        ft.setFromValue(from);
        ft.setToValue(to);
        return ft;
    }
}
