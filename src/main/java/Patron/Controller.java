package Patron;

import Moduls.TableViewItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
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
    public ListView listView;
    @FXML
    public TableView<TableViewItem> tableView;
    @FXML
    public TableColumn<TableViewItem, Button> name;
    @FXML
    public  TableColumn<TableViewItem, String> size;
    @FXML
    public  TableColumn<TableViewItem, String> date;
    private final ObservableList<TableViewItem> fileList = FXCollections.observableArrayList();
    private final ObservableList<File> selectedFileList = FXCollections.observableArrayList();
    private final ObservableList<File> copiedFileList = FXCollections.observableArrayList();
    @FXML
    public ContextMenu cm;
    @FXML
    public TextField textField;

    @FXML
    TreeView treeView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileDir = new File("D:\\");
        name.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        size.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        date.setCellValueFactory(new PropertyValueFactory<>("modDate"));
        tableViewDraw();


        //Заготовка для дерева файлів
        treeView.setOnMouseClicked(event -> {
            System.out.println("Clicked");
        });

        File[] drivePath = File.listRoots();
        TreeItem<String> rootItem = new TreeItem<>("This computer");
        treeView.setRoot(rootItem);
        for (File file : drivePath) {
            TreeItem<String> drive = new TreeItem<>(file.toString());
            rootItem.getChildren().add(drive);
        }
    }
    /**Відображення списку файлів та обробник кліків*/
    public void tableViewDraw(){
        fileList.clear();
        for (File file : Objects.requireNonNull(fileDir.listFiles())){
            fileList.add(new TableViewItem(file, calculateSize(file), getDate(file)));
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
        if(IsDrive(fileDir)){return;}
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
    public void CreateClick(MouseEvent event){

    }
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
                Alert warning = setAlert("WARNING", "WARNING","Така папка вжеіснує");
                warning.showAndWait();
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
                Alert warning = setAlert("WARNING", "WARNING","Такий файл уже існує");
                warning.showAndWait();
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
        Alert alert = setAlert("CONFIRMATION", "Підтвердження видалення", "Перемістити в корзину?");
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
    /**Споміжні функції*/
    private Alert setAlert(String type, String title, String header){
        switch (type) {
            case "CONFIRMATION" -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(title);
                alert.setHeaderText(header);
                return alert;
            }
            case "ERROR" -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(header);
                return alert;
            }
            case "WARNING" -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(title);
                alert.setHeaderText(header);
                return alert;
            }
            case "INFORMATION" -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setHeaderText(header);
                return alert;
            }
        }
        return null;
    }
    private String calculateSize(File f){
        /*
         * Функція розрахунку ваги файлу
         */

        String s;
        long sizeInByte=0;
        Path path;

        /*Розрахунок ваги для диску*/
        if(IsDrive(f)){
            return f.getTotalSpace()/(1024*1024*1024) + "GB";
        }
        if(IsFolder(f)){
            return "";
        }
        //-------------------------//

        path = Paths.get(f.toURI()); // Отримуємо шлях до файлу
        //sizeInByte = f.getTotalSpace(); // terrible idea cz sob subfolder e 199GB, 99GB
        try {
            sizeInByte = Files.size(path);//at least works ^_^ отримуємо розмір файлу в байтах
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*В конструкції if, else if виконуємо перевірку розміру файла
         * і відповідн до розміру обираємо одиницю вимірювання (Б, КБ, МБ, ГБ)
         * та ділимо байти щоб отрмати значення відповідної величини*/
        if(sizeInByte<(1024)){
            s = sizeInByte + "B";  //Якощ розмір менше 1кб
            return s;
        }
        else if(sizeInByte<(1024*1024)){
            long sizeInKb = sizeInByte/1024; s = sizeInKb + "KB"; return s; //Якщо розмір більше 1кб але менше 1мб
        }
        else if(sizeInByte<(1024*1024*1024)){
            long sizeInMb = sizeInByte/(1024*1024); s = sizeInMb + "MB"; return s; //Якщо розмір більше 1мб але менше 1гб
        }
        else{
            long sizeInGb = sizeInByte/(1024*1024*1024); s = sizeInGb + "GB"; return s; //Якщо розмір більше 1гб
        }
    }
    private String getDate(File f){
        String dateCreated;
        try {
            BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
            FileTime fileTime = attr.creationTime();
            dateCreated = fileTime.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dateCreated;
    }
    private boolean IsDrive(File f){
        /* Перевіряємо отриманий файл на диск ;l
         * systemRoots присвоюємо ...
         * в циклі перевіряємо чи відповідає ...*/
        File[] systemRoots = File.listRoots();
        for (File systemRoot : systemRoots) {
            if (f.equals(systemRoot)) return true;
        }
        return false;
    }
    private boolean IsFolder(File f){
        Path path = Path.of(f.getPath());
        return Files.isDirectory(path);
    }
}