package Patron;

import Moduls.TableViewItem;
import Moduls.TreeViewItem;
import Moduls.myUtils;
import Moduls.TreeCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;


public class Controller implements Initializable {
    public Stage stage;
    public Scene scene;
    public static File fileDir;
    public static Stack<File> previousFileDir = new Stack<>();
    public static Stack<File> forwardFileDir = new Stack<>();
    @FXML
    public TableView<TableViewItem> tableView;
    @FXML
    public TableColumn<TableViewItem, HBox> name;
    @FXML
    public TableColumn<TableViewItem, String> size;
    @FXML
    public TableColumn<TableViewItem, String> date;
    private final ObservableList<TableViewItem> fileList = FXCollections.observableArrayList();
    private final ObservableList<File> selectedFileList = FXCollections.observableArrayList();
    private final ObservableList<File> copiedFileList = FXCollections.observableArrayList();
    @FXML
    public ContextMenu cm;
    @FXML
    public TextField textField;
    @FXML
    public TreeView<TreeViewItem> treeView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileDir = new File("D:\\");
        name.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        size.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        date.setCellValueFactory(new PropertyValueFactory<>("modDate"));
        tableViewDraw();

        // Створюємо кореневий елемент TreeView
        TreeItem<TreeViewItem> rootItem = new TreeItem<>(new TreeViewItem(new File("This PC")));
        // Заповнюємо дерево накопичувачами, підключеними до комп'ютера
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File drive : roots) {
                TreeItem<TreeViewItem> driveItem = new TreeItem<>(new TreeViewItem(drive));
                rootItem.getChildren().add(driveItem);
            }
        }
        // Встановлюємо кореневий елемент в TreeView
        treeView.setRoot(rootItem);
        // Встановлюємо фабрику для відображення елементів TreeView
        treeView.setCellFactory(param -> new TreeCell());

    }

    public void TreeViewClicked(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1){
            TreeItem<TreeViewItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getChildren().isEmpty()) {
                File selectedDir = selectedItem.getValue().getFile();
                for(File file : Objects.requireNonNull(selectedDir.listFiles())){
                    if (file.isDirectory()) { selectedItem.getChildren().add(new TreeItem<>(new TreeViewItem(file))); }
                }
            }
            if (selectedItem != null && selectedItem.getValue().getFile().isDirectory()) {
                previousFileDir.push(fileDir);
                fileDir = treeView.getSelectionModel().getSelectedItem().getValue().getFile();
                tableViewDraw();
            }

        }
    }
    /**Відображення списку файлів та обробник кліків*/
    public void tableViewDraw(){
        fileList.clear();
        for (File file : Objects.requireNonNull(fileDir.listFiles())){
            fileList.add(new TableViewItem(file, myUtils.calculateSize(file), myUtils.getDate(file)));
        }
        tableView.setItems(fileList);
        textField.setText(fileDir.toString());
    }
    public void tableViewClicked(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if (event.getClickCount() == 1){
                selectedFileList.clear();
                ObservableList<TableViewItem> items = tableView.getSelectionModel().getSelectedItems();
                for(TableViewItem item : items){
                    selectedFileList.add(item.getFile());
                }
            }
            if(event.getClickCount() == 2){
                File f = tableView.getSelectionModel().getSelectedItem().getFile();
                if(f.isDirectory()) {
                    Controller.previousFileDir.push(Controller.fileDir);
                    Controller.fileDir = f;
                    tableViewDraw();
                }else {
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
    /**Обробники подій для кнопок*/
    public void switchToAnalytics(MouseEvent event) throws IOException {
        /*Функція перехіду з основної сцени в сцену з аналітикою*/
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Analytics.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void BackClick(MouseEvent event){
        if(previousFileDir.isEmpty()){return;}
        forwardFileDir.push(fileDir);
        fileDir = previousFileDir.pop();
        tableViewDraw();
    }
    public void ForwardClick(MouseEvent event){
        if(forwardFileDir.isEmpty()){return;}
        previousFileDir.push(fileDir);
        fileDir = forwardFileDir.pop();
        tableViewDraw();
    }
    public void DirUpClicked(MouseEvent event) {
        if(myUtils.IsDrive(fileDir)){return;}
        previousFileDir.push(fileDir);
        int lastSlash = fileDir.getPath().lastIndexOf('\\');
        StringBuilder sb = new StringBuilder(fileDir.getPath());
        for (int index = lastSlash+1; index < sb.toString().length();) { sb.deleteCharAt(index); }
        fileDir = new File(sb.toString());
        tableViewDraw();
    }
    public void TextFieldEvent(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            CharSequence ch = textField.getCharacters();
            File f = new File(ch.toString());
            if (f.exists()) {
                if (f.isDirectory()) {
                    previousFileDir.push(fileDir);
                    fileDir = f;
                    tableViewDraw();
                } else {
                    OpenFile(f);
                }
            }
        }
    }
    public void ReloadClicked(MouseEvent event) { tableViewDraw(); }
    public void SearchClicked(MouseEvent event) {}
    public void HomeClick(MouseEvent event){}
    public void CreateClick(MouseEvent event){}
    public void CreateFolder(javafx.event.ActionEvent event){
        TextInputDialog dialog = new TextInputDialog("New folder");
        dialog.setTitle("Створеня папки");
        dialog.setHeaderText("");
        dialog.setContentText("Введіть назву папки:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            File theDir = new File(fileDir.getPath()+"\\"+result.get());
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            else{
                Alert warning = myUtils.setAlert("WARNING", "WARNING","Така папка вжеіснує");
                Objects.requireNonNull(warning).showAndWait();
            }
            tableViewDraw();
        });
    }
    public void CreateFile(javafx.event.ActionEvent event){
        TextInputDialog dialog = new TextInputDialog("New file.txt");
        dialog.setTitle("Створеня файлу");
        dialog.setHeaderText("");
        dialog.setContentText("Введіть назву файла та розширення (назва.txt):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            File file = new File(fileDir.getPath()+"\\"+result.get());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                tableViewDraw();
            }else{
                Alert warning = myUtils.setAlert("WARNING", "WARNING","Такий файл уже існує");
                Objects.requireNonNull(warning).showAndWait();
            }
        });
    }
    public void PasteClick(MouseEvent event) throws IOException {
        for(File f : copiedFileList){
            if(f.isDirectory()){
                System.out.println("Поки не можу копіюввати папки");
            }
            else {
                String copyPath = fileDir.getPath() + "\\" + f.getName();
                String originPath = f.getPath();
                Files.copy(Path.of(originPath), Path.of(copyPath), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        tableViewDraw();
    }
    public void CutClick(MouseEvent event){
        copiedFileList.clear();
        copiedFileList.addAll(selectedFileList);
        for (File f : selectedFileList) {
            Desktop.getDesktop().moveToTrash(f);
        }
        selectedFileList.clear();
        tableViewDraw();
    }
    public void CopyClick(MouseEvent event){
        copiedFileList.clear();
        copiedFileList.addAll(selectedFileList);
    }
    public void DeleteClick(MouseEvent event){
        Alert alert = myUtils.setAlert("CONFIRMATION", "Підтвердження видалення", "Перемістити в корзину?");
        ButtonType buttonYes = new ButtonType("Так");
        ButtonType buttonCancel = new ButtonType("Відмінити", ButtonBar.ButtonData.CANCEL_CLOSE);
        assert alert != null;
        alert.getButtonTypes().setAll(buttonYes, buttonCancel);
        alert.showAndWait();

        if(alert.getResult() == buttonYes) {
            for (File f : selectedFileList) {
                Desktop.getDesktop().moveToTrash(f);
            }
            selectedFileList.clear();
            tableViewDraw();
        }
    }
    public void SelectAllClick(MouseEvent event){
        ObservableList<TableViewItem> items = tableView.getSelectionModel().getSelectedItems();
        for(TableViewItem item : items){
            selectedFileList.add(item.getFile());
        }
    }
//    public void TreeViewClicked(MouseEvent event) {
//        //System.out.println(treeView.getSelectionModel().getSelectedItem().getValue());
//    }
}