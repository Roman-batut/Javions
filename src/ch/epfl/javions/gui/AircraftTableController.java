package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.function.Consumer;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

public final class AircraftTableController {

    private final ObjectProperty<ObservableAircraftState> clickedPlane;
    private TableView<ObservableAircraftState> tableView;

    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> clickedPlane){
        this.clickedPlane = clickedPlane;
        tableView =new TableView<>();
//        tableView.setItems(aircraftStates);
        tableView.getStylesheets().add("table.css");
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
        TableColumn<ObservableAircraftState, String> icaoColumn = new TableColumn<>();
        icaoColumn.setPrefWidth(60);
        TableColumn<ObservableAircraftState, Double> valueColumn = new TableColumn<>();
        valueColumn.setPrefWidth(85);
        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>();
        callSignColumn.setPrefWidth(70);
        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>();
        registrationColumn.setPrefWidth(90);
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>();
        modelColumn.setPrefWidth(230);
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>();
        typeColumn.setPrefWidth(50);
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>();
        descriptionColumn.setPrefWidth(70);
        callSignColumn.setCellValueFactory(f ->
                f.getValue().callSignProperty().map(CallSign::string));

        tableView.getColumns().add(callSignColumn);
        tableView.getColumns().add(icaoColumn);
        tableView.getColumns().add(valueColumn);
        tableView.getColumns().add(registrationColumn);
        tableView.getColumns().add(modelColumn);
        tableView.getColumns().add(typeColumn);
        tableView.getColumns().add(descriptionColumn);

    }

     public TableView<ObservableAircraftState> pane(){
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
