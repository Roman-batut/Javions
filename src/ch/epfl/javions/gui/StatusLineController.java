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

    private BorderPane pane;
    private Text aircraftCountText;
    private Text messageCountText;
    private IntegerProperty aircraftCount;
    private LongProperty messageCount;

    //* Constructor

    public StatusLineController(){
        pane = new BorderPane();
        pane.getStylesheets().add("status.css");

        aircraftCountText = new Text();
        messageCountText = new Text();

        aircraftCount = new SimpleIntegerProperty(0);
        messageCount = new SimpleLongProperty(0);

        StringBinding aircraftCountValue = Bindings.createStringBinding(() ->
            String.format("%03d",aircraftCountProperty().get()),aircraftCountProperty());
        aircraftCountText.textProperty().bind(Bindings.createStringBinding(()-> "Aéronefs visibles : " + aircraftCountValue.getValue(), aircraftCountValue));

        StringBinding messageCountValue = Bindings.createStringBinding(() ->
                String.format("%03d",messageCountProperty().get()),messageCountProperty());
        messageCountText.textProperty().bind(Bindings.createStringBinding(()-> "Messages reçues : " + messageCountValue.getValue(), messageCountValue));

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
