package utils;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;

public class AnimationUtils {

    public static void shake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(55), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(7);
        tt.setAutoReverse(true);
        tt.setInterpolator(Interpolator.EASE_OUT);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }

    public static void fadeIn(Node node) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(380), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_IN);
        ft.play();
    }

    public static void fadeInFast(Node node) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(180), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public static void fadeOut(Node node, Runnable onFinished) {
        FadeTransition ft = new FadeTransition(Duration.millis(260), node);
        ft.setFromValue(node.getOpacity());
        ft.setToValue(0);
        ft.setInterpolator(Interpolator.EASE_OUT);
        if (onFinished != null) ft.setOnFinished(e -> onFinished.run());
        ft.play();
    }

    public static void slideUp(Node node) {
        node.setTranslateY(24);
        node.setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(360), node);
        tt.setFromY(24);
        tt.setToY(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft = new FadeTransition(Duration.millis(360), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        new ParallelTransition(tt, ft).play();
    }

    public static void slideInRight(Node node) {
        node.setTranslateX(36);
        node.setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(320), node);
        tt.setFromX(36);
        tt.setToX(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft = new FadeTransition(Duration.millis(320), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        new ParallelTransition(tt, ft).play();
    }

    public static void slideInLeft(Node node) {
        node.setTranslateX(-36);
        node.setOpacity(0);
        TranslateTransition tt = new TranslateTransition(Duration.millis(320), node);
        tt.setFromX(-36);
        tt.setToX(0);
        tt.setInterpolator(Interpolator.EASE_OUT);
        FadeTransition ft = new FadeTransition(Duration.millis(320), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        new ParallelTransition(tt, ft).play();
    }

    // Page transition for sidebar page switches
    public static void pageTransition(Node node) {
        node.setOpacity(0);
        node.setTranslateY(12);
        node.setScaleX(0.98);
        node.setScaleY(0.98);

        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        ft.setFromValue(0);
        ft.setToValue(1);

        TranslateTransition tt = new TranslateTransition(Duration.millis(300), node);
        tt.setFromY(12);
        tt.setToY(0);

        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        st.setFromX(0.98);
        st.setToX(1.0);
        st.setFromY(0.98);
        st.setToY(1.0);

        ParallelTransition pt = new ParallelTransition(ft, tt, st);
        pt.setInterpolator(Interpolator.EASE_OUT);
        pt.play();
    }

    public static void pulse(Node node) {
        ScaleTransition grow = new ScaleTransition(Duration.millis(130), node);
        grow.setFromX(1);
        grow.setFromY(1);
        grow.setToX(1.07);
        grow.setToY(1.07);
        grow.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(130), node);
        shrink.setFromX(1.07);
        shrink.setFromY(1.07);
        shrink.setToX(1);
        shrink.setToY(1);
        shrink.setInterpolator(Interpolator.EASE_IN);

        new SequentialTransition(grow, shrink).play();
    }

    public static void buttonPress(Node node) {
        ScaleTransition down = new ScaleTransition(Duration.millis(90), node);
        down.setToX(0.94);
        down.setToY(0.94);
        down.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition up = new ScaleTransition(Duration.millis(120), node);
        up.setToX(1);
        up.setToY(1);
        up.setInterpolator(Interpolator.EASE_OUT);

        new SequentialTransition(down, up).play();
    }

    public static void cardHoverEnter(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(180), node);
        st.setToX(1.018);
        st.setToY(1.018);
        st.setInterpolator(Interpolator.EASE_OUT);
        st.play();
    }

    public static void cardHoverExit(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(180), node);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(Interpolator.EASE_OUT);
        st.play();
    }

    public static void flashError(Node node) {
        String original = node.getStyle();
        node.setStyle(original + "-fx-border-color:#F87171;-fx-border-width:2px;");
        PauseTransition pause = new PauseTransition(Duration.millis(2000));
        pause.setOnFinished(e -> node.setStyle(original));
        pause.play();
    }

    public static void flashSuccess(Node node) {
        String original = node.getStyle();
        node.setStyle(original + "-fx-border-color:#34D399;-fx-border-width:2px;");
        PauseTransition pause = new PauseTransition(Duration.millis(2000));
        pause.setOnFinished(e -> node.setStyle(original));
        pause.play();
    }

    public static void bounce(Node node) {
        TranslateTransition t1 = new TranslateTransition(Duration.millis(100), node);
        t1.setToY(-14);
        t1.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition t2 = new TranslateTransition(Duration.millis(100), node);
        t2.setToY(0);
        t2.setInterpolator(Interpolator.EASE_IN);

        TranslateTransition t3 = new TranslateTransition(Duration.millis(70), node);
        t3.setToY(-7);
        t3.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition t4 = new TranslateTransition(Duration.millis(70), node);
        t4.setToY(0);
        t4.setInterpolator(Interpolator.EASE_IN);

        new SequentialTransition(t1, t2, t3, t4).play();
    }

    // Staggered entrance for stat cards
    public static void staggerFadeIn(java.util.List<Node> nodes, int delayMs) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            node.setOpacity(0);
            node.setTranslateY(20);
            node.setScaleX(0.96);
            node.setScaleY(0.96);

            final int idx = i;
            PauseTransition delay = new PauseTransition(Duration.millis(idx * delayMs));
            delay.setOnFinished(e -> {
                FadeTransition ft = new FadeTransition(Duration.millis(320), node);
                ft.setFromValue(0);
                ft.setToValue(1);

                TranslateTransition tt = new TranslateTransition(Duration.millis(320), node);
                tt.setFromY(20);
                tt.setToY(0);

                ScaleTransition st = new ScaleTransition(Duration.millis(320), node);
                st.setFromX(0.96);
                st.setToX(1.0);
                st.setFromY(0.96);
                st.setToY(1.0);

                ParallelTransition pt = new ParallelTransition(ft, tt, st);
                pt.setInterpolator(Interpolator.EASE_OUT);
                pt.play();
            });
            delay.play();
        }
    }

    public static StackPane createLoadingOverlay(String message) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(6,9,15,0.75);");

        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(220);
        box.setMaxHeight(160);
        box.setStyle(
                "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #0B1226, #0E1530);" +
                        "-fx-background-radius: 16px;" +
                        "-fx-border-color: rgba(255,107,157,0.35);" +
                        "-fx-border-width: 1.5px;" +
                        "-fx-border-radius: 16px;" +
                        "-fx-padding: 32 36;" +
                        "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.7),28,0.4,0,8);"
        );

        ProgressIndicator spinner = new ProgressIndicator(-1);
        spinner.setPrefSize(48, 48);
        spinner.setStyle(
                "-fx-progress-color: #FF6B9D;" +
                        "-fx-accent: #FF6B9D;"
        );

        Label brand = new Label("VESPERA");
        brand.setAlignment(Pos.CENTER);
        brand.setMaxWidth(Double.MAX_VALUE);
        brand.setStyle(
                "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #FFB86B;" +
                        "-fx-letter-spacing: 3px;" +
                        "-fx-opacity: 0.7;"
        );

        Label lbl = new Label(message);
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(200);
        lbl.setStyle(
                "-fx-text-fill: #94A3B8;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: 'Segoe UI', Arial;" +
                        "-fx-text-alignment: center;"
        );
        lbl.setWrapText(true);

        box.getChildren().addAll(spinner, brand, lbl);
        overlay.getChildren().add(box);
        overlay.setOpacity(0);

        FadeTransition ft = new FadeTransition(Duration.millis(240), overlay);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return overlay;
    }

    public static void hideOverlay(StackPane overlay, Pane parent) {
        FadeTransition ft = new FadeTransition(Duration.millis(200), overlay);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> parent.getChildren().remove(overlay));
        ft.play();
    }

    public static void animateCounter(Label label, double target, String prefix, String suffix) {
        Timeline tl = new Timeline();
        int frames = 24;
        for (int i = 1; i <= frames; i++) {
            double progress = 1.0 - Math.pow(1.0 - (double) i / frames, 2);
            final double val = target * progress;
            tl.getKeyFrames().add(new KeyFrame(Duration.millis(i * 35), e ->
                    label.setText(prefix + String.format("%.0f", val) + suffix)
            ));
        }
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(frames * 35 + 50), e ->
                label.setText(prefix + String.format("%.0f", target) + suffix)
        ));
        tl.play();
    }

    public static void animateMoney(Label label, double target) {
        Timeline tl = new Timeline();
        int frames = 24;
        for (int i = 1; i <= frames; i++) {
            double progress = 1.0 - Math.pow(1.0 - (double) i / frames, 2);
            final double val = target * progress;
            tl.getKeyFrames().add(new KeyFrame(Duration.millis(i * 35), e ->
                    label.setText(String.format("$%.2f", val))
            ));
        }
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(frames * 35 + 50), e ->
                label.setText(String.format("$%.2f", target))
        ));
        tl.play();
    }

    // Shimmer skeleton loading pulse
    public static Timeline createShimmer(Node node) {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.opacityProperty(), 0.4)),
                new KeyFrame(Duration.millis(700),
                        new KeyValue(node.opacityProperty(), 0.9, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1400),
                        new KeyValue(node.opacityProperty(), 0.4, Interpolator.EASE_BOTH))
        );
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }

    // Glow pulse for status indicators
    public static Timeline createGlowPulse(Node node) {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.opacityProperty(), 0.6)),
                new KeyFrame(Duration.millis(900),
                        new KeyValue(node.opacityProperty(), 1.0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1800),
                        new KeyValue(node.opacityProperty(), 0.6, Interpolator.EASE_BOTH))
        );
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }

    // Continuous rotation for loading icons
    public static RotateTransition createContinuousRotation(Node node) {
        RotateTransition rt = new RotateTransition(Duration.millis(1200), node);
        rt.setByAngle(360);
        rt.setCycleCount(RotateTransition.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        return rt;
    }

    public static void setupHoverScale(Node node, double scaleFactor) {
        node.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(160), node);
            st.setToX(scaleFactor);
            st.setToY(scaleFactor);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        node.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(160), node);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
    }

    // Typewriter text reveal
    public static void typewriter(Label label, String text, int msPerChar) {
        label.setText("");
        Timeline tl = new Timeline();
        for (int i = 1; i <= text.length(); i++) {
            final String partial = text.substring(0, i);
            tl.getKeyFrames().add(new KeyFrame(
                    Duration.millis(i * msPerChar),
                    e -> label.setText(partial)
            ));
        }
        tl.play();
    }

    // Ripple click feedback
    public static void ripple(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.04);
        st.setToY(1.04);
        st.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition st2 = new ScaleTransition(Duration.millis(200), node);
        st2.setFromX(1.04);
        st2.setFromY(1.04);
        st2.setToX(1.0);
        st2.setToY(1.0);
        st2.setInterpolator(Interpolator.EASE_IN);

        new SequentialTransition(st, st2).play();
    }
}
