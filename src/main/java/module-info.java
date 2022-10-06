module com.example.brickpaint {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires java.desktop;
    requires com.gluonhq.charm.glisten;
    requires javafx.media;
    requires org.controlsfx.controls;

    opens com.example.brickpaint to javafx.fxml;
    exports com.example.brickpaint;

}