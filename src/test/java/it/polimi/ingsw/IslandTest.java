package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Class to test the Island class
 */
public class IslandTest
{
    Island island;

    @BeforeEach
    public void init() { island = new Island(); }

    /**
     * Test that a normal student is added,
     * null or duplicates are not added
     */
    @Test
    public void addStudentTest()
    {
        Student student = new Student(SchoolColor.BLUE);

        //add a normal student
        island.addStudent(student);
        assertEquals(island.getStudentsList().get(0), student);
        assertEquals(island.getStudents()[0], student);

        //add the same studente as before
        island.addStudent(student);
        assertEquals(island.getStudentsList().size(), 1);
        assertEquals(island.getStudents().length, 1);

        //add a null student
        island.addStudent(null);
        assertEquals(island.getStudentsList().size(), 1);
        assertEquals(island.getStudents().length, 1);
    }

    /**
     * Test that if the island is already contained or is null is not added
     * Test that an island is added accurately
     */
    @Test
    public void mergeIslandTest()
    {
        assertEquals(island.getIslands().size(), 1);

        island.mergeIsland(island);
        assertEquals(island.getIslands().size(), 1);

        island.mergeIsland(null);
        assertEquals(island.getIslands().size(), 1);

        Island island1 = new Island();
        island.mergeIsland(island1);
        assertEquals(island.getIslands().size(), 2);
        assertEquals(island.getIslands().get(1), island1.getIslands().get(0));
    }

    /**
     * Test the removeTower() method
     * TODO not finished
     */
    @Test
    public void removeTowerTest()
    {
        Tower tower = new Tower(TowerColor.BLACK);

        island.addTower(tower);
        assertEquals(island.getTowers()[0], tower);

        island.removeTower(tower);
        assertEquals(island.getTowers().length, 0);
    }
}
