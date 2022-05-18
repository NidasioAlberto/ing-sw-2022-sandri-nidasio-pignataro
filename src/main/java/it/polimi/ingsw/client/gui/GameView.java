package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.objects.*;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.TowerColor;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the game window and draws all the needed stuff that the server updates and requests
 */
public class GameView extends Application
{
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static final int ANIMATION_UPDATE_PERIOD_MILLIS = 20;
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
    private AnimationHandler updater;
    private DrawableMotherNature motherNature;
    private DrawableIslandCollection islandCollection;
    private DrawableSchoolBoard schoolBoard;

    /**
     * Collection of all the drawable objects, useful for repetitive tasks
     */
    private List<DrawableObject> drawableObjects;

    /**
     * Constructor
     * 
     * @param mode The view mode that we want the game to start with
     */
    public GameView(ViewMode mode)
    {
        // Useless
        super();

        if (mode == null)
            throw new NullPointerException("[GameView] Null view mode");

        this.viewMode = mode;
    }

    /**
     * Void constructor
     */
    public GameView()
    {
        this(ViewMode.MODE_3D);
    }

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

        // Create the updater to update the objects every period
        updater = new AnimationHandler(ANIMATION_UPDATE_PERIOD_MILLIS);

        // Create a mix with ambient and point light
        setupLights();
        // Set the camera up in perspective mode
        setupCamera();

        // Create the ground plane so that the mouse ray casting hits something
        // just below the playground
        Box groundPlane = new Box(3000, 3000, 0);
        groundPlane.translateYProperty().set(6f);
        groundPlane.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        groundPlane.setMaterial(new PhongMaterial(Color.TRANSPARENT));

        // Create all the game components
        group.getChildren().add(groundPlane);
        motherNature = new DrawableMotherNature(3, 7.5f, 1.5f, updater);
        islandCollection = new DrawableIslandCollection(100, 2.5f, 1.5f, 100, updater);
        schoolBoard = new DrawableSchoolBoard(350, updater);
        DrawableSchoolBoard s2 = new DrawableSchoolBoard(350, updater);
        DrawableSchoolBoard s3 = new DrawableSchoolBoard(350, updater);

        // Eventually modify the single objects for window design things
        islandCollection.translate(new Point3D(0, 0, 150));
        schoolBoard.translate(new Point3D(0, 0, -175));
        s2.translate(new Point3D(-400, 0, 150));
        s3.translate(new Point3D(400, 0, 150));
        s2.addRotation(new Rotate(90, new Point3D(0, 1, 0)));
        s3.addRotation(new Rotate(-90, new Point3D(0, 1, 0)));

        // Add all the created objects to the collection of drawable objects
        drawableObjects.add(motherNature);
        drawableObjects.add(islandCollection);
        drawableObjects.add(schoolBoard);
        drawableObjects.add(s2);
        drawableObjects.add(s3);

        // Dynamic object add to schoolboard
        schoolBoard.addProfessor(SchoolColor.GREEN, group, pointLight);
        schoolBoard.addProfessor(SchoolColor.RED, group, pointLight);
        schoolBoard.addProfessor(SchoolColor.YELLOW, group, pointLight);
        schoolBoard.addProfessor(SchoolColor.PINK, group, pointLight);
        schoolBoard.addProfessor(SchoolColor.BLUE, group, pointLight);

        s2.addProfessor(SchoolColor.GREEN, group, pointLight);
        s2.addProfessor(SchoolColor.RED, group, pointLight);
        s2.addProfessor(SchoolColor.YELLOW, group, pointLight);
        s2.addProfessor(SchoolColor.PINK, group, pointLight);
        s2.addProfessor(SchoolColor.BLUE, group, pointLight);

