module Patron {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.commons.io;


    opens Patron to javafx.fxml;
    opens Moduls to javafx.base;
    exports Patron;
}