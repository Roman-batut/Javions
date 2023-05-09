package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

public final class AircraftController {

    private MapParameters mapParameters;
    private ObservableSet<ObservableAircraftState> aircraftStates;
    private  ObjectProperty<ObservableAircraftState> clickedPlane;
    private Pane pane;

    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> clickedPlane){

        this.mapParameters = mapParameters;
        this.aircraftStates = aircraftStates;
        this.clickedPlane = clickedPlane;
        pane = new Pane();
        pane.setPickOnBounds(false);

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    aircraft(change.getElementAdded());
                    if(change.wasRemoved()){
                        pane.getChildren().remove(change.getElementRemoved());
                    }
                });

    }

    public Pane pane(){
        pane.getStylesheets().add("aircraft.css");
        return pane;
    }

    //*Private Methods

    //Label
    private Rectangle background(Text text){
        Rectangle rectangle = new Rectangle();

        rectangle.widthProperty().bind(text.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rectangle.heightProperty().bind(text.layoutBoundsProperty().map(b -> b.getHeight() + 4));

        return rectangle;
    }

    private Text text(ObservableAircraftState state){
        Text text = new Text();
        StringProperty name = new SimpleStringProperty();

        if(state.getRegistration() != null){
            name.set(state.getRegistration().string());
        }else{
            name.bind(state.callSignProperty().asString().when(Bindings.createBooleanBinding(() ->
                    state.callSignProperty() != null, state.callSignProperty())).orElse(state.getIcaoAddress().string()));
        }

        text.textProperty().bind(Bindings.createStringBinding(() ->
                        name.get() + "\n"+ (int) state.velocityProperty().get()+"km/h" + "\u2002" + (int)state.altitudeProperty().get() + "m",
                name, state.velocityProperty(),state.altitudeProperty()));

        return text;
    }

    private Group label(ObservableAircraftState state){
        Text text = text(state);
        Rectangle rectangle = background(text);

        Group label = new Group(rectangle, text);

        label.getStyleClass().add("label");

        return label;
    }

    //Icon

    private SVGPath icon(ObservableAircraftState state, AircraftIcon aircraftIcon){
        SVGPath icon = new SVGPath();

        icon.setContent(aircraftIcon.svgPath());

        icon.rotateProperty().bind(Bindings.createDoubleBinding(() ->
                aircraftIcon.canRotate() ? Units.convertTo(state.getTrackOrHeanding(), Units.Angle.DEGREE) : 0, state.trackOrHeading()));

        icon.fillProperty().bind(Bindings.createObjectBinding(() ->
                ColorRamp.PLASMA.at(Math.pow((state.getAltitude()/12000.d),1.d/3)), state.altitudeProperty()));

        icon.setOnMousePressed(e ->
                clickedPlane.set(state));

        icon.getStyleClass().add("aircraft");

        return icon;
    }

    //Icon + Label
    private Group iconLabel(ObservableAircraftState state){
        SVGPath icon = icon(state, iconCreation(state));

        Group label = label(state);
        label.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                state.equals(clickedPlane.get()) || mapParameters.getZoom()>=11, clickedPlane, mapParameters.zoom()));

        Group iconLabel = new Group(icon, label);
        iconLabel.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                WebMercator.x(mapParameters.getZoom(), state.getPosition().longitude()) - mapParameters.getMinX(), state.positionProperty(), mapParameters.minX()));

        iconLabel.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                WebMercator.y(mapParameters.getZoom(), state.getPosition().latitude()) - mapParameters.getMinY(), state.positionProperty(), mapParameters.minY()));

        return iconLabel;
    }

    //Trajectory

    private Line line(double x, double y){
        Line line = new Line();

        line.setStartX(x);
        line.setStartY(y);

        return line;
    }

    private Group trajectory(ObservableAircraftState state){
        Line line = line(WebMercator.x(mapParameters.getZoom(), state.getPosition().longitude()), WebMercator.y(mapParameters.getZoom(), state.getPosition().latitude()));

        line.endXProperty().bind(Bindings.createDoubleBinding(() ->
                WebMercator.x(mapParameters.getZoom(), state.getPosition().longitude()), state.positionProperty()));

        line.endYProperty().bind(Bindings.createDoubleBinding(() ->
                WebMercator.y(mapParameters.getZoom(), state.getPosition().latitude()), state.positionProperty()));

        Group trajectory = new Group(line);

        trajectory.getStyleClass().add("trajectory");

        return trajectory;
    }

    //Global Group

    private void aircraft(ObservableAircraftState state){
        Group iconLabel = iconLabel(state);
        Group trajectory = trajectory(state);
        Group aircraft = new Group(iconLabel, trajectory);

        aircraft.setId(state.getIcaoAddress().string());
        pane.getChildren().add(aircraft);
    }

    private AircraftIcon iconCreation(ObservableAircraftState state){
        if(state == null){
            return AircraftIcon.iconFor(new AircraftTypeDesignator(""), new AircraftDescription("") ,0, WakeTurbulenceCategory.UNKNOWN);
        }
        AircraftTypeDesignator typeDesignator = state.getTypeDesignator() == null ? new AircraftTypeDesignator("") : state.getTypeDesignator();
        AircraftDescription description = state.getDescription() == null ? new AircraftDescription("") : state.getDescription();

        return AircraftIcon.iconFor(typeDesignator, description, state.getCategory(), state.getWakeTurbulenceCategory());
    }

}


//#TODO rename en anglais bien correcte
// #TODO faire la gestion d'error quand le state est null
