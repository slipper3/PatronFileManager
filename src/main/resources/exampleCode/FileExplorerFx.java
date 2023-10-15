package exampleCode;


import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import static javafx.embed.swing.SwingFXUtils.toFXImage;


/**
 * Created by Fahim on 4/27/2017.
 *
 * Commented and modded 10/8/2023 by slipper3
 */
public abstract class FileExplorerFx implements FileExplorer{
    //
    static File CurrDirFile;
    static String CurrDirStr;
    static Label lbl;
    static String CurrDirName;
    static TilePane tilePane;
    SimpleDateFormat sdf;

    TableView<FileInfo> tableview;
    TableColumn<FileInfo, ImageView> image;
    TableColumn<FileInfo, String> date;
    TableColumn<FileInfo, String> name;
    TableColumn<FileInfo, String> size;

    FileExplorerFx(){}

    public Image getIconImageFX(File f){
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
        Image imgfx = toFXImage(bimg,null);
        return imgfx;
    }


    public void setLabelTxt(){

        lbl.setText(CurrDirStr);
    }

    public String calculateSize(File f){
        /**
         * Функція розрахунку ваги файлу
         */

        String s;long sizeInByte=0; Path path;

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
    public int FilesHiddensCount(File dir){
        int count = 0;
        File[] fl = dir.listFiles();
        //System.out.println(fl.length);
        for(int i=0; i<fl.length; i++){
            try{if(fl[i].isHidden() || fl[i].isFile()) count++; }
            catch(Exception x){
                System.out.println("Exception at prototype1, fileexplorer CountDir: "+x.getMessage());
            }

        }
        return count;
    }
    /*******************************************/
    public int NumOfDirectChilds(File f){
        return f.listFiles().length;
    }

}