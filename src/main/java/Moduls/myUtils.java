package Moduls;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class myUtils {
    public myUtils(){}
    public static Image IconToImage(Icon icon){
        icon = new SafeIcon(icon);
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        icon.paintIcon(new JPanel(), image.getGraphics(), 0, 0);
        Image img = SwingFXUtils.toFXImage(image, null);
        return img;
    }
    public static Alert setAlert(String type, String title, String header){
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
    public static String calculateSize(File f){
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
    public static String getDate(File f){
        String dateCreated;
        try {
            BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
            FileTime fileTime = attr.creationTime();
            dateCreated = fileTime.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int index = dateCreated.indexOf("T");
        StringBuilder sb = new StringBuilder(dateCreated);
        sb.replace(index, index+1, " ");
        index = dateCreated.lastIndexOf(".");
        for (int i = index; index < sb.toString().length();) { sb.deleteCharAt(i); }
        return sb.toString();
    }
    public static boolean IsDrive(File f){
        /* Перевіряємо отриманий файл на диск ;l
         * systemRoots присвоюємо ...
         * в циклі перевіряємо чи відповідає ...*/
        File[] systemRoots = File.listRoots();
        for (File systemRoot : systemRoots) {
            if (f.equals(systemRoot)) return true;
        }
        return false;
    }
    public static boolean IsFolder(File f){
        Path path = Path.of(f.getPath());
        return Files.isDirectory(path);
    }
}
