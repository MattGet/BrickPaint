module com.brickpaint2 {
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
    requires com.google.guice;
    requires io.reactivex.rxjava2;
    requires jama;

    opens com.brickpaint2.jmonet.tools to com.google.guice;
    opens com.brickpaint2.jmonet.tools.base to com.google.guice;
    opens com.brickpaint2 to javafx.fxml;
    exports com.brickpaint2.jmonet.tools.attributes to com.google.guice;
    exports com.brickpaint2.jmonet.tools.cursors to com.google.guice;
    exports com.brickpaint2;
}