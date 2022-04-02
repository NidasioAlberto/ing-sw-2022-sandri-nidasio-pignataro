package it.polimi.ingsw;

import it.polimi.ingsw.model.Professor;
import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.TowerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SchoolBoardTest
{
    SchoolBoard board;

    @BeforeEach
    public void init() { board = new SchoolBoard(7, TowerColor.BLACK); }

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
        assertEquals(8, board.MAX_TOWERS);

        board = new SchoolBoard(9, TowerColor.BLACK);

        //With 9 students we can have 8 towers
        assertEquals(9, board.MAX_STUDENTS_ENTRANCE);
        assertEquals(6, board.MAX_TOWERS);
    }

    /**
     * Tests the correct initialization of all the lists
     */
    @Test
    public void correctInitializationTest()
    {
        assertEquals(0, board.getProfessors().size());
        assertEquals(0, board.getTowers().size());
        assertEquals(0, board.getStudentsInEntrance().size());

        for(int i = 0; i < SchoolColor.values().length; i++)
            assertEquals(0, board.getStudentsNumber(SchoolColor.values()[i]));
    }

    /**
     * Tests the addProfessor method
     */
    @Test
    public void addProfessorTest()
    {
        //Null professor
        assertThrows(NullPointerException.class, () -> board.addProfessor(null));
        assertEquals(0, board.getProfessors().size());

        //Create the professors to be added
        Professor firstProfessor = new Professor(SchoolColor.BLUE);
        Professor secondProfessor = new Professor(SchoolColor.RED);

        //Add legit professor
        board.addProfessor(firstProfessor);

        assertEquals(1, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(firstProfessor));

        //Add the same professor
        board.addProfessor(firstProfessor);

        assertEquals(1, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(firstProfessor));

        //Add the second professor
        board.addProfessor(secondProfessor);

        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(secondProfessor));
        assertEquals(true, board.getProfessors().contains(firstProfessor));
    }

    /**
     * Tests the remove professor method
     */
    @Test
    public void removeProfessorTest()
    {
        //Professors to be added and removed
        Professor firstProfessor = new Professor(SchoolColor.RED);
        Professor secondProfessor = new Professor(SchoolColor.BLUE);

        //Setup the test
        board.addProfessor(firstProfessor);
        board.addProfessor(secondProfessor);

        //Null remove
        assertThrows(NullPointerException.class, () -> board.removeProfessor(null));
        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(firstProfessor));
        assertEquals(true, board.getProfessors().contains(secondProfessor));

        //Remove a different instance
        board.removeProfessor(new Professor(SchoolColor.RED));
        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(firstProfessor));
        assertEquals(true, board.getProfessors().contains(secondProfessor));

        //Remove the actual first professor
        board.removeProfessor(firstProfessor);
        assertEquals(1, board.getProfessors().size());
        assertEquals(true, board.getProfessors().contains(secondProfessor));

        //Remove the actual second professor
        board.removeProfessor(secondProfessor);
        assertEquals(0, board.getProfessors().size());
    }

    /**
     * Tests the hasProfessor method
     */
    @Test
    public void hasProfessorTest()
    {
        //Professors to be added and removed
        Professor firstProfessor = new Professor(SchoolColor.RED);
        Professor secondProfessor = new Professor(SchoolColor.BLUE);

        assertEquals(false, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(false, board.hasProfessor(secondProfessor.getColor()));

        //Setup the test
        board.addProfessor(firstProfessor);
        board.addProfessor(secondProfessor);

        //Null remove
        assertThrows(NullPointerException.class, () -> board.removeProfessor(null));
        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(true, board.hasProfessor(secondProfessor.getColor()));

        //Remove a different instance
        board.removeProfessor(new Professor(SchoolColor.RED));
        assertEquals(2, board.getProfessors().size());
        assertEquals(true, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(true, board.hasProfessor(secondProfessor.getColor()));

        //Remove the actual first professor
        board.removeProfessor(firstProfessor);
        assertEquals(1, board.getProfessors().size());
        assertEquals(false, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(true, board.hasProfessor(secondProfessor.getColor()));

        //Remove the actual second professor
        board.removeProfessor(secondProfessor);
        assertEquals(0, board.getProfessors().size());
        assertEquals(false, board.hasProfessor(firstProfessor.getColor()));
        assertEquals(false, board.hasProfessor(secondProfessor.getColor()));
    }
}
