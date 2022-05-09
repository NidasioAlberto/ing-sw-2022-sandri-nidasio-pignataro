package it.polimi.ingsw.client.gui;

import javafx.application.Application;
import javafx.scene.Camera;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This class represents the game window and draws all the needed stuff that
 * the server updates and requests
 */
public class GameView extends Application
{
    public static final int WIDTH = 300;
    public static final int HEIGHT = 300;

    /**
     * This is the camera view, it can be moved around (switch from 3D to 2D)
     */
    private Camera camera;

    /**
     * This tells the applications what is the view that the player wants
     */
    private ViewMode viewMode;

    /**
     * This is the window scene. It contains all the components
     */
    private Scene scene;

    /**
     * Constructor
     * @param mode The view mode that we want the game to start with
     */
    public GameView(ViewMode mode)
    {
        // Useless
        super();

        if(mode == null)
            throw new NullPointerException("[GameView] Null view mode");

        this.viewMode = mode;
        setup();
    }

    /**
     * Void constructor
     */
    public GameView() { this(ViewMode.MODE_2D); }

    /**
     * This method is used to instantiate all the useful things
     */
    private void setup()
    {

    }

    /**
     * This method checks what is the current camera view and changes things to do so
     */
    private void updateCameraView()
    {

    }

    /**
     * This method sets the camera view and updates the view
     * @param mode The new view
     */
    public void setViewMode(ViewMode mode)
    {
        if(mode == null)
            throw new NullPointerException("[GameView] Null view mode");

        if(mode != viewMode)
        {
            // If they differ i change the camera view
            this.viewMode = mode;
            updateCameraView();
        }
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        // I need to think that this method is our main.
        // It has all the references to this object, and can pass it as argument
        // to all the needed objects
        StackPane root = new StackPane();
        scene = new Scene(root, WIDTH, HEIGHT);

        stage.setTitle("Eriantys Game");
        // Set the scene
        stage.setScene(scene);
        stage.show();


        // I need to set the default params
        viewMode = ViewMode.MODE_2D;
    }

    /**
     * It starts the application
     */
    public static void main(String args[])
    {
        launch(args);
    }
}
