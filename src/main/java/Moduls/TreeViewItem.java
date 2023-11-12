package Moduls;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class TreeViewItem {
    private final HBox hbox;
    private final File file;

    public TreeViewItem(File f){
        this.hbox = setup(f);
        this.file = f;
    }

    public HBox setup(File f){
        String driveName = f.getName();
        if(myUtils.IsDrive(f)){
            FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            driveName = fileSystemView.getSystemDisplayName(f);
        }
        Label fileLabel = new Label(driveName);
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(f);
        if (icon!=null){
            ImageView fileIcon = new ImageView(myUtils.IconToImage(icon));
            HBox hBox = new HBox(fileIcon, fileLabel);
            return hBox;
        }
        HBox hBox = new HBox(fileLabel);
        return hBox;
    }
    public HBox getHbox(){return hbox;}

    public File getFile() {
        return file;
    }
}
