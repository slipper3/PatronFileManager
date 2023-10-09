module com.example.patron {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.patron to javafx.fxml;
    exports com.example.patron;
}