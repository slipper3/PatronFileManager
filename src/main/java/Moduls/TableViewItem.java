package Moduls;

public class TableViewItem {
    /** Клас для об'єктів таблиці*/
    private String name;
    private String size;
    private String date;

    public TableViewItem(String name, String size, String date){
        this.name = name;
        this.size = size;
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }
}
