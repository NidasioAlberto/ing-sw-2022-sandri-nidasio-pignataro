package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.Island;

import java.util.List;

public class IslandsUpdate extends ModelUpdate
{
    /**
     * The whole island list of objects
     */
    private List<Island> islands;

    /**
     * The current mother nature position
     */
    private int motherNatureIndex;

    /**
     * Constructor
     * @param islands Collection of all the islands
     * @param motherNatureIndex Index of mother nature position inside the collection
     */
    public IslandsUpdate(List<Island> islands, int motherNatureIndex)
    {
        if(islands == null)
            throw new NullPointerException("[IslandsUpdate] Null island list");
        // It is possible according to java 8 documentation
        if(islands.contains(null))
            throw new NullPointerException("[IslandsUpdate] Null island inside the list");
        if(motherNatureIndex < 0 || motherNatureIndex >= islands.size())
            throw new IndexOutOfBoundsException("[IslandsUpdate] Mother nature index out of bounds");

        this.islands            = islands;
        this.motherNatureIndex  = motherNatureIndex;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }
}
