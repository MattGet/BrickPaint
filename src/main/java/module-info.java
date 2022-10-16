module com.example.brickpaint {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.fxml;
    requires org.apache.commons.io;
    requires org.controlsfx.controls;

    opens com.example.brickpaint to javafx.fxml;
    exports com.example.brickpaint;
}