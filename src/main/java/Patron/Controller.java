package Patron;

import Moduls.TableViewItem;
import Moduls.TreeViewItem;
import Moduls.myUtils;
import Moduls.TreeCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static Moduls.myUtils.*;

import org.apache.commons.io.FileUtils;


public class Controller implements Initializable {
    @FXML
    public TableView<TableViewItem> tableView;
    @FXML
    public TableColumn<TableViewItem, HBox> name;
    @FXML
    public TableColumn<TableViewItem, String> size;
    @FXML
    public TableColumn<TableViewItem, String> date;
    @FXML
    public TextField textField;
    @FXML
    public TreeView<TreeViewItem> treeView;
    @FXML
    public TextField searchField;
    public Stage stage;
    public Scene scene;
    public static File fileDir;
    public static Stack<File> previousFileDir = new Stack<>();
    public static Stack<File> forwardFileDir = new Stack<>();
    private final ObservableList<TableViewItem> fileList = FXCollections.observableArrayList();
    private final ObservableList<File> selectedFileList = FXCollections.observableArrayList();
    private String sortType = "none";
    private boolean isCut = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileDir = new File("D:\\");
        name.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        size.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        date.setCellValueFactory(new PropertyValueFactory<>("modDate"));
        tableViewDraw(sortType);

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
    /**Популізація дерева файлів*/
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
                tableViewDraw(sortType);
            }

        }
    }
    /**Відображення списку файлів та обробник кліків*/
    public void tableViewDraw(String sortType) {
        fileList.clear();
        File[] files = fileDir.listFiles();
        switch (sortType){
            case "none":
                //System.out.println("none");
                break;
            case "name":
                Arrays.sort(Objects.requireNonNull(files), Comparator.comparing(File::getName));
                break;
            case "creation-date":
                Arrays.sort(Objects.requireNonNull(files), Comparator.comparingLong(myUtils::getFileCreationEpoch));
                break;
            case "modify-date":
                Arrays.sort(Objects.requireNonNull(files), Comparator.comparing(File::lastModified));
                break;
            case "size":
                Arrays.sort(Objects.requireNonNull(files), Comparator.comparingLong(myUtils::calculateSizeInt));
                Collections.reverse(Arrays.asList(files));
                break;
        }
        for(File file : Objects.requireNonNull(files)){
            if (file.isDirectory()){ fileList.add(CreateTableViewItem(file)); }
        }
        for(File file : files){
            if (!file.isDirectory()){ fileList.add(CreateTableViewItem(file)); }
        }
        tableView.setItems(fileList);
        textField.setText(fileDir.toString());
    }
    public void tableViewClicked(MouseEvent event) {
        selectedFileList.clear();
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if (event.getClickCount() == 1){
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
                    tableViewDraw(sortType);
                }else {
                    OpenFile(f);
                }
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
        tableViewDraw(sortType);
    }
    public void ForwardClick(MouseEvent event){
        if(forwardFileDir.isEmpty()){return;}
        previousFileDir.push(fileDir);
        fileDir = forwardFileDir.pop();
        tableViewDraw(sortType);
    }
    public void DirUpClicked(MouseEvent event) {
        if(myUtils.IsDrive(fileDir)){return;}
        previousFileDir.push(fileDir);
        int lastSlash = fileDir.getPath().lastIndexOf('\\');
        StringBuilder sb = new StringBuilder(fileDir.getPath());
        for (int index = lastSlash+1; index < sb.toString().length();) { sb.deleteCharAt(index); }
        fileDir = new File(sb.toString());
        tableViewDraw(sortType);
    }
    public void TextFieldEvent(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            CharSequence ch = textField.getCharacters();
            File f = new File(ch.toString());
            if (f.exists()) {
                if (f.isDirectory()) {
                    previousFileDir.push(fileDir);
                    fileDir = f;
                    tableViewDraw(sortType);
                } else {
                    OpenFile(f);
                }
            }
        }
    }
    public void ReloadClicked(MouseEvent event) { tableViewDraw(sortType); }
    public void HomeClick(MouseEvent event){}
    public void CreateFolder(javafx.event.ActionEvent event){
        TextInputDialog dialog = new TextInputDialog("New folder");
        dialog.setTitle("Створеня папки");
        dialog.setHeaderText("");
        dialog.setContentText("Введіть назву папки:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            File theDir = new File(fileDir.getPath()+"\\"+result.get());
            if (!theDir.exists()) {
                if(!theDir.mkdirs()){ System.out.println("The folder was not created.");}
            }
            else{
                Alert warning = myUtils.setAlert("WARNING", "WARNING","Така папка вжеіснує");
                Objects.requireNonNull(warning).showAndWait();
            }
            tableViewDraw(sortType);
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
                tableViewDraw(sortType);
            }else{
                Alert warning = myUtils.setAlert("WARNING", "WARNING","Такий файл уже існує");
                Objects.requireNonNull(warning).showAndWait();
            }
        });
    }
    public void PasteClick(MouseEvent event) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            List<File> list = (List<File>) clipboard.getData(DataFlavor.javaFileListFlavor);
            PasteFileList(list);
        } catch (UnsupportedFlavorException | IOException e) {
            throw new RuntimeException(e);
        }
        tableViewDraw(sortType);
    }
    public void PasteFileList(List<File> files) {
        File destination = fileDir;
        try {
            for(File f : files) {
                if(f.isDirectory()){ FileUtils.copyDirectoryToDirectory(f, destination); }
                else { FileUtils.copyFileToDirectory(f, destination); }
                if(isCut){Desktop.getDesktop().moveToTrash(f);}
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tableViewDraw(sortType);
    }
    public void CutClick(MouseEvent event){
        List<File> copyFileList = new ArrayList<>(selectedFileList);
        copyFilesToClipboard(copyFileList);
        isCut = true;
    }
    public void CopyClick(MouseEvent event){
        isCut = false;
        List<File> copyFileList = new ArrayList<>(selectedFileList);
        copyFilesToClipboard(copyFileList);
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
            tableViewDraw(sortType);
        }
    }
    public void SelectAllClick(MouseEvent event){
        tableView.getSelectionModel().selectAll();
        ObservableList<TableViewItem> items = tableView.getSelectionModel().getSelectedItems();
        for(TableViewItem item : items){
            selectedFileList.add(item.getFile());
        }
    }
    /**Пошук файлів*/
    public void SearchClicked(MouseEvent event) throws IOException {
        String request = searchField.getCharacters().toString();
        List<Path> foundFiles = findFilesByName(Path.of(fileDir.getPath()), request);
        foundFilesDraw(foundFiles, request);
    }
    public void SearchFieldEvent(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER){
            String request = searchField.getCharacters().toString();
            List<Path> foundFiles = findFilesByName(Path.of(fileDir.getPath()), request);
            foundFilesDraw(foundFiles, request);
        }
    }
    public void foundFilesDraw(List<Path> foundedFilesList, String request){
        fileList.clear();
        for (Path path : foundedFilesList){
            fileList.add(CreateTableViewItem(path.toFile()));
        }
        tableView.setItems(fileList);
        textField.setText("Результат пошуку по запиту: "+"\""+request+"\"");
    }
    /**Сортування файлів*/
    public void SortByName(ActionEvent event) {
        tableViewDraw(sortType="name");
    }
    public void SortByCreationDate(ActionEvent event) {
        tableViewDraw(sortType="creation-date");
    }
    public void SortByModifyDate(ActionEvent event) {
        tableViewDraw(sortType="modify-date");
    }
    public void SortBySize(ActionEvent event) {
        tableViewDraw(sortType="size");
    }

    public TableViewItem CreateTableViewItem(File file){
        return new TableViewItem(file, calculateSize(file), getModified(file));
    }
}