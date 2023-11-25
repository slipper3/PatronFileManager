package Patron;

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
    private Button actionButton;
    private Thread thread = new Thread();
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
    public Button Back;
    @FXML
    public Label lblUsed;
    @FXML
    public Label lblFree;
    @FXML
    public ProgressBar progressBar;



    public void switchToMainScene(javafx.event.ActionEvent actionEvent) throws IOException {
        if (thread.isAlive()){
            thread.stop();
            driveCombo.setDisable(false);
            progressBar.setVisible(false);
            progressBar.setProgress(0);
            System.out.println("Thread has stopped!");
        }
        if (AnalyticTable.isVisible()){
            AnalyticTable.setVisible(false);
            AnalyticVBox.setVisible(true);
            utility.clearFileList();
            AnalyticTable.getItems().clear();
        } else {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ManagerUi.fxml")));
            stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setVisible(false);
        utility = new Utility();
        AnalyticTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        nameAnalytic.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        sizeAnalytic.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        dateAnalytic.setCellValueFactory(new PropertyValueFactory<>("modDate"));
        ArrayList<Drive> drives = utility.getAllDrives();
        driveCombo.getItems().addAll(drives);
    }

    public void loadChartData() {
        if (AnalyticTable.isVisible()){
            ActionEvent event = new ActionEvent(actionButton, null);
            utility.clearFileList();
            AnalyticTable.getItems().clear();
            drawFiles(event);
        }
        spaceChart.getData().clear();
        Drive drive = driveCombo.getSelectionModel().getSelectedItem();
        PieChart.Data usedData = new PieChart.Data("Використане місце " + drive.getUsedPer() + " %", drive.getDblUsedSpace());
        PieChart.Data remData = new PieChart.Data("Вільне місце " + drive.getRemPer() + " %", drive.getDblFreeSpace());
        spaceChart.getData().addAll(usedData, remData);
        lblUsed.setText(drive.getUsedSpace());
        lblFree.setText(drive.getFreeSpace());
    }

    public void drawFiles(ActionEvent event) {
        actionButton = (Button) event.getSource();

        if (driveCombo.getSelectionModel().getSelectedItem() != null && !thread.isAlive()) {
            driveCombo.setDisable(true);
            progressBar.setVisible(true);

            if(actionButton.equals(imageButton)){
                thread = new Thread(() -> {
                    System.out.println("Image button start");
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Images", progressBar);
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        AnalyticTable.setVisible(true);
                        AnalyticVBox.setVisible(false);
                        driveCombo.setDisable(false);
                        progressBar.setVisible(false);
                        progressBar.setProgress(0);
                    });
                });
                thread.start();
            } else if (actionButton.equals(documentButton)){
                thread = new Thread(() -> {
                    System.out.println("Document button start");
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Documents", progressBar);
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        AnalyticTable.setVisible(true);
                        AnalyticVBox.setVisible(false);
                        driveCombo.setDisable(false);
                        progressBar.setVisible(false);
                        progressBar.setProgress(0);
                    });
                });
                thread.start();
            } else if (actionButton.equals(videoButton)) {
                thread = new Thread(() -> {
                    System.out.println("Video button start");
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Videos", progressBar);
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        AnalyticTable.setVisible(true);
                        AnalyticVBox.setVisible(false);
                        driveCombo.setDisable(false);
                        progressBar.setVisible(false);
                        progressBar.setProgress(0);
                    });
                });
                thread.start();
            } else if (actionButton.equals(archiveButton)){
                thread = new Thread(() -> {
                    System.out.println("Archives button start");
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Archives", progressBar);
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        AnalyticTable.setVisible(true);
                        AnalyticVBox.setVisible(false);
                        driveCombo.setDisable(false);
                        progressBar.setVisible(false);
                        progressBar.setProgress(0);
                    });
                });
                thread.start();
            } else if (actionButton.equals(appButton)){
                thread = new Thread(() -> {
                    System.out.println("App button start");
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "App", progressBar);
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        AnalyticTable.setVisible(true);
                        AnalyticVBox.setVisible(false);
                        driveCombo.setDisable(false);
                        progressBar.setVisible(false);
                        progressBar.setProgress(0);
                    });
                });
                thread.start();
            } else if (actionButton.equals(musicButton)){
                thread = new Thread(() -> {
                    System.out.println("Music button start");
                    tableItems = utility.getFiles(
                            driveCombo.getSelectionModel().getSelectedItem().getFile().listFiles(), 0, "Music", progressBar);
                    Platform.runLater(() -> {
                        AnalyticTable.setItems(tableItems);
                        AnalyticTable.setVisible(true);
                        AnalyticVBox.setVisible(false);
                        driveCombo.setDisable(false);
                        progressBar.setVisible(false);
                        progressBar.setProgress(0);
                    });
                });
                thread.start();
            }


        } else if (driveCombo.getSelectionModel().getSelectedItem() == null) {
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

