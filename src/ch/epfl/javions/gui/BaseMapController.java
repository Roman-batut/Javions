package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;

public final class BaseMapController {

    private TileManager tileManager;
    private MapParameters mapParameters;
    private Pane pane;
    private Canvas canvas;
    private boolean redrawNeeded;

    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
        redrawNeeded = false;
        canvas = new Canvas();
        pane = new Pane(canvas);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    public Pane pane(){
        return pane;
    }

    public void centerOn(GeoPos geoPos){
       double coordX = WebMercator.x(mapParameters.getZoom(), geoPos.longitude());
       double coordY = WebMercator.y(mapParameters.getZoom(), geoPos.latitude());

//       Peut etre inver height width pas sur ....
       double vectorX = coordX - pane.getWidth() - mapParameters.getMinX() ;
       double vectorY = coordY - pane.getHeight() - mapParameters.getMinY() ;
       mapParameters.scroll(vectorX, vectorY);
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        // … à faire : dessin de la carte
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
