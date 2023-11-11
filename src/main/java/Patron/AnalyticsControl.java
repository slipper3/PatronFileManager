package Patron;

import Moduls.FileTypes;
import Moduls.TableViewItem;
import Moduls.Drive;

import Moduls.Utility;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class AnalyticsControl implements Initializable {
    public Stage stage;
    public Scene scene;
    private ObservableList<PieChart.Data> pieChartData;
    private ObservableList<TableViewItem> tableItems = FXCollections.observableArrayList();
    private Utility utility;
    @FXML
    public Button imageButton;
    @FXML
    public Button documentButton;
    @FXML
    public Button archiveButton;
    @FXML
    public Button musicButton;
    @FXML
    public Button videoButton;
    @FXML
    public Button appButton;
    @FXML
    public ComboBox<Drive> driveCombo;
    @FXML
    public TableView<TableViewItem> AnalyticTable;
    @FXML
    public TableColumn<TableViewItem, Button> nameAnalytic;
    @FXML
    public  TableColumn<TableViewItem, String> sizeAnalytic;
    @FXML
    public  TableColumn<TableViewItem, String> dateAnalytic;
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
        AnalyticTable.getItems().clear();
        tableItems.clear();
        utility.clearFileList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        utility = new Utility();
        AnalyticTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        nameAnalytic.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        sizeAnalytic.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        dateAnalytic.setCellValueFactory(new PropertyValueFactory<>("modDate"));
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

    public void drawFiles(ActionEvent event) {
        Button actionButton = (Button) event.getSource();
        if (driveCombo.getSelectionModel().getSelectedItem() != null) {

            if(actionButton.equals(imageButton)){
                Thread thread = new Thread(() -> {
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Images");
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        Back.setVisible(true);
                        AnalyticTable.setVisible(true);
                        SwitchScene.setVisible(false);
                        AnalyticVBox.setVisible(false);
                    });
                });
                thread.start();
            } else if (actionButton.equals(documentButton)){
                Thread thread = new Thread(() -> {
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Documents");
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        Back.setVisible(true);
                        AnalyticTable.setVisible(true);
                        SwitchScene.setVisible(false);
                        AnalyticVBox.setVisible(false);
                    });
                });
                thread.start();
            } else if (actionButton.equals(videoButton)) {
                Thread thread = new Thread(() -> {
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Videos");
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        Back.setVisible(true);
                        AnalyticTable.setVisible(true);
                        SwitchScene.setVisible(false);
                        AnalyticVBox.setVisible(false);
                    });
                });
                thread.start();
            } else if (actionButton.equals(archiveButton)){
                Thread thread = new Thread(() -> {
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Archives");
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        Back.setVisible(true);
                        AnalyticTable.setVisible(true);
                        SwitchScene.setVisible(false);
                        AnalyticVBox.setVisible(false);
                    });
                });
                thread.start();
            } else if (actionButton.equals(appButton)){
                Thread thread = new Thread(() -> {
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "App");
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        Back.setVisible(true);
                        AnalyticTable.setVisible(true);
                        SwitchScene.setVisible(false);
                        AnalyticVBox.setVisible(false);
                    });
                });
                thread.start();
            } else if (actionButton.equals(musicButton)){
                Thread thread = new Thread(() -> {
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Music");
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        Back.setVisible(true);
                        AnalyticTable.setVisible(true);
                        SwitchScene.setVisible(false);
                        AnalyticVBox.setVisible(false);
                    });
                });
                thread.start();
            }


        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mandatory");
            alert.setContentText("Please select drive");
            alert.show();
            driveCombo.requestFocus();
        }
        System.out.println("Done!");
    }

    public void tableViewClicked(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount() == 2){
                File f = AnalyticTable.getSelectionModel().getSelectedItem().getFile();
                if(f.isFile()) {
                    OpenFile(f);
                }
            }
        }
    }

    private void OpenFile(File f){
        if (!Desktop.isDesktopSupported()) {
            System.out.println("Desktop is not supported");
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        if (f.exists()) {
            try {
                desktop.open(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

