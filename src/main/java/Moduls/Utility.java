package Moduls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Objects;

public class Utility {

    private double totalGB;
    private double usedGB;
    private double freeGB;

    public ObservableList<TableViewItem> fileList;


    public Utility() {
        fileList = FXCollections.observableArrayList();
    }

    public ArrayList<Drive> getAllDrives() {
        ArrayList<Drive> drives = new ArrayList<Drive>();
        FileSystemView fsv = FileSystemView.getFileSystemView();

        File[] drivesList = File.listRoots();
        if (drivesList != null) {
            for (File aDrive : drivesList) {
                Drive drive = new Drive();
                drive.setFile(aDrive);
                drive.setDriveName(aDrive.getName());

                double totalSpace = aDrive.getTotalSpace();
                double usedSpace = aDrive.getTotalSpace() - aDrive.getFreeSpace();
                double remSpace = aDrive.getFreeSpace();

                totalGB = totalSpace / 1073741824.0;

                drive.setTotalSpace(String.format("%.2f", totalGB) + " GB");

                usedGB = usedSpace / 1073741824.0;

                drive.setUsedSpace(String.format("%.2f", usedGB) + " GB");

                freeGB = remSpace / 1073741824.0;

                drive.setFreeSpace(String.format("%.2f", freeGB) + " GB");



                double usedPer = (usedSpace / totalSpace) * 100;

                double remPer = (remSpace / totalSpace) * 100;

                drive.setDblFreeSpace(remSpace);
                drive.setDblTotalSpace(totalSpace);
                drive.setDblUsedSpace(usedSpace);
                drive.setPer(Math.round(usedPer));
                drive.setRemPer(Math.round(remPer));
                drives.add(drive);
            }
        }
        return drives;
    }

    public ObservableList<TableViewItem> getFiles(File[] files, int level, String fileType){

        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    String extension = getExtensionByStringHandling(f.getName());
                    FileTypes filetypes = new FileTypes();
                    for (String name : filetypes.hashtable.get(fileType)) {
                        if (Objects.equals(extension, name)) {
                            TableViewItem item = new TableViewItem(f, calculateSize(f), getDate(f));
                            System.out.println("Working!");
                            fileList.add(item);
                            break;
                        }
                    }
                } else if (f.isDirectory()) {
                    getFiles(f.listFiles(), level + 1, fileType);
                }
            }
        }

        return fileList;
    }

    private String calculateSize(File f){
        /*
         * Функція розрахунку ваги файлу
         */

        String s;
        long sizeInByte=0;
        Path path;

        /*Розрахунок ваги для диску*/

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

    public String getExtensionByStringHandling(String filename) {
        String extension = "";
        int index = filename.lastIndexOf('.');
        if (index > 0) {
            extension = filename.substring(index + 1);
        }
        return extension;
    }

    public void clearFileList(){
        fileList.clear();
    }
}
