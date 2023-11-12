package Moduls;

public class TreeCell extends javafx.scene.control.TreeCell<TreeViewItem> {
    // Клас TreeCell для коректного відображення HBox у TreeView
    @Override
    protected void updateItem(TreeViewItem item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setGraphic(item.getHbox());
        }
    }
}
