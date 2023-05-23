module Test{
    requires javafx.controls;
    requires javafx.graphics;
    requires kotlin.stdlib;
    requires tornadofx;
    requires java.desktop;
    requires commons.io;
    requires opencv;

    opens com.example.demo.app;
    opens com.example.demo.view;
}