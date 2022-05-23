package it.polimi.ingsw.model;

import java.io.Serial;
import java.io.Serializable;
import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

/**
 * This class represents the tower object. Each player/team has 6 or 8 different colored towers in total and the amount of the "island positioned"
 * ones determines the winner of the match.
 */
public class Tower implements Serializable
{
    @Serial
    private static final long serialVersionUID = 2862369408462365038L;

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

    @Override
    public String toString()
    {
        return PrintHelper.drawColor(color, GamePieces.TOWER.toString());
    }
}
