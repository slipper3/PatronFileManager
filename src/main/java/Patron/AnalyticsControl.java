package Patron;

import Moduls.TableViewItem;
import Moduls.Drive;

import Moduls.Utility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class AnalyticsControl implements Initializable {
    public Stage stage;
    public Scene scene;
    private ObservableList<PieChart.Data> pieChartData;
    @FXML
    public Button imageButton;
    @FXML
    public Button documentButton;
    @FXML
    public ComboBox<Drive> driveCombo;
    @FXML
    public TableView<TableViewItem> AnalyticTable;
    @FXML
    public PieChart spaceChart;
    @FXML
    public VBox AnalyticVBox;
    @FXML
    public Button SwitchScene;
    @FXML
    public Button Back;
    @FXML
    public Label lblUsed;
    @FXML
    public Label lblFree;


    public void switchToMainScene(javafx.event.ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ManagerUi.fxml")));
        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void back() {
        SwitchScene.setVisible(true);
        AnalyticVBox.setVisible(true);
        Back.setVisible(false);
        AnalyticTable.setVisible(false);
    }
    public void findFile(){
        Back.setVisible(true);
        AnalyticTable.setVisible(true);
        SwitchScene.setVisible(false);
        AnalyticVBox.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utility utility = new Utility();
        ArrayList<Drive> drives = utility.getAllDrives();
        driveCombo.getItems().addAll(drives);
    }

    public void loadChartData() {
        pieChartData = FXCollections.observableArrayList();
        Drive drive = driveCombo.getSelectionModel().getSelectedItem();
        PieChart.Data usedData = new PieChart.Data("Використане місце " + drive.getUsedPer() + " %", drive.getDblUsedSpace());
        PieChart.Data remData = new PieChart.Data("Вільне місце " + drive.getRemPer() + " %", drive.getDblFreeSpace());
        pieChartData.add(usedData);
        pieChartData.add(remData);
        spaceChart.setData(pieChartData);
        lblUsed.setText(drive.getUsedSpace());
        lblFree.setText(drive.getFreeSpace());
    }
}

