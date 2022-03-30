package it.polimi.ingsw;

import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.SchoolColor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProfessorTest
{
    /**
     * For each color, create a professor for that color and check its color.
     */
    @Test
    public void checkProfessorColorTest()
    {
        for (SchoolColor color : SchoolColor.values())
        {
            Professor s = new Professor(color);
            assertEquals(color, s.getColor());
        }
    }

    /**
     * The professor constructor should throw NullPointerException if an invalid parameter is
     * passed.
     */
    @Test
    public void nullParameterTest()
    {
        assertThrows(NullPointerException.class, () -> new Professor(null));
    }
}
