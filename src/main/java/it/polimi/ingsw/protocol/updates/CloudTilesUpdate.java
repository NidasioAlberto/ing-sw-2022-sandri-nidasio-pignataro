package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.CloudTile;

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
}
