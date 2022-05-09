package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * This class represents the tower object. Each player/team has 6 or 8 different colored towers in
 * total and the amount of the "island positioned" ones determines the winner of the match.
 */
public class Tower implements Serializable
{
    private TowerColor color;

    /**
     * Constructor.
     * 
     * @param color The tower color.
     * @throws NullPointerException Thrown if the specified color is invalid.
     */
    public Tower(TowerColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[Tower] A null color was provided");

        this.color = color;
    }

    public TowerColor getColor()
    {
        return color;
    }
}
