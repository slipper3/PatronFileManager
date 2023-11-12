package Moduls;

import Patron.Controller;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class TableViewItem {
    private final String fileSize;
    private final String modDate;
    private final HBox fileName;
    private final File file;


    public TableViewItem(File f, String size, String date){
        /**
         * Клас для об'єктів таблиці
         */
        this.fileName = setup(f);
        this.fileSize = size;
        this.modDate = date;
        this.file = f;
    }
    private HBox setup(File f){
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(f);
        ImageView fileIcon = new ImageView(myUtils.IconToImage(icon));
        Label fileLabel = new Label(f.getName());
        HBox hBox = new HBox(fileIcon, fileLabel);
        return hBox;
    }

    public String getFileSize() {
        return fileSize;
    }
    public String getModDate() {
        return modDate;
    }
    public HBox getFileName() {
        return fileName;
    }
    public File getFile() {
        return file;
    }
}
