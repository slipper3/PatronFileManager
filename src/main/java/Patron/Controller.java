package Patron;

import Moduls.TableViewItem;
import Moduls.TreeViewItem;
import Moduls.myUtils;
import Moduls.TreeCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.*;
import java.util.List;

import static Moduls.myUtils.*;

import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;


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
    @FXML
    public ProgressBar progressBar;
    public Stage stage;
    public Scene scene;
    private static Thread thread = new Thread();
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
        rootItem.setExpanded(true);
        // Заповнюємо дерево накопичувачами, підключеними до комп'ютера
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File drive : roots) {
                TreeItem<TreeViewItem> driveItem = new TreeItem<>(new TreeViewItem(drive));
                rootItem.getChildren().add(driveItem);
                // Поширюємо дочірні елементи на одну ступінь глибше
                populateTreeView(driveItem);
            }
        }
        // Встановлюємо кореневий елемент в TreeView
        treeView.setRoot(rootItem);
        // Встановлюємо фабрику для відображення елементів TreeView
        treeView.setCellFactory(param -> new TreeCell());
    }
    /**Популізація дерева файлів*/
    public void populateTreeView(TreeItem<TreeViewItem> parent) {
        if (parent != null && parent.getChildren().isEmpty()) {
            File[] files = parent.getValue().getFile().listFiles();
            if (files != null)
                for (File file : files)
                    if (file.isDirectory() && !isExcluded(file)) {
                        TreeItem<TreeViewItem> child = new TreeItem<>(new TreeViewItem(file));
                        parent.getChildren().add(child);
                        System.out.println("additional children added");
                    }
        }
    }
    private void refreshTreeView(TreeItem<TreeViewItem> root, File currentDir) {
        for(TreeItem<TreeViewItem> child : root.getChildren()){
            if(child.getValue().getFile().isDirectory() && child.getValue().getFile().getPath().equals(currentDir.getPath())){
                child.getChildren().clear();
                File[] files = child.getValue().getFile().listFiles();
                if (files != null)
                    for (File file : files)
                        if(file.isDirectory() && !isExcluded(file)){
                            TreeItem<TreeViewItem> childItem = new TreeItem<>(new TreeViewItem(file));
                            child.getChildren().add(childItem);
                        }
            }
            else if (child.getValue().getFile().isDirectory())
                refreshTreeView(child, currentDir);
        }
    }
    public void TreeViewClicked(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1){
            TreeItem<TreeViewItem> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getChildren().isEmpty()) {
                File selectedDir = selectedItem.getValue().getFile();
                for(File file : Objects.requireNonNull(selectedDir.listFiles())){
                    if (file.isDirectory() && !isExcluded(file)) {
                        TreeItem<TreeViewItem> child = new TreeItem<>(new TreeViewItem(file));
                        selectedItem.getChildren().add(child);
                        // Поширюємо дочірні елементи на одну ступінь глибше
                        populateTreeView(child);
                    }
                }
            }
            if (selectedItem != null && selectedItem.getValue().getFile().isDirectory()) {
                previousFileDir.push(fileDir);
                fileDir = treeView.getSelectionModel().getSelectedItem().getValue().getFile();
                tableViewDraw(sortType);
            }
            //refreshTreeView();
        }

    }
    /**Відображення списку файлів та обробник кліків*/
    public void tableViewDraw(String sortType) {
        fileList.clear();
        File[] files = fileDir.listFiles();
        switch (sortType){
            case "name":
                Arrays.sort(Objects.requireNonNull(files), Comparator.comparing(myUtils::getLowName));
                break;
            case "creation-date":
                Arrays.sort(Objects.requireNonNull(files), Comparator.comparingLong(myUtils::getFileCreationEpoch));
                break;
            case "modify-date":
                Arrays.sort(Objects.requireNonNull(files), Comparator.comparing(File::lastModified));
                break;
            case "size":
                Arrays.sort(Objects.requireNonNull(files), Comparator.comparingLong(myUtils::calculateSizeInt));
                break;
        }
        for(File file : Objects.requireNonNull(files)){
            if (file.isDirectory() && !isExcluded(file)){ fileList.add(CreateTableViewItem(file)); }
        }
        for(File file : files){
            if (!file.isDirectory() && !isExcluded(file)){ fileList.add(CreateTableViewItem(file)); }
        }
        if(myUtils.IsDrive(fileDir)){
            FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            searchField.setPromptText("Пошук у "+fileSystemView.getSystemDisplayName(fileDir));
        }
        else searchField.setPromptText("Пошук у "+fileDir.getName());
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
    public void ReloadClicked(MouseEvent event) { tableViewDraw(sortType); refreshTreeView(treeView.getRoot(), fileDir); }
    public void HomeClick(MouseEvent event){
        fileDir = new File("D:\\");
        tableViewDraw(sortType);
        refreshTreeView(treeView.getRoot(), fileDir);
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
                if(!theDir.mkdirs()){ System.out.println("The folder was not created.");}
                tableViewDraw(sortType);
                refreshTreeView(treeView.getRoot(), fileDir);
            }
            else{
                Alert warning = myUtils.setAlert("WARNING", "WARNING","Така папка вже існує");
                Objects.requireNonNull(warning).showAndWait();
            }
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
                refreshTreeView(treeView.getRoot(), fileDir);
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
        refreshTreeView(treeView.getRoot(), fileDir);
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
        refreshTreeView(treeView.getRoot(), fileDir);
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
            refreshTreeView(treeView.getRoot(), fileDir);
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
        if(!thread.isAlive()){
            thread = new Thread(() -> {
                textField.setVisible(false);
                progressBar.setVisible(true);
                myUtils.searchResult.clear();
                String request = searchField.getCharacters().toString();
                List<File> foundFiles = findFilesByName(fileDir, request);
                foundFilesDraw(foundFiles, request);
            });
            thread.start();
        }
    }
    public void SearchFieldEvent(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER && !thread.isAlive()){
            thread = new Thread(() -> {
                textField.setVisible(false);
                progressBar.setVisible(true);
                myUtils.searchResult.clear();
                String request = searchField.getCharacters().toString();
                List<File> foundFiles = findFilesByName(fileDir, request);
                foundFilesDraw(foundFiles, request);
            });
            thread.start();
        }
    }
    public void foundFilesDraw(List<File> foundedFilesList, String request){
        fileList.clear();
        textField.setVisible(true);
        progressBar.setVisible(false);
        textField.setText("Результат пошуку по запиту: "+"\""+request+"\"");
        for (File file : foundedFilesList){
            fileList.add(CreateTableViewItem(file));
        }
        tableView.setItems(fileList);
        tableView.setVisible(true);
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
        //if (sortType.equals("size"))

        tableViewDraw(sortType="size");
    }
    public TableViewItem CreateTableViewItem(File file){
        return new TableViewItem(file, calculateSize(file), getModified(file));
    }
}