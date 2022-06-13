package it.polimi.ingsw.model;

import java.io.Serial;
import java.io.Serializable;
import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

/**
 * This class represents the game tower. Each player/team has 6 or 8 different colored towers in total and the amount of the "island positioned" ones
 * determines the winner of the match.
 */
public class Tower implements Serializable
{
    @Serial
    private static final long serialVersionUID = 2862369408462365038L;

    /**
     * Color if the pawn.
     */
    private TowerColor color;

    /**
     * Create a new Tower object.
     * 
     * @param color Color of the pawn.
     * @throws NullPointerException Thrown if the given color is null
     */
    public Tower(TowerColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[Tower] A null color was provided");

        this.color = color;
    }

    /**
     * Return the color of the tower.
     * 
     * @return Color of the tower.
     */
    public TowerColor getColor()
    {
        return color;
    }

    /**
     * Returns the tower representation in string format.
     * 
     * @return Tower's string representation.
     */
    @Override
    public String toString()
    {
        return PrintHelper.drawColor(color, GamePieces.TOWER.toString());
    }
}
