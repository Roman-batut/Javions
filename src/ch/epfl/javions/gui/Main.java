package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Main extends Application {

    //* Constants

    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final String TITLE = "Javions";
    private static final double ONE_SECOND = 1e+9;

    //* Main Launch

    public static void main(String[] args) {
        launch(args);
    }

    //* Start

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Map instantiation
        Path tileCache = Path.of("tile-cache");
        TileManager tm =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(8, 33_530, 23_070);
        BaseMapController baseMapController = new BaseMapController(tileManager, mapParameters);

        //Création de la base de données
        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        AircraftDatabase db = new AircraftDatabase(f);

        //Manager
        AircraftStateManager aircraftStateManager = new AircraftStateManager(aircraftDatabase);
        ObjectProperty<ObservableAircraftState> selectedAirplane =
                new SimpleObjectProperty<>();
        //Scene
        AircraftTableController aircraftTableController =
                new AircraftTableController(aircraftStateManager.states(), selectedAirplane);
        AircraftController aircraftController =
                new AircraftController(mapParameters, aircraftStateManager.states(), selectedAirplane);
        StatusLineController statusLineController = new StatusLineController();

        //Scene implementation
        StackPane stackPane = new StackPane(baseMapController.pane(), aircraftController.pane());
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(aircraftTableController.pane());
        borderPane.setTop(statusLineController.pane());
        SplitPane root = new SplitPane(stackPane, borderPane);
        root.setOrientation(Orientation.VERTICAL);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.show();

        //Binding og the number of plane
        statusLineController.aircraftCountProperty().bind(Bindings.size(aircraftStateManager.states()));

        ConcurrentLinkedQueue<RawMessage> queue = new ConcurrentLinkedQueue<>();

        Thread reader;

        //From File
        if (!getParameters().getRaw().isEmpty()) {
            String path = getParameters().getRaw().get(0);

             reader = new Thread(() -> {
                long lastone = 0;
                try (DataInputStream s = new DataInputStream(new BufferedInputStream (new FileInputStream(path)))) {
                    byte[] bytes = new byte[RawMessage.LENGTH];
                    while (true) {
                        long timeStampNs = s.readLong();
                        int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                        assert bytesRead == RawMessage.LENGTH;
                        ByteString message = new ByteString(bytes);

                        Thread.sleep((long) ((timeStampNs-lastone)/(1e+6)));
                        lastone = timeStampNs;

                        queue.add(new RawMessage(timeStampNs, message));
                    }
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
            });

        //From System.in
        } else {
            AdsbDemodulator demodulator = new AdsbDemodulator(System.in);

             reader = new Thread(() -> {
                    try {
                        RawMessage nextMessage;
                        while ((nextMessage = demodulator.nextMessage()) != null) {
                            queue.add(nextMessage);
                            Thread.sleep(10);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
             });
        }

        //Thread start
        reader.setDaemon(true);
        reader.start();

        selectedAirplane.addListener((e) -> baseMapController.centerOn(selectedAirplane.get().getPosition()));

        //Animation des aéronefs
        new AnimationTimer() {
            long last =0;
            @Override
            public void handle(long now) {
                try {
                    if(now-last >= ONE_SECOND){
                        aircraftStateManager.purge();
                        last = now;
                    }
                    while (!queue.isEmpty()){
                        Message m = MessageParser.parse(queue.remove());
                        if (m != null) {
                            aircraftStateManager.updateWithMessage(m);
                            statusLineController.messageCountProperty()
                                    .set(statusLineController.messageCountProperty().get()+1);
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }
}
