package Patron;

import Moduls.TableViewItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Stack;

import static javafx.embed.swing.SwingFXUtils.toFXImage;

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
    ObservableList<TableViewItem> fileList = FXCollections.observableArrayList();

    @FXML
    TreeView treeView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fileDir = new File("C:\\");
        tableView.setOnMouseClicked(this::tableViewDraw);
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
    public void tableViewDraw(MouseEvent event){
        fileList.clear();
        for (File file : Objects.requireNonNull(fileDir.listFiles())){
            fileList.add(new TableViewItem(file, calculateSize(file), getDate(file)));
        }
        tableView.setItems(fileList);
    }
    public void tableViewDraw(){
        fileList.clear();
        for (File file : Objects.requireNonNull(fileDir.listFiles())){
            fileList.add(new TableViewItem(file, calculateSize(file), getDate(file)));
        }
        tableView.setItems(fileList);
    }
    public void switchToAnalytics(javafx.event.ActionEvent actionEvent) throws IOException {
        /*Функція перехіду з основної сцени в сцену з аналітикою*/
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Analytics.fxml")));
        stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void BackClick(javafx.event.ActionEvent actionEvent){
        if(previousFileDir.isEmpty()){return;}
        forwardFileDir.push(fileDir);
        fileDir = previousFileDir.pop();
        tableViewDraw();
    }
    public void ForwardClick(javafx.event.ActionEvent actionEvent){
        if(forwardFileDir.isEmpty()){return;}
        previousFileDir.push(fileDir);
        fileDir = forwardFileDir.pop();
        tableViewDraw();
    }
    public void HomeClick(javafx.event.ActionEvent actionEvent){}
    /**Споміжні функції для виводу списку файлів*/
    public String calculateSize(File f){
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
    public String getDate(File f){
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
    public boolean IsDrive(File f){
        /* Перевіряємо отриманий файл на диск ;l
         * systemRoots присвоюємо ...
         * в циклі перевіряємо чи відповідає ...*/
        File[] systemRoots = File.listRoots();
        for (File systemRoot : systemRoots) {
            if (f.equals(systemRoot)) return true;
        }
        return false;
    }
    public boolean IsFolder(File f){
        Path path = Path.of(f.getPath());
        return Files.isDirectory(path);
    }
    public Image getIconImageFX(File f) {
        /*
         * Метод для отримання системних іконок для дисків, папок, файлів
         *
         * Функція отримує деякий файл, робить запит
         *      ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon("отриманий файл");
         * Але цей метод повертає картинку 16х16
         *
         * Можна використовувати
         *      Icon icon = new ImageIcon(ShellFolder.getShellFolder(new File(FILENAME)).getIcon(true));
         * Що поверне картинку 16х16 при .getIcon і 32х32 при .getLargeIcon
         *
         *      java.awt.Image img = icon.getImage();
         * icon.getImage() - повертає картинку з іконки, java.awt. - використовується для масштабування картинки
         *
         * Клас BufferedImage використовується для обробки та керування данними картинки
         *
         *  Image imgFx = toFXImage(bImg,null);
         * Цей кусочок коду конвертує отриману картинку в javaFX картинку
         */
        ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(f);
        java.awt.Image img = icon.getImage();
        BufferedImage bImg = (BufferedImage) img;
        return toFXImage(bImg, null);
    }
}