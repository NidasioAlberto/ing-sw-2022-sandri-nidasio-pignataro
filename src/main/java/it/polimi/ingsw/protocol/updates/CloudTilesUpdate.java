package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

public class CloudTilesUpdate extends ModelUpdate
{
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
    public void handleUpdate(Object handler)
    {

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
            rep += cloudTile.toString();
            rep += PrintHelper.moveCursorRelative(4, 1);
        }

        return rep;
    }

    public static void main(String[] args)
    {
        List <CloudTile> cloudTiles = new ArrayList<>();

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
