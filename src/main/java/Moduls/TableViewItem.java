package Moduls;

public class TableViewItem {
    /** Клас для об'єктів таблиці*/
    private final String fileName;
    private final String fileSize;
    private final String modDate;

    public TableViewItem(String name, String size, String date){
        this.fileName = name;
        this.fileSize = size;
        this.modDate = date;
    }
    public String getFileSize() {
        return fileSize;
    }
    public String getModDate() {
        return modDate;
    }
    public String getFileName() {
        return fileName;
    }
}
