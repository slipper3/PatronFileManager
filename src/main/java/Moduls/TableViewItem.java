package Moduls;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;


public class TableViewItem {
    private final String fileSize;
    private final String modDate;
    private final HBox fileName;
    private  final File file;


    public TableViewItem(File f, String size, String date){
        /** Клас для об'єктів таблиці*/
        this.fileName = setup(f);
        this.fileSize = size;
        this.modDate = date;
        this.file = f;
    }
    private HBox setup(File f){
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(f);
        ImageView fileIcon = new ImageView(IconToImage(icon));
        Label fileLabel = new Label(f.getName());
        HBox hBox = new HBox(fileIcon, fileLabel);
        return hBox;
    }
    private Image IconToImage(Icon icon){
        icon = new SafeIcon(icon);
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        icon.paintIcon(new JPanel(), image.getGraphics(), 0, 0);
        Image img = SwingFXUtils.toFXImage(image, null);
        return img;
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
