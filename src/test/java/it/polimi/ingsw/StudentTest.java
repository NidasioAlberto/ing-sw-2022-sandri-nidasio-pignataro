package it.polimi.ingsw;

import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.SchoolColor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StudentTest
{
    /**
     * For each color, create a student for that color and check its color.
     */
    @Test
    public void checkStudentColorTest()
    {
        for (SchoolColor color : SchoolColor.values())
        {
            Student s = new Student(color);
            assertEquals(s.getColor(), color);
        }
    }

    /**
     * The student constructor should throw NullPointerException if an invalid parameter is passed.
     */
    @Test
    public void nullParameterTest()
    {
        assertThrows(NullPointerException.class, () -> new Student(null));
    }
}
