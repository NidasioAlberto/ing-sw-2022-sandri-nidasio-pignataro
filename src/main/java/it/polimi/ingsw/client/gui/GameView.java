package it.polimi.ingsw.client.gui;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.Visualizable;
import it.polimi.ingsw.client.gui.objects.*;
import it.polimi.ingsw.client.gui.objects.types.StudentType;
import it.polimi.ingsw.protocol.answers.*;
import it.polimi.ingsw.protocol.updates.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.image.Image;
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

    public static final int CAMERA_ANGLE = 55;
    /**
     * This is the camera view, it can be moved around (switch from 3D to 2D)
     */
    private Camera camera;

    /**
     * This tells the applications what is the view that the player wants
     */
    private ViewMode viewMode;

    /**
     * This is the stage that contains the scene.
     */
    private Stage stage;

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
    private DrawableCharacterCardCollection characterCardCollection;
    private DrawableErrorMessage errorMessage;
    private DrawableIndex index;

    // Selectable students
    private DrawableStudent redSelectableStudent;
    private DrawableStudent greenSelectableStudent;
    private DrawableStudent blueSelectableStudent;
    private DrawableStudent pinkSelectableStudent;
    private DrawableStudent yellowSelectableStudent;

    /**
     * Client which calls the visualizable methods
     */
    private Client client;

    /**
     * The player name of this machine
     */
    private String playerName;


    /**
     * Controller of lobby's scenes.
     */
    private SceneController sceneController;

    /**
     * Set true when a StartMatchAnswer is received.
     */
    private boolean isMatchStarted;

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
        try
        {
            // Create the group and the scene
            group = new Group();
            scene = new Scene(group, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);

            // Set the scene background
            scene.setFill(Color.rgb(129, 202, 241));
            scene.getStylesheets().add(getClass().getResource("/Lobby/style.css").toExternalForm());

            // Create the updater to update the objects every period
            updater = new AnimationHandler(ANIMATION_UPDATE_PERIOD_MILLIS);

            // Create the updates handler
            updatesHandler = new UpdatesHandler(UPDATES_HANDLER_PERIOD_MILLIS);

            // Start the time scheduled animations
            updater.start();

            // Start the updates handler
            updatesHandler.start();

            // Setup the client
            client = new Client();
            client.setVisualizer(this);
            sceneController = new SceneController(this, client, scene);
            sceneController.setRoot("/Lobby/login.fxml");

            // Set the client to the action translator
            ActionTranslator.getInstance().setClient(client);

            // Set the gameview instance to the action translator for reset purposes
            ActionTranslator.getInstance().setGameView(this);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error while creating the scene");
        }
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
            // camera.translateYProperty().set(-1000 * Math.sin(angle));
            // camera.translateZProperty().set(-1000 * Math.cos(angle));

            camera.translateYProperty().set(-1000 * Math.sin(Math.toRadians(CAMERA_ANGLE)));
            camera.translateZProperty().set(-1000 * Math.cos(Math.toRadians(CAMERA_ANGLE)));
            camera.setRotationAxis(new Point3D(1, 0, 0));
            camera.rotateProperty().set(-CAMERA_ANGLE);
        }
    }

    /**
     * This method is used to delete all the graphical objects and unsubscribe them from every possible javafx thing, to let the garbage collector
     * destroy all the instances
     */
    public void clearAll()
    {
        // Only if the match is already started
        if (!isMatchStarted)
            return;

        assistantCollection.clearAll();
        cloudTileCollection.clearAll();
        islandCollection.clearAll();
        schoolBoardCollection.clearAll();
        characterCardCollection.clearAll();

        // Destroy also the camera
        scene.setCamera(new ParallelCamera());

        // Set the camera pointer to null
        camera = null;

        // Set the match not started
        isMatchStarted = false;
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
        this.stage = stage;
        // Show the window with the correct title
        stage.setTitle("Eriantys Game");
        stage.getIcons().add(new Image("EryantisIcon.png"));

        // The lobby is not resizable
        stage.setResizable(false);

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
        characterCardCollection.updatePosition();
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
        // Put the update lambda inside the updater
        updatesHandler.subscribeUpdate(() -> characterCardCollection.displayUpdate(update));
    }

    @Override
    public void displayCharacterCards(CharacterCardsUpdate update)
    {
        // Put the update lambda inside the updater
        updatesHandler.subscribeUpdate(() -> characterCardCollection.displayUpdate(update));
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
        // Put the update lambda inside the updater
        updatesHandler.subscribeUpdate(() -> schoolBoardCollection.displayUpdate(update));
    }

    @Override
    public void displayEndMatch(EndMatchAnswer answer)
    {
        updatesHandler.subscribeUpdate(() -> {
            // visualize the error
            errorMessage.setTextColor(Color.BLACK);
            errorMessage.setText(answer.toString());
            errorMessage.setBackground(Color.rgb(132, 245, 66));
            errorMessage.addAnimationPosition(new Point3D(150, 0, -110), 30);

            // Add a lot of positions so that it stays where it should for a while
            for (int i = 0; i < 200; i++)
                errorMessage.addAnimationPosition(new Point3D(150, 0, -110), 30);

            // After the message is done return back
            errorMessage.addAnimationPosition(new Point3D(300, 0, -110), 30);

            // Set a timer to return to lobby
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                updatesHandler.subscribeUpdate(() -> {
                    clearAll();
                    sceneController.setRoot("/Lobby/lobby.fxml");
                });
            }, 10, TimeUnit.SECONDS);

        });
        // Moving to lobby which is not resizable
        Platform.runLater(() -> stage.setResizable(false));
    }

    @Override
    public void displayError(ErrorAnswer answer)
    {
        // Add the lambda to reset all the positions of movable things
        if (isMatchStarted)
        {
            updatesHandler.subscribeUpdate(() -> resetPosition());
            updatesHandler.subscribeUpdate(() -> {
                // this.clearAll();
                // visualize the error
                errorMessage.setTextColor(Color.WHITE);
                errorMessage.setText(answer.getErrorMessage());
                errorMessage.setBackground(Color.rgb(245, 66, 66));
                errorMessage.addAnimationPosition(new Point3D(150, 0, -110), 30);

                // Add a lot of positions so that it stays where it should for a while
                for (int i = 0; i < 200; i++)
                    errorMessage.addAnimationPosition(new Point3D(150, 0, -110), 30);

                // After the message is done return back
                errorMessage.addAnimationPosition(new Point3D(300, 0, -110), 30);
            });
            // System.out.println(answer.getErrorMessage());
        } else
            updatesHandler.subscribeUpdate(() -> sceneController.displayError(answer.getErrorMessage()));
    }

    @Override
    public void displayJoinedMatch(JoinedMatchAnswer answer)
    {
        updatesHandler.subscribeUpdate(() -> sceneController.displayJoinedMatch());
    }

    @Override
    public void displayMatchesList(MatchesListAnswer answer)
    {
        updatesHandler.subscribeUpdate(() -> sceneController.displayMatchesList(answer));
    }

    @Override
    public void displaySetName(SetNameAnswer answer)
    {
        playerName = answer.getName();
        createCollections();
        updatesHandler.subscribeUpdate(() -> sceneController.displaySetName(answer));
    }

    @Override
    public void displayStartMatch(StartMatchAnswer answer)
    {
        isMatchStarted = true;
    }

    public void matchBegin()
    {
        // Set the camera up in perspective mode
        setupCamera();

        // Setup the ground plane if nothing has already been created once (i use index but we can use every drawable object/collection)
        if (index == null)
            setupGroundPlane();

        scene.setRoot(group);

        // Set the current visualization and position the camera according to that
        updateCameraView();

        // Set the match started to true
        isMatchStarted = true;

        // The match is resizable
        stage.setResizable(true);
    }

    @Override
    public void displayConnectionError(ErrorAnswer answer)
    {
        updatesHandler.subscribeUpdate(() -> sceneController.displayConnectionError(answer.getErrorMessage()));
    }

    private void createCollections()
    {
        setupLights();
        group.getChildren().add(ambientLight);
        group.getChildren().add(pointLight);

        /**
         * Collection creations
         */
        // Set the game objects and collections
        index = new DrawableIndex(CAMERA_ANGLE, updater);
        assistantCollection = new DrawableAssistantCollection(50, pointLight, ambientLight, group, updater);
        cloudTileCollection = new DrawableCloudTileCollection(40, pointLight, ambientLight, group, updater);
        islandCollection = new DrawableIslandCollection(120, 2.5f, 1.75f, 105, index, pointLight, ambientLight, group, updater);
        schoolBoardCollection = new DrawableSchoolBoardCollection(350, playerName, pointLight, ambientLight, group, updater);
        characterCardCollection = new DrawableCharacterCardCollection(60, pointLight, ambientLight, group, updater);
        errorMessage = new DrawableErrorMessage(CAMERA_ANGLE, 400, updater);
        redSelectableStudent = new DrawableStudent(StudentType.RED, updater);
        greenSelectableStudent = new DrawableStudent(StudentType.GREEN, updater);
        blueSelectableStudent = new DrawableStudent(StudentType.BLUE, updater);
        pinkSelectableStudent = new DrawableStudent(StudentType.PINK, updater);
        yellowSelectableStudent = new DrawableStudent(StudentType.YELLOW, updater);

        // Add the index to group and light
        index.addToGroup(group);
        index.subscribeToAmbientLight(ambientLight);

        // Add the error message to group and light
        errorMessage.addToGroup(group);
        errorMessage.subscribeToAmbientLight(ambientLight);

        // Add to group and light selectable students
        redSelectableStudent.addToGroup(group);
        redSelectableStudent.subscribeToPointLight(pointLight);
        greenSelectableStudent.addToGroup(group);
        greenSelectableStudent.subscribeToPointLight(pointLight);
        blueSelectableStudent.addToGroup(group);
        blueSelectableStudent.subscribeToPointLight(pointLight);
        pinkSelectableStudent.addToGroup(group);
        pinkSelectableStudent.subscribeToPointLight(pointLight);
        yellowSelectableStudent.addToGroup(group);
        yellowSelectableStudent.subscribeToPointLight(pointLight);

        assistantCollection.translate(new Point3D(0, -10, -290));
        islandCollection.translate(new Point3D(0, 0, 150));
        cloudTileCollection.translate(new Point3D(0, 0, 200));
        characterCardCollection.translate(new Point3D(0, 0, 100));
        // X = 300 hidden, X=150 visible
        errorMessage.translate(new Point3D(300, 0, -110));
        index.translate(new Point3D(-650, 0, -25));

        // Position the selectable students
        redSelectableStudent.translate(new Point3D(-60, 0, 250));
        greenSelectableStudent.translate(new Point3D(-30, 0, 250));
        blueSelectableStudent.translate(new Point3D(0, 0, 250));
        pinkSelectableStudent.translate(new Point3D(30, 0, 250));
        yellowSelectableStudent.translate(new Point3D(60, 0, 250));

        // Make the students not draggable
        redSelectableStudent.setDraggable(false);
        greenSelectableStudent.setDraggable(false);
        blueSelectableStudent.setDraggable(false);
        pinkSelectableStudent.setDraggable(false);
        yellowSelectableStudent.setDraggable(false);
    }

    /**
     * Method to stop the executions and the client thread. CALLED BY JAVAFX!
     */
    @Override
    public void stop()
    {
        // Stop the client
        try
        {
            client.stop();
        } catch (Exception e)
        {
            System.err.println("Unable to stop: " + e.getMessage());
        }

        // Terminate the application anyway
        System.exit(0);
    }

    /**
     * It starts the application
     */
    public static void main(String args[])
    {
        launch(args);
    }
}
