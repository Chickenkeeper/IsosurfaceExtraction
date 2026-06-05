package org.openjfx.isosurface.view;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionModel;
import org.openjfx.isosurface.model.util.Named;

/**
 * A combo-box that can be scrolled by a mouse.
 * It also supports automatic labeling of values via the {@code Named} interface.
 *
 * @param <T> the type of value to store in this combo-box
 */
public class ScrollableComboBox<T extends Named> extends ComboBox<T> {
    /**
     * Constructs a new empty {@code ScrollableComboBox} instance.
     */
    public ScrollableComboBox() {
        super();

        setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && !empty) {
                    setText(item.getDisplayString());
                }
            }
        });
        setButtonCell(getCellFactory().call(null));
        setOnScroll(e -> {
            final SelectionModel<T> shapeSelectionModel = getSelectionModel();
            final int numItems = getItems().size();
            final double deltaScroll = e.isShiftDown() ? e.getDeltaX() : e.getDeltaY();
            final int deltaIndex = deltaScroll < 0 ? 1 : -1;
            final int currSelectedIndex = shapeSelectionModel.getSelectedIndex();
            final int newSelectedIndex = (currSelectedIndex + deltaIndex + numItems) % numItems;

            shapeSelectionModel.select(newSelectedIndex);
        });
    }
}
