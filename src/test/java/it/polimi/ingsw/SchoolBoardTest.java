package it.polimi.ingsw;

import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.TowerColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SchoolBoardTest
{
    SchoolBoard board;

    /**
     * Tests that if we put wrong parameters into the constructor, an error is thrown
     */
    @Test
    public void wrongParametersTest()
    {
        assertThrows(NullPointerException.class, () -> board = new SchoolBoard(7, null));
        assertThrows(IllegalArgumentException.class, () -> board = new SchoolBoard(2, TowerColor.BLACK));
    }

    /**
     * Tests if with correct parameters the general parameter assignment is correct.
     */
    @Test
    public void correctParametersTest()
    {
        board = new SchoolBoard(7, TowerColor.BLACK);

        //With 7 students we can have only 6 towers
        assertEquals(7, board.MAX_STUDENTS_ENTRANCE);
        assertEquals(6, board.MAX_TOWERS);

        board = new SchoolBoard(9, TowerColor.BLACK);

        //With 9 students we can have 8 towers
        assertEquals(9, board.MAX_STUDENTS_ENTRANCE);
        assertEquals(8, board.MAX_TOWERS);
    }

    @Test
    public void correctInitializationTest()
    {

    }
}
