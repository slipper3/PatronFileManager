package Patron;

import Moduls.TableViewItem;
import Moduls.TreeViewClass;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static javafx.embed.swing.SwingFXUtils.toFXImage;

public class Controller implements Initializable {
    static TreeViewClass Fx1;
    @FXML
    public ListView listView;
    @FXML
    public TableView<TableViewItem> tableView;
    @FXML
    public TableColumn<TableViewItem, String> name;
    @FXML
    public  TableColumn<TableViewItem, String> size;
    @FXML
    public  TableColumn<TableViewItem, String> date;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<TableViewItem> fileList = FXCollections.observableArrayList(
                new TableViewItem("name", "size", "date")
        );


        var StartDirectory = Paths.get(System.getProperty("user.dir"));
        //Path StartDirectory = Path.of("D:\\kpi\\2kurs\\kursova\\kod\\Patron\\src\\main\\resources\\Images");
        /** Тут виводиться список папок певної директорії в об'єкт ListView
         * Код успішно працює*/
//        try {
//            Files.walk(StartDirectory)
//                    .filter(path -> Files.isDirectory(path))
//                    .forEach(dir -> {
//                        listView.getItems().add(new HBox(new TextField(dir.getFileName().toString()), new Label(calculateSize(dir.toFile()))));
//                    });
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        /** А в цьому кусочку я намагаюсь вивести список файлфв у вигляді таблиці
         * Код не працює з невідомої мені причини
         * Переглянутий контент:
         * TableView - https://www.youtube.com/watch?v=fnU1AlyuguE
         * Files.walk - https://www.youtube.com/watch?v=JlibW-BJ6I4&t=362s
         * ListView - https://www.youtube.com/watch?v=Pqfd4hoi5cc&t=74s */
        name.setCellValueFactory(new PropertyValueFactory<TableViewItem, String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<TableViewItem, String>("size"));
        date.setCellValueFactory(new PropertyValueFactory<TableViewItem, String>("date"));

//        try {
//            Files.walk(StartDirectory)
//                    .filter(path -> Files.isDirectory(path))
//                    .forEach(path -> {
//                        fileList.add(new TableViewItem(path.toString(), calculateSize(path.toFile()), "date created"));
//                        //tableView.getItems().add(new TableViewItem(path.toString(), calculateSize(path.toFile()), "date created"));
//                    });
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        //tableView.getItems().addAll(fileList);
        tableView.setItems(fileList);
    }

    @FXML
    public void test(){
        System.out.println("done");
    }


    /**Тут додав готові функції з прикладу */
    public String calculateSize(File f){
        /**
         * Функція розрахунку ваги файлу
         */

        String s;
        long sizeInByte=0;
        Path path;

        /**Розрахунок ваги для диску*/
        if(IsDrive(f)){
            return Long.toString(f.getTotalSpace()/(1024*1024*1024))+"GB";
        }
        //-------------------------//

        path = Paths.get(f.toURI()); // Отримуємо шлях до файлу
        //sizeInByte = f.getTotalSpace(); // terrible idea cz sob subfolder e 199GB, 99GB esob dekhay >_<
        try {
            sizeInByte = Files.size(path);//at least works ^_^ отримуємо розмір файлу в байтах
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**В конструкції if, else if виконуємо перевірку розміру файла
         * і відповідн до розміру обираємо одиницю вимірювання (Б, КБ, МБ, ГБ)
         * та ділимо байти щоб отрмати значення відповідної величини*/
        if(sizeInByte<(1024)){
            s = Long.toString(sizeInByte)+"B";  //Якощ розмір менше 1кб
            return s;
        }
        else if(sizeInByte>=(1024) && sizeInByte<(1024*1024)){
            long sizeInKb = sizeInByte/1024; s = Long.toString(sizeInKb)+"KB"; return s; //Якщо розмір більше 1кб але менше 1мб
        }
        else if(sizeInByte>=(1024*1024) && sizeInByte<(1024*1024*1024)){
            long sizeInMb = sizeInByte/(1024*1024); s = Long.toString(sizeInMb)+"MB"; return s; //Якщо розмір більше 1мб але менше 1гб
        }
        else if(sizeInByte>=(1024*1024*1024)){
            long sizeInGb = sizeInByte/(1024*1024*1024); s = Long.toString(sizeInGb)+"GB"; return s; //Якщо розмір більше 1гб
        }

        return null;
    }
    public boolean IsDrive(File f){
        /** Перевіряємо отриманий файл на диск ;l
         * sysroots присвоюємо ...
         * в циклі перевіряємо чи відповідає ...*/
        File[] sysroots = File.listRoots();
        for(int i=0; i<sysroots.length;i++){
            if(f.equals(sysroots[i])) return true;
        }
        return false;
    }

    public Image getIconImageFX(File f) {
        /**
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
         *  Image imgfx = toFXImage(bimg,null);
         * Цей кусочок коду конвертує отриману картинку в javaFX картинку
         */
        ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(f);
        java.awt.Image img = icon.getImage();
        BufferedImage bimg = (BufferedImage) img;
        Image imgfx = toFXImage(bimg, null);
        return imgfx;
    }
}