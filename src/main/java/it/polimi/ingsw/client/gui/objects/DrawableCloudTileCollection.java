package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.CloudType;
import it.polimi.ingsw.client.gui.objects.types.StudentType;
import it.polimi.ingsw.model.CloudTile;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.protocol.updates.CloudTilesUpdate;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;

import java.util.ArrayList;
import java.util.List;

public class DrawableCloudTileCollection extends DrawableCollection
{
    /**
     * Dimension of a single tile
     */
    private final double DIMENSION;

    /**
     * All the cloud tiles
     */
    private List<DrawableCloudTile> tiles;

    /**
     * Constructor
     */
    public DrawableCloudTileCollection(double dimension, PointLight pointLight, AmbientLight ambientLight, Group group, AnimationHandler updater)
    {
        super(pointLight, ambientLight, group, updater);

        if (dimension < 0)
            throw new IllegalArgumentException("[DrawableCloudTileCollection] Invalid cloud tile dimension");

        // Assign all the variables
        this.DIMENSION = dimension;

        // Initialize the first position to 0
        position = new Point3D(0, 0, 0);

        // Initialize all the cloud tiles
        tiles = new ArrayList<>();
    }

    /**
     * Method to update the internal position of the cloud tiles
     */
    private void updatePosition()
    {
        for (int i = 0; i < tiles.size(); i++)
            tiles.get(i).translate(
                    new Point3D(position.getX() - DIMENSION * tiles.size() / 2 + DIMENSION * (i + 1.0 / 2.0), position.getY(), position.getZ()));
    }

    /**
     * Method to display the cloud tiles update
     * 
     * @param update Update message to visualize
     */
    public void displayUpdate(CloudTilesUpdate update)
    {
        // Case we have to create the cloud tiles
        if (tiles.size() == 0)
        {
            // Create all the cloud tiles instances
            for (CloudTile cloud : update.getCloudTiles())
            {
                if (cloud.getStudents().size() == 3)
                    tiles.add(new DrawableCloudTile(DIMENSION, CloudType.CLOUD_3, updater));
                else
                    tiles.add(new DrawableCloudTile(DIMENSION, CloudType.CLOUD_4, updater));
            }

            // subscribe all the tiles to the lights
            for (DrawableCloudTile tile : tiles)
            {
                tile.subscribeToAmbientLight(ambientLight);
                tile.subscribeToPointLight(pointLight);
            }

            // Add the cards to the group
            this.addToGroup();

            // Update their positionings
            updatePosition();
        }

        // Update the cloud tiles with the correct payloads
        for (int i = 0; i < update.getCloudTiles().size(); i++)
        {
            // First clear the tile
            tiles.get(i).clear(group, pointLight);

            // Then replicate all the students inside
            for (Student student : update.getCloudTiles().get(i).getStudents())
            {
                tiles.get(i).addStudent(StudentType.valueOf(student.getColor().name()), group, pointLight);
            }
        }
    }

    @Override
    public void addToGroup()
    {
        for (DrawableCloudTile tile : tiles)
            tile.addToGroup(group);
    }

    @Override
    public void removeFromGroup()
    {
        for (DrawableCloudTile tile : tiles)
            tile.removeFromGroup(group);
    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableCloudTileCollection] Null point");

        // Assign the new position
        this.position = point;

        // Translate all the cloud tiles
        updatePosition();
    }
}
