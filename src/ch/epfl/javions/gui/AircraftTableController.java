package ch.epfl.javions.gui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.layout.Pane;

import javax.swing.text.TabableView;
import javax.swing.text.TableView;
import java.util.function.Consumer;

public final class AircraftTableController {

    private final ObjectProperty<ObservableAircraftState> clickedPlane;
    private TableView tableView;

    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> clickedPlane){
        this.clickedPlane = clickedPlane;
    }

     public TableView pane(){
        return tableView;
    }
     public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer){
        clickedPlane.addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                consumer.accept(newValue);
            }
        });
    }

}
