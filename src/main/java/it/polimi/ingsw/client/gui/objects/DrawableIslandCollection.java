package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.model.Island;
import javafx.scene.Group;
import javafx.scene.PointLight;

import java.util.Random;

public class DrawableIslandCollection implements DrawableObject
{
    public static final int NUMBER_OF_ISLANDS = 12;

    /**
     * Specify the single island dimension
     */
    private final int ISLAND_DIMENSION;

    /**
     * Specify the x multiply factor compared to a circle
     */
    private final float X_MULTIPLY;

    /**
     * Specify the y multiply factor compared to a circle
     */
    private final float Y_MULTIPLY;

    /**
     * Specify the base circle radius
     */
    private final float RADIUS;

    /**
     * Collection of islands
     */
    private DrawableIsland islands[];

    /**
     * Constructor
     * @param island_dimension The single island dimensions
     * @param x_multiply The parameter of radius that is multiplied in the x axis
     * @param y_multiply The parameter of radius that is multiplied in the y axis
     * @param radius The base circle diameter
     */
    public DrawableIslandCollection(int island_dimension, float x_multiply, float y_multiply, float radius)
    {
        if(island_dimension < 0)
            throw new IllegalArgumentException("[DrawableIslandCollection] Negative island dimension");
        if(x_multiply < 0 || y_multiply < 0)
            throw new IllegalArgumentException("[DrawableIslandCollection] Negative multiply factor");
        if(radius < 0)
            throw new IllegalArgumentException("[DrawableIslandCollection] Negative circle radius");

        // Constants assign
        ISLAND_DIMENSION = island_dimension;
        X_MULTIPLY = x_multiply;
        Y_MULTIPLY = y_multiply;
        RADIUS = radius;

        // Create the array of islands and position them in respect of all the parameters
        islands = new DrawableIsland[NUMBER_OF_ISLANDS];

        for(int i = 0; i < islands.length; i++)
        {
            islands[i] = new DrawableIsland(ISLAND_DIMENSION, IslandType.values()[new Random().nextInt(IslandType.values().length)]);
            float angle = i * ((float)360.0 / NUMBER_OF_ISLANDS);
            float coordX = (float)Math.cos(Math.toRadians(angle)) * RADIUS * X_MULTIPLY;
            float coordY = (float)Math.sin(Math.toRadians(angle)) * RADIUS * Y_MULTIPLY;
            islands[i].translate(coordX, 0, coordY);
        }
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableIslandCollection] Null group scene");

        // Add the box to the group
        for(DrawableIsland island : islands)
            island.addToGroup(group);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableIslandCollection] Null group scene");

        // Remove all the boxes
        for(DrawableIsland island : islands)
            island.removeFromGroup(group);
    }

    // Does nothing
    @Override
    public void subscribeToLight(PointLight light) {}

    @Override
    public void updateAnimation()
    {
        // Updates the animation of all the islands
        for(DrawableIsland island : islands)
            island.updateAnimation();
    }

    /**
     * Position setters, needs to be synchronized to handle the scheduled task
     */
    @Override
    public synchronized void translate(float x, float y, float z)
    {
        // For every island i set the position according to the ellipsis and traslate it
        for(int i = 0; i < islands.length; i++)
        {
            islands[i] = new DrawableIsland(ISLAND_DIMENSION, IslandType.values()[new Random().nextInt(IslandType.values().length)]);
            float angle = i * ((float)360.0 / NUMBER_OF_ISLANDS);
            float coordX = (float)Math.cos(Math.toRadians(angle)) * RADIUS * X_MULTIPLY + x;
            float coordZ = (float)Math.sin(Math.toRadians(angle)) * RADIUS * Y_MULTIPLY + z;
            islands[i].translate(coordX, y, coordZ);
        }
    }
}
