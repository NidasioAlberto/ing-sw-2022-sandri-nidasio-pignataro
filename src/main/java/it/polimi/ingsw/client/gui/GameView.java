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

    public static final int ANIMATION_UPDATE_PERIOD_MILLIS = 100;
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
     * Scene lights
     */
    private AmbientLight ambientLight;
    private PointLight pointLight;

    /**
     * Game objects
     */
    private DrawableMotherNature motherNature;
    private DrawableIslandCollection islandCollection;
    private DrawableSchoolBoard schoolBoard;

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
    public GameView() { this(ViewMode.MODE_3D); }

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
        // Set the scene background
        scene.setFill(Color.rgb(129, 202, 241));

        // Create a mix with ambient and point light
        setupLights();
        // Set the camera up in perspective mode
        setupCamera();

        // Create all the game components
        motherNature = new DrawableMotherNature(3, 7.5f, 1.5f);
        islandCollection = new DrawableIslandCollection(100, 2.5f, 1.5f, 100);
        schoolBoard = new DrawableSchoolBoard(350, 350 / DrawableSchoolBoard.SCALE_FACTOR);

        // Eventually modify the single objects for window design things
        islandCollection.translate(new Point3D(0, 0, 150));
        schoolBoard.translate(new Point3D(0, 0, -175));

        // Add all the created objects to the collection of drawable objects
        drawableObjects.add(motherNature);
        drawableObjects.add(islandCollection);
        drawableObjects.add(schoolBoard);

        // Add the lights to the view
        group.getChildren().add(pointLight);
        group.getChildren().add(ambientLight);

        // Add all the game components to the group and subscribe them to the point light
        for(DrawableObject object : drawableObjects)
        {
            object.addToGroup(group);
            object.subscribeToLight(pointLight);
        }

        // Start the time scheduled animations
        startAnimationUpdates();
    }

    /**
     * This method setups all the camera stuff. It is a perspective camera
     * and also it is based on an object attribute, so there is no need
     * to return something.
     */
    private void setupCamera()
    {
        // Set the camera to perspective and to look constantly to the correct point
        // without any adjustment.
        camera = new PerspectiveCamera(true);
        // Adjusting clipping properties
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);
        scene.setCamera(camera);
    }

    /**
     * This method setups all the lights (ambient and point) to be used
     * in the scene. The lights are actually object private attributes
     * so the method returns nothing.
     */
    private void setupLights()
    {
        ambientLight = new AmbientLight();
        ambientLight.setColor(Color.WHITE);
        pointLight = new PointLight();
        pointLight.setColor(Color.WHITE);
        pointLight.translateYProperty().set(-1000);
        pointLight.setRotationAxis(new Point3D(1, 0 ,0));
        pointLight.rotateProperty().set(90);
        pointLight.setLinearAttenuation(-0.0003);
    }

    /**
     * Method that sets periodically updates about Graphical objects
     */
    private void startAnimationUpdates()
    {
        // Create the scheduled task
        Timeline animationUpdates = new Timeline(
                new KeyFrame(Duration.millis(ANIMATION_UPDATE_PERIOD_MILLIS), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        // To handle the animations update i call every drawable object
                        for(DrawableObject object : drawableObjects)
                            object.updateAnimation();
                    }
                })
        );
        // Set the task as infinite and start the task
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
            camera.translateZProperty().set(30);
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

        // Set the current visualization and position the camera according to that
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