        s3.addProfessor(SchoolColor.GREEN, group, pointLight);
        s3.addProfessor(SchoolColor.RED, group, pointLight);
        s3.addProfessor(SchoolColor.YELLOW, group, pointLight);
        s3.addProfessor(SchoolColor.PINK, group, pointLight);
        s3.addProfessor(SchoolColor.BLUE, group, pointLight);
        for(int i = 0; i < 8; i++)
        {
            schoolBoard.addStudentToDining(SchoolColor.GREEN, group, pointLight);
            schoolBoard.addStudentToDining(SchoolColor.RED, group, pointLight);
            schoolBoard.addStudentToDining(SchoolColor.YELLOW, group, pointLight);
            schoolBoard.addStudentToDining(SchoolColor.PINK, group, pointLight);
            schoolBoard.addStudentToDining(SchoolColor.BLUE, group, pointLight);
            schoolBoard.addStudentToEntrance(SchoolColor.RED, group, pointLight);
            schoolBoard.addTower(TowerColor.WHITE, group, pointLight);

            s2.addStudentToDining(SchoolColor.GREEN, group, pointLight);
            s2.addStudentToDining(SchoolColor.RED, group, pointLight);
            s2.addStudentToDining(SchoolColor.YELLOW, group, pointLight);
            s2.addStudentToDining(SchoolColor.PINK, group, pointLight);
            s2.addStudentToDining(SchoolColor.BLUE, group, pointLight);
            s2.addStudentToEntrance(SchoolColor.RED, group, pointLight);
            s2.addTower(TowerColor.BLACK, group, pointLight);

            s3.addStudentToDining(SchoolColor.GREEN, group, pointLight);
            s3.addStudentToDining(SchoolColor.RED, group, pointLight);
            s3.addStudentToDining(SchoolColor.YELLOW, group, pointLight);
            s3.addStudentToDining(SchoolColor.PINK, group, pointLight);
            s3.addStudentToDining(SchoolColor.BLUE, group, pointLight);
            s3.addStudentToEntrance(SchoolColor.RED, group, pointLight);
            s3.addTower(TowerColor.GREY, group, pointLight);
        }

        // Add the lights to the view
        group.getChildren().add(pointLight);
        group.getChildren().add(ambientLight);

        // Add all the game components to the group and subscribe them to the point light
        for (DrawableObject object : drawableObjects)
        {
            object.addToGroup(group);
            object.subscribeToPointLight(pointLight);
            object.subscribeToAmbientLight(ambientLight);
        }

        motherNature.addAnimationPosition(schoolBoard.getPosition(), 2);
        motherNature.addAnimationPosition(islandCollection.getPosition(), 2);
        motherNature.addAnimationPosition(schoolBoard.getPosition(), 2);
        motherNature.addAnimationPosition(islandCollection.getPosition(), 2);

        // Start the time scheduled animations
        updater.start();
    }

    /**
     * This method setups all the camera stuff. It is a perspective camera and also it is based on an object attribute, so there is no need to return
     * something.
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
     * This method setups all the lights (ambient and point) to be used in the scene. The lights are actually object private attributes so the method
     * returns nothing.
     */
    private void setupLights()
    {
        ambientLight = new AmbientLight();
        ambientLight.setColor(Color.WHITE);
        pointLight = new PointLight();
        pointLight.setColor(Color.WHITE);
        pointLight.translateYProperty().set(-1000);
        pointLight.translateZProperty().set(-400);
        pointLight.setRotationAxis(new Point3D(1, 0, 0));
        pointLight.rotateProperty().set(90);
        pointLight.setLinearAttenuation(-0.0003);
    }

    /**
     * This method checks what is the current camera view and changes things to do so
     */
    private void updateCameraView()
    {
        // Depending on the position i rotate and traslate the camera
        if (viewMode == ViewMode.MODE_2D)
        {
            camera.translateYProperty().set(-1000);
            camera.translateZProperty().set(30);
            camera.setRotationAxis(new Point3D(1, 0, 0));
            camera.rotateProperty().set(-90);
        } else if (viewMode == ViewMode.MODE_3D)
        {
            int angle = 55;
            // camera.translateYProperty().set(-1000 * Math.sin(angle));
            // camera.translateZProperty().set(-1000 * Math.cos(angle));

            camera.translateYProperty().set(-1000 * Math.sin(Math.toRadians(angle)));
            camera.translateZProperty().set(-1000 * Math.cos(Math.toRadians(angle)));
            camera.setRotationAxis(new Point3D(1, 0, 0));
            camera.rotateProperty().set(-angle);
        }
    }

    /**
     * This method sets the camera view and updates the view
     * 
     * @param mode The new view
     */
    public void setViewMode(ViewMode mode)
    {
        if (mode == null)
            throw new NullPointerException("[GameView] Null view mode");

        if (mode != viewMode)
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
