package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class to test the Island class
 */
public class IslandTest
{
    Island island;

    @BeforeEach
    public void init()
    {
        island = new Island();
    }

    /**
     * Test that a normal student is added, null or duplicates are not added
     */
    @Test
    public void addStudentTest()
    {
        Student student = new Student(SchoolColor.BLUE);

        //At the beginning there are no students
        assertEquals(island.getStudentsList().size(), 0);

        //Add a normal student
        island.addStudent(student);
        assertEquals(island.getStudentsList().get(0), student);
        assertEquals(island.getStudents()[0], student);
        assertEquals(island.getStudentsList().size(), 1);

        // add the same studente as before
        island.addStudent(student);
        assertEquals(island.getStudentsList().size(), 1);
        assertEquals(island.getStudents().length, 1);

        //Add a null student
        assertThrows(NullPointerException.class, () -> island.addStudent(null));
        assertEquals(island.getStudentsList().get(0), student);
        assertEquals(island.getStudentsList().size(), 1);
        assertEquals(island.getStudents().length, 1);
    }

    /**
     * Test that students are added on the tile with fewer students
     */
    @Test
    public void addStudentOnCorrectIslandTest()
    {

    }

    /**
     *
     */
    @Test
    public void addTowerTest()
    {
        //TODO problema se il colore delle torri è diverso
        island.mergeIsland(new Island());
        Tower tower = new Tower(TowerColor.BLACK);
        Tower tower1 = new Tower(TowerColor.BLACK);
        Tower tower2 = new Tower(TowerColor.BLACK);

        //At the beginning there is no tower on the islands
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());

        //Add a tower
        island.addTower(tower);
        assertEquals(island.getIslands().get(0).getTower().get(), tower);
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().size(), 1);

        //Add a duplicate tower
        island.addTower(tower);
        assertEquals(island.getIslands().get(0).getTower().get(), tower);
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().size(), 1);

        //Add a null tower
        assertThrows(NullPointerException.class, () -> island.addTower(null));
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().size(), 1);

        //Add another tower
        island.addTower(tower1);
        assertEquals(island.getIslands().get(0).getTower().get(), tower);
        assertEquals(island.getIslands().get(1).getTower().get(), tower1);
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().get(1), tower1);
        assertEquals(island.getTowers().size(), 2);

        //Add another tower, but there are no free tiles
        island.addTower(tower1);
        assertEquals(island.getIslands().get(0).getTower().get(), tower);
        assertEquals(island.getIslands().get(1).getTower().get(), tower1);
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().get(1), tower1);
        assertEquals(island.getTowers().size(), 2);
    }


    /**
     * Test that if the island is already contained or is null is not added Test that an island is
     * added accurately
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
     * Test the removeTower() method TODO not finished
     */
    @Test
    public void removeTowerTest()
    {
        Tower tower = new Tower(TowerColor.BLACK);

        island.addTower(tower);
        assertEquals(island.getTowers().get(0), tower);

        island.removeTower(tower);
        assertEquals(island.getTowers().size(), 0);
    }
}
