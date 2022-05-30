package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.Visualizable;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.*;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class CloudTilesUpdate extends ModelUpdate
{
    @Serial
    private static final long serialVersionUID = -2463490370341477330L;

    /**
     * List of all the cloud tiles
     */
    private List<CloudTile> cloudTiles;

    /**
     * Constructor
     * 
     * @param cloudTiles Collection of all the cloud tiles
     */
    public CloudTilesUpdate(List<CloudTile> cloudTiles)
    {
        if (cloudTiles == null)
            throw new NullPointerException("[CloudTilesUpdate] Null cloud tiles list");
        // It is possible according to Java 8 documentation
        if (cloudTiles.contains(null))
            throw new NullPointerException("[CloudTilesUpdate] Null cloud tile inside the list");

        this.cloudTiles = cloudTiles;
    }

    public List<CloudTile> getCloudTiles()
    {
        return cloudTiles;
    }

    @Override
    public void handleUpdate(Visualizable handler)
    {
        handler.displayCloudTiles(this);
    }

    /**
     * Draws a 4x6 representation of each cloud tile with 1 column separation.
     */
    @Override
    public String toString()
    {
        String rep = "";

        // Draw cloud tiles
        for (CloudTile cloudTile : cloudTiles)
        {
            // Draw the cloud tile
            rep += cloudTile.toString();

            // Draw its number
            rep += PrintHelper.moveCursorRelative(3, -5);
            rep += cloudTiles.indexOf(cloudTile);
            rep += PrintHelper.moveCursorRelative(-3, 4);

            // Move cursor to draw the next cloud tile
            rep += PrintHelper.moveCursorRelative(3, 1);
        }

        return rep;
    }

    public static void main(String[] args)
    {
        List<CloudTile> cloudTiles = new ArrayList<>();

        CloudTile cloudTile1 = new CloudTile(CloudTileType.TILE_2);
        cloudTile1.addStudent(new Student(SchoolColor.PINK));
        cloudTile1.addStudent(new Student(SchoolColor.RED));
        cloudTile1.addStudent(new Student(SchoolColor.GREEN));

        CloudTile cloudTile2 = new CloudTile(CloudTileType.TILE_2);
        cloudTile2.addStudent(new Student(SchoolColor.YELLOW));
        cloudTile2.addStudent(new Student(SchoolColor.BLUE));
        cloudTile2.addStudent(new Student(SchoolColor.GREEN));

        cloudTiles.add(cloudTile1);
        cloudTiles.add(cloudTile2);

        CloudTilesUpdate update = new CloudTilesUpdate(cloudTiles);

        System.out.println(update);
    }
}
