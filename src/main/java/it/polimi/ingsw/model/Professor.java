package it.polimi.ingsw.model;

import java.io.Serial;
import java.io.Serializable;
import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

/**
 * This class represents the professor pawn. Each colored professor is contended between the players inside their school boards. It is one of the
 * three main elements of the game along with students and towers.
 */
public class Professor implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1655900624489466140L;

    private SchoolColor color;

    /**
     * Creates a professor object
     * 
     * @param color The pawn color.
     * @throws NullPointerException Thrown if the specified color is invalid.
     */
    public Professor(SchoolColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[Professor] A null color was provided");

        this.color = color;
    }

    /**
     * Returns the professor's color.
     * 
     * @return Professor's color.
     */
    public SchoolColor getColor()
    {
        return color;
    }

    /**
     * Returns a representation of the professor as a colored character.
     * 
     * @return The professor's string representation.
     */
    @Override
    public String toString()
    {
        return PrintHelper.drawColor(color, GamePieces.PROFESSOR.toString());
    }
}
