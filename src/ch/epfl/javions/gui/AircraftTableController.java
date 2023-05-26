package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.*;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.CharSequence.compare;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS;

/**
 * Class representing a controller of the aircraft table view
 * @author Roman Batut (356158)
 * @author Guillaume Chevallier (360709)
 */
public final class AircraftTableController {

    private final ObjectProperty<ObservableAircraftState> clickedPlane;
    private final TableView<ObservableAircraftState> tableView;
    
    private Consumer<ObservableAircraftState> consumer;
    
    //* Constants

    private static final String TABLE_STYLE_SHEET = "table.css";
    private static final String ICAO_COLUMN_TITLE = "OACI";
    private static final int ICAO_WIDTH = 60;
    private static final String CALLSIGN_COLUMN_TITLE = "Indicatif";
    private static final int CALLSIGN_WIDTH = 70;
    private static final String REGISTRATION_COLUMN_TITLE = "Immatriculation";
    private static final int REGISTRATION_WIDTH = 90;
    private static final String MODEL_COLUMN_TITLE = "Modèle";
    private static final int MODEL_WIDTH = 230;
    private static final String TYPE_COLUMN_TITLE = "Type";
    private static final int TYPE_WIDTH = 50;
    private static final String DESCRIPTION_COLUMN_TITLE = "Description";
    private static final int DESCRIPTION_WIDTH = 70;
    private static final String LONGITUDE_COLUMN_TITLE = "Longitude(°)";
    private static final int LAT_AND_LONG_DIGITSECTION = 4;
    private static final String LATITUDE_COLUMN_TITLE = "Latitude(°)";
    private static final int ALT_AND_VEL_DIGITSECTION = 0;
    private static final String ALTITUDE_COLUMN_TITLE = "Altitude(m)";
    private static final String VELOCITY_COLUMN_TITLE = "Vitesse(km/h)";
    private static final int DOUBLE_CLICK_NBR = 2;
    private static final String VALUE_COLUMN_STYLE_SHEET = "numeric";
    private static final int MAX_UNDEFINED_VELOCITY = 0;
    private static final int VALUE_WIDTH = 85;

    //* Constructor

    /**
     * Constructor of the aircraft table controller
     * @param aircraftStates the observable set of aircraft states
     * @param clickedPlane the clicked plane
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> clickedPlane){
        this.clickedPlane = clickedPlane;

        //Table view setup
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        tableView.setTableMenuButtonVisible(true);
        tableView.getStylesheets().add(TABLE_STYLE_SHEET);

        //String column
        TableColumn<ObservableAircraftState, String> icaoColumn = textColumn(ICAO_COLUMN_TITLE, ICAO_WIDTH,
                f -> new ReadOnlyObjectWrapper<>(f.getIcaoAddress()).map(IcaoAddress::string));
        
        TableColumn<ObservableAircraftState, String> callSignColumn = textColumn(CALLSIGN_COLUMN_TITLE, CALLSIGN_WIDTH,
                f -> f.callSignProperty().map(CallSign::string));

        TableColumn<ObservableAircraftState, String> registrationColumn =textColumn(REGISTRATION_COLUMN_TITLE, REGISTRATION_WIDTH,
                f -> new ReadOnlyObjectWrapper<>(f.getAircraftData()).map(d -> d.registration().string()));

        TableColumn<ObservableAircraftState, String> modelColumn = textColumn(MODEL_COLUMN_TITLE, MODEL_WIDTH,
                f -> new ReadOnlyObjectWrapper<>(f.getAircraftData()).map(AircraftData::model));

        TableColumn<ObservableAircraftState, String> typeColumn = textColumn(TYPE_COLUMN_TITLE, TYPE_WIDTH,
                f -> new ReadOnlyObjectWrapper<>(f.getAircraftData()).map(d -> d.typeDesignator().string()));

        TableColumn<ObservableAircraftState, String> descriptionColumn = textColumn(DESCRIPTION_COLUMN_TITLE, DESCRIPTION_WIDTH,
                f -> new ReadOnlyObjectWrapper<>(f.getAircraftData()).map(d -> d.description().string()));


        //Value column
        TableColumn<ObservableAircraftState, String> longitudeColumn = valueColumn(LONGITUDE_COLUMN_TITLE, LAT_AND_LONG_DIGITSECTION,
                f -> f.positionProperty().map(GeoPos::longitude),Units.Angle.DEGREE);
        TableColumn<ObservableAircraftState, String> latitudeColumn = valueColumn(LATITUDE_COLUMN_TITLE,LAT_AND_LONG_DIGITSECTION,
                f -> f.positionProperty().map(GeoPos::latitude),Units.Angle.DEGREE);
        TableColumn<ObservableAircraftState, String> altittudeColumn = valueColumn(ALTITUDE_COLUMN_TITLE, ALT_AND_VEL_DIGITSECTION,
                f -> f.altitudeProperty().map(Number::doubleValue), Units.Length.METER);
        TableColumn<ObservableAircraftState, String> velocityColumn = valueColumn(VELOCITY_COLUMN_TITLE,ALT_AND_VEL_DIGITSECTION,
                f -> f.velocityProperty().map(Number::doubleValue), Units.Speed.KILOMETER_PER_HOUR);


        //Column synthese
        tableView.getColumns().addAll(List.of(icaoColumn, callSignColumn,registrationColumn,modelColumn, typeColumn,
                        descriptionColumn, longitudeColumn,latitudeColumn,altittudeColumn, velocityColumn));

        //Listeners
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if(change.wasAdded()){
                tableView.getItems().add(change.getElementAdded());
                tableView.sort();
            }
            else if(change.wasRemoved()){
                tableView.getItems().remove(change.getElementRemoved());
            }
        });

        clickedPlane.addListener((ChangeListener<? super ObservableAircraftState>) (observableValue, oldValue, newValue)-> {
            tableView.getSelectionModel().select(newValue);
            if (!newValue.equals(oldValue)){
               tableView.scrollTo(newValue);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            clickedPlane.set(newValue);
        });

        tableView.setOnMouseClicked(event -> {
            if ((event.getButton() == MouseButton.PRIMARY)
                    && (event.getClickCount() == DOUBLE_CLICK_NBR)
                    && (clickedPlane.get() != null)
                    && (consumer != null)) {
                consumer.accept(clickedPlane.get());
            }
        });

    }

    //* Getters

    /**
     * Getter of the pane that is the table view
     * @return the pane
     */
    public TableView<ObservableAircraftState> pane(){
        return tableView;
    }

    
    //* Methods
    
