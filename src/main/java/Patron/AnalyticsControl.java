package Patron;

import Moduls.TableViewItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;

public class AnalyticsControl {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    public TableView<TableViewItem> AnalyticTable;
    @FXML
    public VBox AmalyticVBox;
    @FXML
    public Button SwitchScene;
    @FXML
    public Button Back;
    public void switchToMainScene(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ManagerUi.fxml")));
        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void back(javafx.event.ActionEvent actionEvent) throws IOException {
        SwitchScene.setVisible(true);
        AmalyticVBox.setVisible(true);
        Back.setVisible(false);
        AnalyticTable.setVisible(false);
    }
    public void findFile(javafx.event.ActionEvent actionEvent) throws  IOException {
        Back.setVisible(true);
        AnalyticTable.setVisible(true);
        SwitchScene.setVisible(false);
        AmalyticVBox.setVisible(false);
    }
}

