package it.polimi.ingsw.model;

import java.io.Serial;
import java.io.Serializable;
import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

/**
 * This class represents the student pawn. Each student has its own color which is assigned once and can't be modified. Students can be assigned to
 * islands and boards and are one of the three main elements of the game along with professors and towers.
 */
public class Student implements Serializable
{
    @Serial
    private static final long serialVersionUID = 6931487394744798538L;

    /**
     * Color of the student.
     */
    private SchoolColor color;

    /**
     * Creates a Student object.
     * 
     * @param color Color of the pawn.
     * @throws NullPointerException
     */
    public Student(SchoolColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[Student] A null color was provided");

        this.color = color;
    }

    /**
     * Returns the color of the student.
     * 
     * @return Color of the student.
     */
    public SchoolColor getColor()
    {
        return color;
    }

    /**
     * Student representation in string format.
     * 
     * @return String representation of the student.
     */
    @Override
    public String toString()
    {
        return PrintHelper.drawColor(color, GamePieces.STUDENT.toString());
    }
}
