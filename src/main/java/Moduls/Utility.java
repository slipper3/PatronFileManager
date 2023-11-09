package Moduls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;

public class Utility {
    private double totalGB;
    private double usedGB;
    private double freeGB;


    public Utility() {}

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
                System.out.println(Math.round(usedPer) + " %");

                double remPer = (remSpace / totalSpace) * 100;
                System.out.println(Math.round(remPer) + " %");

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
}
