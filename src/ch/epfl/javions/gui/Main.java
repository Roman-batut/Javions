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
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Main extends Application {

    //* Constants

    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final String TITLE = "Javions";
    private static final double ONE_SECOND = 1e+9;
    private static final String TILE_CACHE_PATH = "tile-cache";
    private static final String OPENSTREETMAP_SERVER = "tile.openstreetmap.org";
    private static final String AIRCRAFT_INFOS = "/aircraft.zip";
    private static final double MILLI_CONVERT = 1e+6;
    private static final int INIT_ZOOM = 8;
    private static final int INIT_MIN_X = 33_530;
    private static final int INIT_MIN_Y = 23_070;

    //* Main Launch

    public static void main(String[] args) {
        launch(args);
    }

    //* Start

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Map instantiation
        Path tileCache = Path.of(TILE_CACHE_PATH);
        TileManager tileManager =
                new TileManager(tileCache, OPENSTREETMAP_SERVER);
        MapParameters mapParameters =
                new MapParameters(INIT_ZOOM, INIT_MIN_X, INIT_MIN_Y);
        BaseMapController baseMapController = new BaseMapController(tileManager, mapParameters);

        //Création de la base de données
        URL dbUrl = getClass().getResource(AIRCRAFT_INFOS);
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        AircraftDatabase aircraftDatabase = new AircraftDatabase(f);

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

                        Thread.sleep((long) ((timeStampNs-lastone) / MILLI_CONVERT));
                        lastone = timeStampNs;

                        queue.add(new RawMessage(timeStampNs, message));
                    }
                }catch (IOException | InterruptedException e) {
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
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
             });
        }

        //Thread start
        reader.setDaemon(true);
        reader.start();

        aircraftTableController.setOnDoubleClick(v -> baseMapController.centerOn(v.getPosition()));

        //Animation des aéronefs
        new AnimationTimer() {
            long last = 0;
            @Override
            public void handle(long now) {
                try {
                    if(now - last >= ONE_SECOND){
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
