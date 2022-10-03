module com.example.brickpaint {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires com.gluonhq.charm.glisten;
    requires SimpleDialogFX;
    requires javafx.media;
    requires org.controlsfx.controls;

    opens com.example.brickpaint to javafx.fxml;
    exports com.example.brickpaint;

}