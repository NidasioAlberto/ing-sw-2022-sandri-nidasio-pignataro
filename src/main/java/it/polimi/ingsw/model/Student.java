package it.polimi.ingsw.model;

import java.io.Serial;
import java.io.Serializable;
import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

/**
 * This class represents the student disc. Each student has its own color which is assigned once and
 * can't be modified. Students can be assigned to islands and boards and are one of the three main
 * elements of the game along with professors and towers.
 */
public class Student implements Serializable
{
    @Serial
    private static final long serialVersionUID = 6931487394744798538L;

    private SchoolColor color;

    /**
     * Constructor.
     * 
     * @param color The student's disc color.
     * @throws NullPointerException Thrown if the specified color is invalid.
     */
    public Student(SchoolColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[Student] A null color was provided");

        this.color = color;
    }

    public SchoolColor getColor()
    {
        return color;
    }

    @Override
    public String toString()
    {
        return PrintHelper.drawColor(color, GamePieces.STUDENT.toString());
    }
}
