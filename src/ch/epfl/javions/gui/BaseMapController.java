package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;

import java.io.IOException;

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

       //Peut etre inver height width pas sur ....
       double vectorX = coordX - pane.getWidth() - mapParameters.getMinX() ;
       double vectorY = coordY - pane.getHeight() - mapParameters.getMinY() ;

       mapParameters.scroll(vectorX, vectorY);
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        int xSize = (int) Math.ceil(canvas.getWidth()/256)+1;
        int ySize = (int) Math.ceil(canvas.getHeight()/256)+1;

        int tileX = (int) Math.floor(mapParameters.getMinX()/256);
        double offsetX = tileX*256 - mapParameters.getMinX();
        int tileY = (int) Math.floor(mapParameters.getMinY()/256);
        double offsetY = tileY*256 - mapParameters.getMinY();

        for (int x=0 ; x<xSize ; x++) {
            for (int y=0 ; y<ySize ; y++) {
                try {
                    System.out.println("ta mere la pute");
                    graphicsContext.drawImage(tileManager.imageForTileAt(new TileManager.TileId(mapParameters.getZoom(), tileX+x, tileY+y)), x*256+offsetX, y*256+offsetY);
                } catch (IOException e){
                    System.out.println("ma mere la pute");
                }

            }
        }

    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
