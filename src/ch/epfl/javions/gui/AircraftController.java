package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
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

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>) change -> {
                if(change.wasAdded()){
                    aircraft(change.getElementAdded());
                }

                if(change.wasRemoved()){
                    ObservableAircraftState statermv = change.getElementRemoved();
                    pane.getChildren().removeIf((s) -> s.getId().equals(statermv.getIcaoAddress().string()));
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


        StringBinding velalt = Bindings.createStringBinding(() ->{
            double vel = state.velocityProperty().getValue();
            double alt = state.altitudeProperty().getValue();
            return
                String.format("\n%s km/h" +"  " +"%s m" ,
                        Double.isNaN(vel) ?
                            "?" :
                                String.format("%.2f",(Units.convertTo(vel, Units.Speed.KILOMETER_PER_HOUR))),
                        Double.isNaN(alt) ?
                                "?" : String.format("%.2f",alt)
        );},state.velocityProperty(), state.altitudeProperty());

        Text text = new Text();
        if(state.getAircraftData() == null || state.getRegistration() == null){
            ObservableValue<String> id = (Bindings.createStringBinding(()-> state.getCallSign().string(), state.callSignProperty()))
                    .when(Bindings.createBooleanBinding(()-> state.getCallSign().string().isEmpty(), state.callSignProperty())).orElse(state.getIcaoAddress().string());
            text.textProperty().bind(Bindings.createStringBinding(() -> (id.getValue() + velalt.getValue()), id, velalt));
        }else {
            text.textProperty().bind(Bindings.createStringBinding(()-> state.getRegistration().string() + velalt.getValue(), velalt));
        }

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
                aircraftIcon.canRotate() ? Units.convertTo(state.trackOrHeading().get(), Units.Angle.DEGREE) : 0, state.trackOrHeading()));

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

    private Line line(double startX, double startY, double endX, double endY){
        Line line = new Line();

        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);

        return line;
    }

    private Group trajectory(ObservableAircraftState state){

        //line.endXProperty().bind(Bindings.createDoubleBinding(() ->
        //       WebMercator.x(mapParameters.getZoom(), state.getPosition().longitude()), state.positionProperty()));

        //line.endYProperty().bind(Bindings.createDoubleBinding(() ->
        //        WebMercator.y(mapParameters.getZoom(), state.getPosition().latitude()), state.positionProperty()));
        Group trajectory = new Group();

        if(state == null || state.trajectoryProperty().size() < 2){
            System.out.println("null");
            return trajectory;
        }

        state.trajectoryProperty().addListener((ListChangeListener<ObservableAircraftState.AirbornPos>) change -> {
            trajectory.getChildren().clear();

            for(int i=0 ; i<state.trajectoryProperty().size()-1 ; i++){
                System.out.println(WebMercator.x(mapParameters.getZoom(), state.trajectoryProperty().get(i).position().longitude()) - mapParameters.getMinX());
                System.out.println(WebMercator.y(mapParameters.getZoom(), state.trajectoryProperty().get(i).position().latitude()) - mapParameters.getMinY());
                System.out.println(WebMercator.x(mapParameters.getZoom(), state.trajectoryProperty().get(i+1).position().longitude()) - mapParameters.getMinX());
                System.out.println(WebMercator.y(mapParameters.getZoom(), state.trajectoryProperty().get(i+1).position().latitude()) - mapParameters.getMinY());

                trajectory.getChildren().add(line(
                        WebMercator.x(mapParameters.getZoom(), state.trajectoryProperty().get(i).position().longitude()) - mapParameters.getMinX(),
                        WebMercator.y(mapParameters.getZoom(), state.trajectoryProperty().get(i).position().latitude()) - mapParameters.getMinY(),
                        WebMercator.x(mapParameters.getZoom(), state.trajectoryProperty().get(i+1).position().longitude()) - mapParameters.getMinX(),
                        WebMercator.y(mapParameters.getZoom(), state.trajectoryProperty().get(i+1).position().latitude()) - mapParameters.getMinY()));
            }
        });

        mapParameters.zoom().addListener((InvalidationListener) change -> {
            trajectory.getChildren().clear();

            for(int i=0 ; i<state.trajectoryProperty().size()-1 ; i++){
                trajectory.getChildren().add(line(
                        WebMercator.x(mapParameters.getZoom(), state.trajectoryProperty().get(i).position().longitude()) - mapParameters.getMinX(),
                        WebMercator.y(mapParameters.getZoom(), state.trajectoryProperty().get(i).position().latitude()) - mapParameters.getMinY(),
                        WebMercator.x(mapParameters.getZoom(), state.trajectoryProperty().get(i+1).position().longitude()) - mapParameters.getMinX(),
                        WebMercator.y(mapParameters.getZoom(), state.trajectoryProperty().get(i+1).position().latitude()) - mapParameters.getMinY()));
            }
        });

        trajectory.getStyleClass().add("trajectory");
        return trajectory;
    }

    //Global Group

    private void aircraft(ObservableAircraftState state){
        Group iconLabel = iconLabel(state);

        Group trajectory = trajectory(clickedPlane.get());

        Group aircraft = new Group(trajectory, iconLabel);

        aircraft.setId(state.getIcaoAddress().string());

        pane.getChildren().add(aircraft);
    }

    private AircraftIcon iconCreation(ObservableAircraftState state){
        AircraftData data = state.getAircraftData();

        return data != null ? (AircraftIcon.iconFor(state.getTypeDesignator(), state.getDescription(), state.getCategory(), state.getWakeTurbulenceCategory()))
                : (AircraftIcon.iconFor(new AircraftTypeDesignator(""), new AircraftDescription(""), state.getCategory(), WakeTurbulenceCategory.UNKNOWN));
    }

}


//#TODO rename en anglais bien correcte