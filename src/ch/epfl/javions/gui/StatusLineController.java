package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {

    public static final String STATUS_LINE_STYLE_SHEET = "status.css";
    public static final String DATA_VALUE_FORMAT = "%03d";
    public static final String AIRCRAFT_VISIBLE_STRING = "Aéronefs visibles : ";
    public static final String RECEIVE_MESSAGES_STRING = "Messages reçues : ";
    private final BorderPane pane;
    private final IntegerProperty aircraftCount;
    private final LongProperty messageCount;

    //* Constructor

    public StatusLineController(){
        pane = new BorderPane();
        pane.getStylesheets().add(STATUS_LINE_STYLE_SHEET);

        Text aircraftCountText = new Text();
        Text messageCountText = new Text();

        aircraftCount = new SimpleIntegerProperty(0);
        messageCount = new SimpleLongProperty(0);

        StringBinding aircraftCountValue = Bindings.createStringBinding(() ->
            String.format(DATA_VALUE_FORMAT,aircraftCountProperty().get()),aircraftCountProperty());
        aircraftCountText.textProperty().bind(Bindings.createStringBinding(()-> AIRCRAFT_VISIBLE_STRING + aircraftCountValue.getValue(), aircraftCountValue));

        StringBinding messageCountValue = Bindings.createStringBinding(() ->
                String.format(DATA_VALUE_FORMAT,messageCountProperty().get()),messageCountProperty());
        messageCountText.textProperty().bind(Bindings.createStringBinding(()-> RECEIVE_MESSAGES_STRING + messageCountValue.getValue(), messageCountValue));

        pane.setLeft(aircraftCountText);
        pane.setRight(messageCountText);
    }

    //* Getters

    public BorderPane pane(){
        return pane;
    }

    public IntegerProperty aircraftCountProperty(){
        return aircraftCount;
    }

    public LongProperty messageCountProperty(){
        return messageCount;
    }

}
