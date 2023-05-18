package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.*;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.CharSequence.compare;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

public final class AircraftTableController {

    private final ObjectProperty<ObservableAircraftState> clickedPlane;
    private TableView<ObservableAircraftState> tableView;

    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> clickedPlane){
        this.clickedPlane = clickedPlane;

//      Table view setup
        tableView =new TableView<>();
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
        tableView.getStylesheets().add("table.css");

//      String column
        TableColumn<ObservableAircraftState, String> icaoColumn = new TableColumn<>();
        icaoColumn.setPrefWidth(60);
        icaoColumn.setText("OACI");
        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>();
        callSignColumn.setPrefWidth(70);
        callSignColumn.setText("Indicatif");
        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>();
        registrationColumn.setPrefWidth(90);
        registrationColumn.setText("Immatriculation");
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>();
        modelColumn.setPrefWidth(230);
        modelColumn.setText("Modèle");
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>();
        typeColumn.setPrefWidth(50);
        typeColumn.setText("Type");
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>();
        descriptionColumn.setPrefWidth(70);
        descriptionColumn.setText("Description");

//      Value column
        TableColumn<ObservableAircraftState, String> longitudeColumn = valueColumn("Longitude(°)",4,
                f -> DoubleExpression.doubleExpression(f.positionProperty().map(GeoPos::longitude)),Units.Angle.DEGREE);
        TableColumn<ObservableAircraftState, String> latitudeColumn = valueColumn("Latitude(°)",4,
                f -> DoubleExpression.doubleExpression(f.positionProperty().map(GeoPos::latitude)),Units.Angle.DEGREE);
        TableColumn<ObservableAircraftState, String> altittudeColumn = valueColumn("Altitude(m)",0,
                f -> DoubleExpression.doubleExpression(f.altitudeProperty()), Units.Length.METER);
        TableColumn<ObservableAircraftState, String> velocityColumn = valueColumn("Vitesse(km/h)",0,
                f -> DoubleExpression.doubleExpression(f.velocityProperty()), Units.Speed.KILOMETER_PER_HOUR);


//      Column synthese
        tableView.getColumns().addAll(List.of(icaoColumn, callSignColumn,registrationColumn,modelColumn, typeColumn,
                        descriptionColumn, longitudeColumn,latitudeColumn,altittudeColumn, velocityColumn));

//      Listeners
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if(change.wasAdded()){
                tableView.getItems().add(change.getElementAdded());
                tableView.sort();
            }
            if(change.wasRemoved()){
                tableView.getItems().remove(change.getElementRemoved());
                tableView.sort();
            }
        });

//      Lambda properties update
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

    private TableColumn<ObservableAircraftState, String> valueColumn(String columnTitle, int digitsection,
                                                                     Function<ObservableAircraftState,DoubleExpression> property, double unit) {
        TableColumn<ObservableAircraftState, String> valueColumn =  new TableColumn<>();
        valueColumn.setText(columnTitle);
        valueColumn.setPrefWidth(85);
        valueColumn.getStyleClass().add("numeric");
        NumberFormat numberFormat =  NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(digitsection);
        numberFormat.setMinimumFractionDigits(digitsection);
        valueColumn.setComparator((s1, s2) -> {
            try {
                return (s1 == null || s2 == null) ? String.CASE_INSENSITIVE_ORDER.compare(s1, s2)
                        :  Double.compare(numberFormat.parse(s1).doubleValue(),numberFormat.parse(s2).doubleValue());
            } catch (ParseException e) {
                throw new Error(e);
            }
        });
//        valueColumn.setCellValueFactory(f ->
//               numberFormat.format(property.apply(f.getValue()).doubleValue()))  ;
// #TODO     Post #1882 pour faire afficher et en plus updater je sais pas faire ce soir
        return valueColumn;
    }

}
