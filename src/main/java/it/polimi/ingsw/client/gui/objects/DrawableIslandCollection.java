package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.IslandType;
import it.polimi.ingsw.protocol.updates.IslandsUpdate;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawableIslandCollection extends DrawableCollection
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
    private List<DrawableIsland> islands;

    /**
     * Mother nature
     */
    private DrawableMotherNature motherNature;

    /**
     * The index that the islands update
     */
    private DrawableIndex index;

    /**
     * Constructor
     * 
     * @param island_dimension The single island dimensions
     * @param x_multiply The parameter of radius that is multiplied in the x axis
     * @param y_multiply The parameter of radius that is multiplied in the y axis
     * @param radius The base circle diameter
     * @param index The index that the islands update with their content
     */
    public DrawableIslandCollection(int island_dimension, float x_multiply, float y_multiply, float radius, DrawableIndex index,
            PointLight pointLight, AmbientLight ambientLight, Group group, AnimationHandler updater)
    {
        super(pointLight, ambientLight, group, updater);

        if (island_dimension < 0)
            throw new IllegalArgumentException("[DrawableIslandCollection] Negative island dimension");
        if (x_multiply < 0 || y_multiply < 0)
            throw new IllegalArgumentException("[DrawableIslandCollection] Negative multiply factor");
        if (radius < 0)
            throw new IllegalArgumentException("[DrawableIslandCollection] Negative circle radius");
        if (index == null)
            throw new NullPointerException("[DrawableIslandCollection] Null index");

        // Constants assign
        ISLAND_DIMENSION = island_dimension;
        X_MULTIPLY = x_multiply;
        Y_MULTIPLY = y_multiply;
        RADIUS = radius;
        this.index = index;

        // Create the array of islands and position them in respect of all the parameters
        islands = new ArrayList<>();

        // Create mother nature and subscribe it to lights
        motherNature = new DrawableMotherNature(3, 7.5f, 1.5f, updater);
        motherNature.subscribeToAmbientLight(ambientLight);
        motherNature.subscribeToPointLight(pointLight);
    }

    /**
     * Method to update the islands positions inside the ellipsis
     */
    private void updatePosition()
    {
        for (int i = 0; i < islands.size(); i++)
        {
            float angle = i * ((float) 360.0 / islands.size());

            // Angle compensation for centering
            float delta = i == 0 || i == islands.size() / 2 ? 0 : (float) Math.abs(Math.cos(Math.toRadians(angle))) * 5;
            if ((angle >= 0 && angle <= 90) || (angle >= 180 && angle <= 270))
                // I need to increase the angle
                angle += delta;
            else
                // I need to decrease the angle
                angle -= delta;

            // Set the actual coordinates
            float coordX = (float) Math.cos(Math.toRadians(-angle)) * RADIUS * X_MULTIPLY;
            float coordZ = (float) Math.sin(Math.toRadians(-angle)) * RADIUS * Y_MULTIPLY;
            islands.get(i).translate(new Point3D(coordX + position.getX(), position.getY(), coordZ + position.getZ()));
        }
    }

    /**
     * Method to display the islands update
     * 
     * @param update Update message to visualize
     */
    public void displayUpdate(IslandsUpdate update)
    {
        // Case we have to create the islands
        if (islands.size() == 0)
        {
            // Create all the islands instances
            for (int i = 0; i < update.getIslands().size(); i++)
                islands.add(
                        new DrawableIsland(ISLAND_DIMENSION, IslandType.values()[new Random().nextInt(IslandType.values().length)], index, updater));

            // Subscribe all the islands to the lights
            for (DrawableIsland island : islands)
            {
                island.subscribeToAmbientLight(ambientLight);
                island.subscribeToPointLight(pointLight);
            }

            // Add the islands to the group
            this.addToGroup();

            // Update their positions
            updatePosition();
        }

        // Update every island, keeping in mind that the islands can only be removed
        for (int i = 0; i < update.getIslands().size(); i++)
        {
            islands.get(i).update(update.getIslands().get(i), group, pointLight);

            // Set the island number also
            islands.get(i).setNumber(i);
        }

        // Calculate before the number of islands to be removed
        int removed = islands.size() - update.getIslands().size();

        // Delete the ones in excess (always the last ones for n times like the n islands to remove)
        // this is done to fix a minor visualization bug
        for (int i = 0; i < removed; i++)
        {
            // Clear the island and delete it
            islands.get(islands.size() - 1).clear(pointLight, group);
            islands.get(islands.size() - 1).removeFromGroup(group);
            islands.get(islands.size() - 1).unsubscribeFromAmbientLight(ambientLight);

            // Unsubscribe from updater
            updater.unsubscribeObject(islands.get(islands.size() - 1));
            islands.remove(islands.size() - 1);
        }

        // I have to update the positions
        updatePosition();

        // Set mother nature correctly removing it from every island and then assigning it to the correct one
        for (DrawableIsland island : islands)
            island.removeMotherNature();

        islands.get(update.getMotherNatureIndex()).addMotherNature(motherNature);
    }

    @Override
    public void clearAll()
    {
        for (DrawableIsland island : islands)
        {
            // Clear first the island payload
            island.clear(pointLight, group);
            island.removeFromGroup(group);
            island.unsubscribeFromAmbientLight(ambientLight);
            island.unsubscribeFromPointLight(pointLight);
            updater.unsubscribeObject(island);
        }

        // Clear also mother nature
        // motherNature.removeFromGroup(group);
        // motherNature.unsubscribeFromAmbientLight(ambientLight);
        // motherNature.unsubscribeFromPointLight(pointLight);
        // updater.unsubscribeObject(motherNature);
        // motherNature = null;

        // Clear the array
        islands.clear();
    }

    @Override
    public void addToGroup()
    {
        // Add the box to the group
        for (DrawableIsland island : islands)
            island.addToGroup(group);

        // Add mother nature ignoring exceptions because it could already been added due to previous matches
        try
        {
            motherNature.addToGroup(group);
        } catch (Exception e)
        {
        }
    }

    @Override
    public void removeFromGroup()
    {
        // Remove all the boxes
        for (DrawableIsland island : islands)
            island.removeFromGroup(group);

        // Remove mother nature
        motherNature.removeFromGroup(group);
    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableIslandCollection] Null point");

        // Set the position
        position = point;

        // For every island i set the position according to the ellipsis and traslate it
        updatePosition();
    }
}
