package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
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
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);

        TableColumn<ObservableAircraftState, String> icaoColumn = new TableColumn<>();
        icaoColumn.setPrefWidth(60);
        icaoColumn.setText("OACI");
        TableColumn<ObservableAircraftState, Double> valueColumn = new TableColumn<>();
        valueColumn.setPrefWidth(85);
        valueColumn.setText("value");
        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>();
        callSignColumn.setPrefWidth(70);
        callSignColumn.setText("Callsign");
        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>();
        registrationColumn.setPrefWidth(90);
        registrationColumn.setText("registration");
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>();
        modelColumn.setPrefWidth(230);
        modelColumn.setText("model");
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>();
        typeColumn.setPrefWidth(50);
        typeColumn.setText("type");
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>();
        descriptionColumn.setPrefWidth(70);
        descriptionColumn.setText("description");

        tableView.getColumns().add(icaoColumn);
        tableView.getColumns().add(callSignColumn);
        tableView.getColumns().add(registrationColumn);
        tableView.getColumns().add(modelColumn);
        tableView.getColumns().add(typeColumn);
        tableView.getColumns().add(descriptionColumn);
        tableView.getColumns().add(valueColumn);

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if(change.wasAdded()){
                tableView.getItems().add(change.getElementAdded());
                tableView.getSortOrder().add(typeColumn);
            }
            if(change.wasRemoved()){
                tableView.getItems().remove(change.getElementRemoved());
                tableView.getSortOrder().add(typeColumn);
            }
        });
        callSignColumn.setCellValueFactory(f ->
                f.getValue().callSignProperty().map(CallSign::string));
        typeColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData()).map(d -> d.typeDesignator().string()));
        modelColumn.setCellValueFactory(f ->
            new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData()).map(AircraftData::model));
        icaoColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().getIcaoAddress()).map(IcaoAddress::string));
        registrationColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData()).map(d -> d.registration().string()));
        descriptionColumn.setCellValueFactory(f ->
                new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData()).map(d -> d.description().string()));
        tableView.getStylesheets().add("table.css");
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
