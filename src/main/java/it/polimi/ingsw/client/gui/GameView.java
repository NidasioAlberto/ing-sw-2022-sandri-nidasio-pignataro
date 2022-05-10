package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.objects.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the game window and draws all the needed stuff that
 * the server updates and requests
 */
public class GameView extends Application
{
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

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
     * Game objects
     */
    private DrawableMotherNature motherNature;

    /**
     * Collection of all the drawable objects, useful to the Animation Updates
     */
    private List<DrawableObject> drawableObjects;

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
        // Create the collection of all the drawable objects
        drawableObjects = new ArrayList<DrawableObject>();

        // Create the group and the scene
        Group group = new Group();
        scene = new Scene(group, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);

        // Create a mix with ambient and point light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(Color.WHITE);
        PointLight light = new PointLight();
        light.setColor(Color.WHITE);
        light.translateYProperty().set(-1000);
        light.setRotationAxis(new Point3D(1, 0 ,0));
        light.rotateProperty().set(90);
        light.setLinearAttenuation(-0.0003);


        // Add the lights to the view
        group.getChildren().add(light);
        group.getChildren().add(ambient);

        // Set the scene background
        scene.setFill(Color.rgb(129, 202, 241));

        // Set the camera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);
        scene.setCamera(camera);

        // Create all the game components
        motherNature = new DrawableMotherNature(3, 20, 4);
        DrawableIslandCollection collection = new DrawableIslandCollection(100, 2.5f, 1.5f, 100);

        // Add all the created objects to the collection of drawable objects
        drawableObjects.add(motherNature);
        drawableObjects.add(collection);

        // Add all the game components to the group
        //motherNature.addToGroup(group);
        motherNature.subscribeToLight(light);
        collection.addToGroup(group);

        // Start the time scheduled animations
        startAnimationUpdates();
    }

    /**
     * Method that sets periodically updates about Graphical objects
     */
    private void startAnimationUpdates()
    {
        Timeline animationUpdates = new Timeline(
                new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        // To handle the animations update i call every drawable object
                        for(DrawableObject object : drawableObjects)
                            object.updateAnimation();
                    }
                })
        );
        animationUpdates.setCycleCount(Timeline.INDEFINITE);
        animationUpdates.play();
    }

    /**
     * This method checks what is the current camera view and changes things to do so
     */
    private void updateCameraView()
    {
        // Depending on the position i rotate and traslate the camera
        if(viewMode == ViewMode.MODE_2D)
        {
            camera.translateYProperty().set(-1000);
            camera.setRotationAxis(new Point3D(1, 0, 0));
            camera.rotateProperty().set(-90);
        }
        else if(viewMode == ViewMode.MODE_3D)
        {
            int angle = 55;
            //camera.translateYProperty().set(-1000 * Math.sin(angle));
            //camera.translateZProperty().set(-1000 * Math.cos(angle));

            camera.translateYProperty().set(-1000 * Math.sin(Math.toRadians(angle)));
            camera.translateZProperty().set(-1000 * Math.cos(Math.toRadians(angle)));
            camera.setRotationAxis(new Point3D(1, 0, 0));
            camera.rotateProperty().set(-angle);
        }
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
        setup();
        viewMode = ViewMode.MODE_3D;
        updateCameraView();

        // Show the window with the correct title
        stage.setTitle("Eriantys Game");
        // Set the scene
        stage.setScene(scene);
        stage.show();
    }

    /**
     * It starts the application
     */
    public static void main(String args[])
    {
        launch(args);
    }
}
