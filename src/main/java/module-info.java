module com.example.patron {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens Patron to javafx.fxml;
    exports Patron;
}