module com.example.patron {
    requires transitive javafx.controls;
    requires javafx.fxml;


    opens com.example.patron to javafx.fxml;
    exports com.example.patron;
}