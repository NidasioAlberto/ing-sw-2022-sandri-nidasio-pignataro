package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.protocol.updates.PlayedAssistantCardUpdate;
import it.polimi.ingsw.protocol.updates.SchoolBoardUpdate;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.transform.Rotate;

public class DrawableSchoolBoardCollection extends DrawableCollection
{
    // Positioning constants
    public static final Point3D mainPosition = new Point3D(0, 0, -170);

    // Other players positions and rotations
    public static final Point3D[] otherPositions =
    {new Point3D(-400, 0, 150), new Point3D(400, 0, 150)};
    public static final Rotate[] otherRotations =
    {new Rotate(90, new Point3D(0, 1, 0)), new Rotate(-90, new Point3D(0, 1, 0))};
    public static final boolean[] assistantFlips =
    {true, false};

    /**
     * Schoolboard dimension
     */
    private final double DIMENSION;

    /**
     * Collection of school boards, maximum 3.
     */
    private DrawableSchoolBoard boards[];

    /**
     * Main player name
     */
    private String playerName;

    /**
     * Constructor
     * 
     * @param dimension The single schoolboard x dimension
     * @param playerName The main player name
     * @param pointLight The point light to which subscribe the objects
     * @param ambientLight The ambient light to which subscribe the objects
     * @param group The group to which add the objects
     * @param updater The updater for the animations
     */
    public DrawableSchoolBoardCollection(double dimension, String playerName, PointLight pointLight, AmbientLight ambientLight, Group group,
            AnimationHandler updater)
    {
        super(pointLight, ambientLight, group, updater);

        if (dimension <= 0)
            throw new IllegalArgumentException("[DrawableSchoolBoardCollection] Invalid dimension");
        if (playerName == null)
            throw new NullPointerException("[DrawableSchoolBoardCollection] Null player name");

        // Assign the parameters
        this.DIMENSION = dimension;
        this.playerName = playerName;

        // Create the array of school boards
        boards = new DrawableSchoolBoard[3];
    }

    /**
     * Update all the schoolboards positionings
     */
    public void updatePosition()
    {
        for (DrawableSchoolBoard board : boards)
        {
            if (board != null)
                board.updatePosition();
        }
    }

    /**
     * Method to display an update message
     * 
     * @param update The update to be rendered
     */
    public void displayUpdate(SchoolBoardUpdate update)
    {
        if (update == null)
            throw new NullPointerException("[DrawableSchoolBoardCollection] Null update");

        // Check if the corresponging player index is null, if so i have to create a schoolboard
        if (boards[update.getPlayerIndex()] == null)
        {
            // Create the new board
            DrawableSchoolBoard board = new DrawableSchoolBoard(DIMENSION, update.getPlayer(), updater);

            // Add the board to the group
            board.addToGroup(group);

            // Subscribe the board to the lightings
            board.subscribeToAmbientLight(ambientLight);
            board.subscribeToPointLight(pointLight);

            // Check if the board is this player board
            if (update.getPlayer().equals(playerName))
            {
                // Translate the board to the main player position
                board.translate(mainPosition.add(position));
            } else
            {
                // Check the board positioning and translate it
                int number = 0;
                for (int i = 0; i < boards.length; i++)
                {
                    if (boards[i] != null && !boards[i].getPlayerName().equals(playerName))
                    {
                        number++;
                    }
                }

                // Set the board to mouse transparent because it's not the main one
                board.disableVisibility();

                // Take the position based on the number of not main boards and translate/rotate the board
                board.translate(otherPositions[number].add(position));
                board.addRotation(otherRotations[number]);
                board.setAssistantFlip(assistantFlips[number]);
            }

            // Add the board to the array AFTER THE POSITION CHECK
            boards[update.getPlayerIndex()] = board;
        }

        // Update the corresponding schoolboard
        boards[update.getPlayerIndex()].update(update.getBoard(), group, pointLight);
    }

    /**
     * Method to display an assistant played update
     */
    public void displayAssistantUpdate(PlayedAssistantCardUpdate update)
    {
        if (update == null)
            throw new NullPointerException("[DrawableSchoolBoardCollection] Null update");

        // Find the schoolboard whith the corresponding user and update it
        for (DrawableSchoolBoard board : boards)
        {
            if (board != null && board.getPlayerName().equals(update.getPlayer()))
                board.updateAssitantCard(update.getCard(), group, ambientLight);
        }
    }

    @Override
    public void addToGroup()
    {
        // Add all the schoolboard to the group
        for (DrawableSchoolBoard board : boards)
            board.addToGroup(group);
    }

    @Override
    public void removeFromGroup()
    {
        // Remove all the schoolboards from the group
        for (DrawableSchoolBoard board : boards)
            board.removeFromGroup(group);
    }

    /**
     * IMPORTANT: TO BE USED BEFORE ANY MESSAGE ARRIVES
     */
    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableSchoolBoardCollection] Null point");

        // Set the position
        position = point;
    }

}