    /**
     * Set the consumer of the double click
     * @param consumer the consumer
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer){
        this.consumer = consumer;
    }

    //* Private methods

    /**
     * Creates the value column
     * @param columnTitle the title of the column
     * @param digitSection the number of digits after the decimal point
     * @param property the property of the column
     * @param unit the unit of the column
     * @return the value column
     */
    private TableColumn<ObservableAircraftState, String> valueColumn(String columnTitle, int digitSection,
                                                                     Function<ObservableAircraftState,ObservableValue<Number>> property, double unit) {

        TableColumn<ObservableAircraftState, String> valueColumn =  new TableColumn<>();
        valueColumn.setText(columnTitle);
        valueColumn.setPrefWidth(VALUE_WIDTH);
        valueColumn.getStyleClass().add(VALUE_COLUMN_STYLE_SHEET);

        NumberFormat numberFormat =  NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(digitSection);
        numberFormat.setMinimumFractionDigits(digitSection);

        //Comparator
        valueColumn.setComparator((s1, s2) -> {
            try {
               s1 = (s1 == null) ? "" :s1;
               s2 = (s2 == null) ? "" :s2;
               return (s1.equals("") || s2.equals("")) ?
                       String.CASE_INSENSITIVE_ORDER.compare(s1, s2)
                       :  Double.compare(numberFormat.parse(s1).doubleValue(),numberFormat.parse(s2).doubleValue());
            } catch (ParseException e) {
                throw new Error(e);
            }
        });

        //Cell factory
        valueColumn.setCellValueFactory(f->
               property.apply(f.getValue())
                       .map(e -> Units.convertTo(e.doubleValue(), unit))
                       .map(e -> e < MAX_UNDEFINED_VELOCITY ? null : e)
                       .map(numberFormat::format)
        );

        return valueColumn;
    }

    /**
     * Creates the text column
     * @param columnTitle the title of the column
     * @param width the width of the column
     * @param function the function of the column
     * @return the text column
     */
    private TableColumn<ObservableAircraftState, String> textColumn(String columnTitle, int width,
                            Function<ObservableAircraftState, ObservableValue<String>> function){

        TableColumn<ObservableAircraftState, String> textcolumn = new TableColumn<>();

        textcolumn.setText(columnTitle);
        textcolumn.setPrefWidth(width);
        textcolumn.setCellValueFactory(f -> function.apply(f.getValue()));

        return textcolumn;
    }

}