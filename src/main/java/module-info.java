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
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;

    opens com.example.brickpaint to javafx.fxml;
    exports com.example.brickpaint;
}