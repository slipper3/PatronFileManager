package Moduls;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class myUtils {
    private static List<String> excludedFiles = Arrays.asList(
            "Config.msi",
            "Config.Msi",
            "$RECYCLE.BIN",
            "$Recycle.Bin",
            "Documents and Settings",
            "System Volume Information",
            "Thumbs.db",
            "desktop.ini",
            "pagefile.sys",
            "pagefile.sys",
            "swapfile.sys",
            "Memory.dmp",
            "IconCache.db",
            "boot.ini",
            "BCDBOOT.log",
            "$Windows.~BT",
            "$Windows.~WS",
            "Recovery",
            "Boot");
    public static final List<File> searchResult = new ArrayList<>();
    public static long sizeResult = 0;
    private static Thread thread = new Thread();

    public myUtils(){}
    public static void copyFilesToClipboard(List<File> files) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = new TransferableFiles(files);
        clipboard.setContents(transferable, null);
    }
    public static List<File> pasteFilesFromClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                return new ArrayList<>(fileList);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }

        return new ArrayList<>();
    }
    public static Image IconToImage(Icon icon){
        icon = new SafeIcon(icon);
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
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
        //long result = 0;
        long sizeInByte=0;
        Path path;

        /*Розрахунок ваги для диску*/
        if(IsDrive(f)){
            return f.getTotalSpace()/(1024*1024*1024) + "GB";
        }
        if(IsFolder(f)){
            /** Цей кусочек коду підраховує вагу папки та відображає в списку файлів
             * але цей підрахунок займає забагато часу,
             * також є деякі файли вагу яких підрахувати не можливо, тому і вагу папки вирахувати не можливо
            File[] files = f.listFiles();
            try{
                for(File file : Objects.requireNonNull(files)){ result += calculateSizeInt(file); }
                if(result<(1024)){return result + " B";}
                else if(result<(1024*1024)){return result/1024 + " KB";}
                else if(result<(1024*1024*1024)){return result/(1024*1024) + " MB";}
                else {return result/(1024*1024*1024) + " GB";}
            }
            catch (Exception e){
                return "Немжлив підрахувати";
            }*/
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
            s = sizeInByte + " B";  //Якощ розмір менше 1кб
            return s;
        }
        else if(sizeInByte<(1024*1024)){
            long sizeInKb = sizeInByte/1024; s = sizeInKb + " KB"; return s; //Якщо розмір більше 1кб але менше 1мб
        }
        else if(sizeInByte<(1024*1024*1024)){
            long sizeInMb = sizeInByte/(1024*1024); s = sizeInMb + " MB"; return s; //Якщо розмір більше 1мб але менше 1гб
        }
        else{
            long sizeInGb = sizeInByte/(1024*1024*1024); s = sizeInGb + " GB"; return s; //Якщо розмір більше 1гб
        }
    }

    public static long calculateSizeInt(File f){
        /*
         * Функція розрахунку ваги файлу
         */

        long sizeInByte=0;
        Path path;

        /*Розрахунок ваги для диску*/
        if(IsDrive(f)){
            return f.getTotalSpace()/(1024*1024*1024);
        }
        if(!IsFolder(f)){
//            File[] files = f.listFiles();
//            if (files !=null)
//                for(File file : files){ sizeResult += calculateSizeInt(file); }
//            return sizeResult;
            path = Paths.get(f.toURI()); // Отримуємо шлях до файлу
            //sizeInByte = f.getTotalSpace(); // terrible idea cz sob subfolder e 199GB, 99GB
            try {
                sizeInByte = Files.size(path);//at least works ^_^ отримуємо розмір файлу в байтах
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sizeInByte;
        }

        return sizeInByte;
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
    public static long getFileCreationEpoch(File file) {
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(),
                    BasicFileAttributes.class);
            return attr.creationTime()
                    .toInstant()
                    .toEpochMilli();
        } catch (IOException e) {
            throw new RuntimeException(file.getAbsolutePath(), e);
        }
    }
    public static String getModified(File f){
        String dateModified;
        try {
            BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
            FileTime fileTime = attr.lastModifiedTime();
            dateModified = fileTime.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int index = dateModified.indexOf("T");
        StringBuilder sb = new StringBuilder(dateModified);
        sb.replace(index, index+1, " ");
        index = dateModified.lastIndexOf(":");
        for (int i = index+3; i < sb.toString().length();) { sb.deleteCharAt(i); }
        return sb.toString();
    }
    public static  String getExtension(File f){
        System.out.println(f.getName());
        StringBuilder extension = new StringBuilder(f.getName());
        int dotIndex = extension.reverse().toString().indexOf(".");
        for (int index = dotIndex; index < extension.toString().length();) { extension.deleteCharAt(index); }
        return extension.reverse().toString();
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
    public static void OpenFile(File f){
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
    public static List<File> findFilesByName(File startDir, String fileName) {
        File[] files = startDir.listFiles();
        if (files != null)
            for (File file : files){
                if (file.isDirectory()) {
                    if (!isExcluded(file) && file.getName().contains(fileName))
                        searchResult.add(file);
                    System.out.println(file);
                    findFilesByName(file, fileName);
                }
                else if (!isExcluded(file) && file.getName().contains(fileName))
                    searchResult.add(file); System.out.println(file);
            }
        return searchResult;
    }
    public static boolean isHidden(Path path){
        return !path.toFile().isHidden();
    }
    public static boolean isExcluded(File file){
        return excludedFiles.contains(file.getName());
    }
    public static String getLowName(File file){
        return file.getName().toLowerCase();
    }
}