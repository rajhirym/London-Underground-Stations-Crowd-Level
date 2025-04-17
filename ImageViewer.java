import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.HashMap;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * This is the main javaFX class of the application.
 * It draws the map onto the canvas, draws each station according to the longitudes and latitudes.
 * It handles user clicks and gives information about the crowding level of each station when clicked. 
 *
 * @author Rym Rajhi
 */

public class ImageViewer extends Application {
    private TFLStations TFLStations = new TFLStations();
    private TfLCrowdingAPI crowding= new TfLCrowdingAPI();
    private double mapWidth;
    private double mapHeight;
    private Canvas canvas; 
    private GraphicsContext gc;
    private ArrayList<double[]> allCoords= new ArrayList<>();
    private HashMap<double[], StationPoint> stationHashMap= new HashMap<>();

    private double latTop = 51.627741;
    private double latBottom = 51.395246;
    private double lonLeft = -0.40653443;
    private double lonRight = 0.20205370;

    @Override
    public void start(Stage primaryStage) {
        Image image = new Image("London.png");
        mapWidth= image.getWidth();
        mapHeight= image.getHeight();

        canvas = new Canvas(mapWidth,mapHeight); // width, height
        gc = canvas.getGraphicsContext2D();

        //draw the image on the canvas
        gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
        drawStations();
        canvas.setOnMouseClicked(event -> handleMapClick(event.getX(), event.getY()));
        ScrollPane scrollMap = new ScrollPane();
        scrollMap.setPrefSize(900,700);
        scrollMap.setContent(canvas);

        //Add canvas to a layout

        VBox root = new VBox();
        createMenuBar(root);      
        root.getChildren().add(scrollMap);  

        Scene scene = new Scene(root);

        //Setup stage
        primaryStage.setTitle("Underground Stations Map");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Creates the menubar, allowing you to access additional options in the 
     * map viewer. 
     * @param root The pane/container to add the menubar into.
     */
    private void createMenuBar(Pane root){
        MenuBar menuBar = new MenuBar();
        root.getChildren().add(menuBar);
        Menu helpMenu = new Menu("Help");

        MenuItem introItem = new MenuItem("Introduction");
        introItem.setOnAction(this::createWelcomePage);

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(this::showAbout);

        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setOnAction(this::quitApplication);

        helpMenu.getItems().addAll(introItem, aboutItem, quitItem);
        menuBar.getMenus().addAll(helpMenu);

    }

    public void createWelcomePage(ActionEvent event){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Welcome page");
        alert.setContentText("This application allows the user to view how crowded each underground station in London is, in real time, by click on it.");

        alert.showAndWait();
    }

    /**
     * Show a brief about dialog with the version and the author name
     * @param event the event triggering the dialog to show.
     */
    public void showAbout(ActionEvent event){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About Underground Stations Map");
        alert.setHeaderText("Underground Stations Map \nVersion: 1.0");
        alert.setContentText("Authors: Rym Rajhi");

        alert.showAndWait();
    }

    /**
     * Closes the application
     */
    private void quitApplication(ActionEvent event)
    {
        System.exit(0);
    }

    /**
     * This method checks if the user clicked on a station
     * If the mouse click is on a station an information dialog will pop up. 
     */
    private void handleMapClick(double clickX, double clickY) {
        double radius = 8.0; // Define a small radius to check proximity to a station
        // Loop through all coordinates and check if the click is close to any station
        for (double[] stationCoords : allCoords) {
            double stationX = stationCoords[0];
            double stationY = stationCoords[1];
            // Calculate the distance between the click and the station
            double dx = clickX - stationX;
            double dy = clickY - stationY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= radius) {
                String stationName= stationHashMap.get(stationCoords).StationName();
                String stationId= stationHashMap.get(stationCoords).naptanId();
                
                showStationDialog(stationName, stationId);
                return; // Station found, no need to check further
            }
        }
    }

    /**
     * Whenever a station is clicked on the canvas this dialog will pop up. 
     * It gives the station's sname and the crowding level at that time. 
     */
    private void showStationDialog(String stationName, String stationId) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Station Selected");
        alert.setHeaderText(null);
        alert.setContentText("You clicked on station: " + stationName +"\n"+ crowding.getCrowdingInfo(stationId));
        alert.showAndWait();
    }

    /**
     * This methods uses the longitude and the latitude of each station to return an array containing the corresponding 
     * x and y coords of that point on the map
     */
    private double[] mapStationsCoords(double lon, double lat){
        double x = (lon - lonLeft) / (lonRight - lonLeft) * mapWidth;
        double y = (latTop - lat) / (latTop - latBottom) * mapHeight;
        double[] coords= new double[2];
        coords[0]=x;
        coords[1]=y;

        return coords;
    }

    /**
     * This methods loops through each station point and paints them onto the canvas
     */
    private void drawStations()
    {   ArrayList<StationPoint> stationDetails = TFLStations.getAllStatiosnDetails();
        for (StationPoint sp: stationDetails)
        {   
            if (sp.lat() >= latBottom && sp.lat() <= latTop &&
            sp.lon() >= lonLeft && sp.lon() <= lonRight) {
                double[] coords = mapStationsCoords(sp.lon(), sp.lat());

                allCoords.add(coords);
                stationHashMap.put(coords,sp);

                gc.setFill(Color.RED);
                gc.fillOval(coords[0], coords[1], 5, 5);
            }
        }
    }

}

