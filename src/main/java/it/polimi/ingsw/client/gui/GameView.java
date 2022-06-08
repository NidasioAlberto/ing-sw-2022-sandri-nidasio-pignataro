package it.polimi.ingsw.client.gui;

import java.util.Scanner;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.Visualizable;
import it.polimi.ingsw.client.gui.objects.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.protocol.answers.*;
import it.polimi.ingsw.protocol.commands.CreateMatchCommand;
import it.polimi.ingsw.protocol.commands.JoinMatchCommand;
import it.polimi.ingsw.protocol.commands.SetNameCommand;
import it.polimi.ingsw.protocol.updates.*;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

/**
 * This class represents the game window and draws all the needed stuff that the server updates and requests
 */
public class GameView extends Application implements Visualizable
{
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static final int ANIMATION_UPDATE_PERIOD_MILLIS = 20;
    public static final int UPDATES_HANDLER_PERIOD_MILLIS = 100;
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
     * Group to which all the objects are added
     */
    private Group group;

    /**
     * Scene lights
     */
    private AmbientLight ambientLight;
    private PointLight pointLight;

    /**
     * Game objects
     */
    private AnimationHandler updater;
    private UpdatesHandler updatesHandler;
    private DrawableIslandCollection islandCollection;
    private DrawableAssistantCollection assistantCollection;
    private DrawableCloudTileCollection cloudTileCollection;
    private DrawableSchoolBoardCollection schoolBoardCollection;

    /**
     * Client which calls the visualizable methods
     */
    private Client client;

    /**
     * The player name of this machine
     */
    private String playerName;

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
        playerName = "pippo";
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
        // Create the group and the scene
        group = new Group();
        scene = new Scene(group, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        // Set the scene background
        scene.setFill(Color.rgb(129, 202, 241));

        // Create the updater to update the objects every period
        updater = new AnimationHandler(ANIMATION_UPDATE_PERIOD_MILLIS);

        // Create the updates handler
        updatesHandler = new UpdatesHandler(UPDATES_HANDLER_PERIOD_MILLIS);

        // Create a mix with ambient and point light
        setupLights();
        group.getChildren().add(ambientLight);
        group.getChildren().add(pointLight);
        // Set the camera up in perspective mode
        setupCamera();

        // Setup the ground plane
        setupGroundPlane();

        /**
         * Collection creations
         */
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert game name");
        playerName = scanner.nextLine();

        // Set the game objects and collections
        assistantCollection = new DrawableAssistantCollection(50, pointLight, ambientLight, group, updater);
        cloudTileCollection = new DrawableCloudTileCollection(40, pointLight, ambientLight, group, updater);
        islandCollection = new DrawableIslandCollection(120, 2.5f, 1.75f, 105, pointLight, ambientLight, group, updater);
        schoolBoardCollection = new DrawableSchoolBoardCollection(350, playerName, pointLight, ambientLight, group, updater);

        assistantCollection.translate(new Point3D(0, -10, -290));
        islandCollection.translate(new Point3D(0, 0, 150));
        cloudTileCollection.translate(new Point3D(0, 0, 100));
        // Start the time scheduled animations
        updater.start();

        // Start the updates handler
        updatesHandler.start();

        // Setup the client
        client = new Client();
        client.setVisualizer(this);
        try
        {
            client.connect();
            new Thread(() -> client.run()).start();
            Thread.sleep(1000);
            client.sendCommand(new SetNameCommand(playerName));
            client.sendCommand(new CreateMatchCommand("m", 2, GameMode.EXPERT));
            client.sendCommand(new JoinMatchCommand("m"));
        } catch (Exception e)
        {
            System.out.println("Connection error");
        }
        scanner.close();

        // Set the client to the action translator
        ActionTranslator.getInstance().setClient(client);

        // Set the gameview instance to the action translator for reset purposes
        ActionTranslator.getInstance().setGameView(this);
    }

    /**
     * This method creates a ground plane for mouse ray casting purposes and to reset the positions when an invalid action is performed
     */
    private void setupGroundPlane()
    {
        // Create the ground plane so that the mouse ray casting hits something
        // just below the playground
        Box groundPlane = new Box(3000, 3000, 0);
        groundPlane.translateYProperty().set(6f);
        groundPlane.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        groundPlane.setMaterial(new PhongMaterial(Color.TRANSPARENT));
        group.getChildren().add(groundPlane);

        // Set the on mouse drag function so that when an action is performed over the plane, the plane
        // sets itself as on dragged item and the action translator resets all
        groundPlane.setMouseTransparent(false);

        groundPlane.setOnMouseDragReleased((event) -> {
            // Set the target of the action
            ActionTranslator.getInstance().setDroppedOnItem("Groundplane");

            // Execute the reset
            ActionTranslator.getInstance().execute();
        });
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
     * Visualizable methods
     */
    public void resetPosition()
    {
        // Resets the position of all the movable components
        schoolBoardCollection.updatePosition(); // Called update instead of reset because the schoolboards actually perform this method
    }

    @Override
    public void displayAssistantCards(AssistantCardsUpdate update)
    {
        // Put the update lambda inside the updater
        updatesHandler.subscribeUpdate(() -> assistantCollection.displayUpdate(update));
    }

    @Override
    public void displayCharacterCardPayload(CharacterCardPayloadUpdate update)
    {

    }

    @Override
    public void displayCharacterCards(CharacterCardsUpdate update)
    {

    }

    @Override
    public void displayCloudTiles(CloudTilesUpdate update)
    {
        // Put the update lambda inside the updater
        updatesHandler.subscribeUpdate(() -> cloudTileCollection.displayUpdate(update));
    }

    @Override
    public void displayIslands(IslandsUpdate update)
    {
        // Put the update lambda inside the updater
        updatesHandler.subscribeUpdate(() -> islandCollection.displayUpdate(update));
    }

    @Override
    public void displayPlayedAssistantCard(PlayedAssistantCardUpdate update)
    {
        // Put the update labmda inside the updater
        updatesHandler.subscribeUpdate(() -> schoolBoardCollection.displayAssistantUpdate(update));
    }

    @Override
    public void displaySchoolboard(SchoolBoardUpdate update)
    {
        // Put the update lambda inside the updater
        updatesHandler.subscribeUpdate(() -> schoolBoardCollection.displayUpdate(update));
    }

    @Override
    public void setCurrentPlayer(CurrentPlayerUpdate update)
    {

    }

    @Override
    public void displayEndMatch(EndMatchAnswer answer)
    {

    }

    @Override
    public void displayError(ErrorAnswer answer)
    {
        // Add the lambda to reset all the positions of movable things
        updatesHandler.subscribeUpdate(() -> resetPosition());
        System.out.println(answer.getErrorMessage());
    }

    @Override
    public void displayJoinedMatch(JoinedMatchAnswer answer)
    {

    }

    @Override
    public void displayMatchesList(MatchesListAnswer answer)
    {

    }

    @Override
    public void displaySetName(SetNameAnswer answer)
    {

    }

    @Override
    public void displayStartMatch(StartMatchAnswer answer)
    {

    }

    /**
     * It starts the application
     */
    public static void main(String args[])
    {
        launch(args);
    }
}
