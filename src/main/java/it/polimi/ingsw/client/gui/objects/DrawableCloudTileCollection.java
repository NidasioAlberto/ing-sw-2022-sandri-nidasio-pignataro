package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.transform.Rotate;

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
     * Current collection position
     */
    private Point3D position;

    /**
     * Constructor
     */
    protected DrawableCloudTileCollection(double dimension, PointLight pointLight, AmbientLight ambientLight, Group group, AnimationHandler updater)
    {
        super(pointLight, ambientLight, group, updater);

        if(dimension < 0)
            throw new IllegalArgumentException("[DrawableCloudTileCollection] Invalid cloud tile dimension");

        // Assign all the variables
        this.DIMENSION = dimension;

        // Initialize the first position to 0
        position = new Point3D(0, 0,0 );

        // Initialize all the cloud tiles
        tiles = new ArrayList<>();
    }

    @Override
    public void addToGroup()
    {
        for(DrawableCloudTile tile : tiles)
            tile.addToGroup(group);
    }

    @Override
    public void removeFromGroup()
    {
        for(DrawableCloudTile tile : tiles)
            tile.removeFromGroup(group);
    }

    @Override
    public void translate(Point3D point)
    {
        if(point == null)
            throw new NullPointerException("[DrawableCloudTileCollection] Null point");

        // Assign the new position
        this.position = point;

        // Translate all the cloud tiles
        for(int i = 0; i < tiles.size(); i++)
        {
            // I need to translate the tile of the right amount
            double x = point.getX() - DIMENSION * tiles.size() / 2 + i * DIMENSION;
            tiles.get(i).translate(new Point3D(x + point.getX(), point.getY(), point.getZ()));
        }
    }
}
