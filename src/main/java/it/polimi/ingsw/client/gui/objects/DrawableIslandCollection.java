package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.IslandType;
import it.polimi.ingsw.client.gui.objects.types.TowerType;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.transform.Rotate;

import java.util.Random;

public class DrawableIslandCollection extends DrawableObject
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
     * Specify the mean position of the collection
     */
    private Point3D position;

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
    public DrawableIslandCollection(int island_dimension, float x_multiply, float y_multiply, float radius, AnimationHandler updater)
    {
        super(updater);

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

        // Create the first position on 0, 0, 0
        position = new Point3D(0, 0, 0);

        for(int i = 0; i < islands.length; i++)
        {
            islands[i] = new DrawableIsland(ISLAND_DIMENSION, IslandType.values()[new Random().nextInt(IslandType.values().length)], updater);
            float angle = i * ((float)360.0 / NUMBER_OF_ISLANDS);
            float coordX = (float)Math.cos(Math.toRadians(angle)) * RADIUS * X_MULTIPLY;
            float coordY = (float)Math.sin(Math.toRadians(angle)) * RADIUS * Y_MULTIPLY;
            islands[i].translate(new Point3D(coordX, 0, coordY));
        }

        // At the end if the updater != null i add the box to it
        if(this.updater != null)
            this.updater.subscribeObject(this);
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
    public void subscribeToPointLight(PointLight light) {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableIslandCollection] Null ambient light");

        // Add the light to all the islands
        for(DrawableIsland island : islands)
            island.subscribeToAmbientLight(light);
    }

    @Override
    public void enableVisibility()
    {

    }

    @Override
    public void disableVisibility()
    {

    }

    /**
     * Position setters, needs to be synchronized to handle the scheduled task
     */
    @Override
    public synchronized void translate(Point3D point)
    {
        if(point == null)
            throw new NullPointerException("[DrawableIslandCollection] Null point");

        // Set the position
        position = point;

        // For every island i set the position according to the ellipsis and traslate it
        for(int i = 0; i < islands.length; i++)
        {
            islands[i] = new DrawableIsland(ISLAND_DIMENSION, IslandType.values()[new Random().nextInt(IslandType.values().length)], updater);
            float angle = i * ((float)360.0 / NUMBER_OF_ISLANDS);

            // Angle compensation for centering
            float delta = i == 0 || i == NUMBER_OF_ISLANDS / 2 ? 0 : (float)Math.abs(Math.cos(Math.toRadians(angle))) * 5;
            if((angle >= 0 && angle <= 90) || (angle >= 180 && angle <= 270))
                // I need to increase the angle
                angle += delta;
            else
                // I need to decrease the angle
                angle -= delta;

            // Set the actual coordinates
            float coordX = (float)Math.cos(Math.toRadians(angle)) * RADIUS * X_MULTIPLY;
            float coordZ = (float)Math.sin(Math.toRadians(angle)) * RADIUS * Y_MULTIPLY;
            islands[i].translate(new Point3D(coordX + point.getX(), point.getY(), coordZ + point.getZ()));
        }
    }

    // This method does nothing, i don't want the island collection to rotate
    @Override
    public void addRotation(Rotate rotation) {}

    @Override
    public Point3D getPosition() { return position; }
}