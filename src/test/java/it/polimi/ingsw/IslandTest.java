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

        // At the beginning there are no students
        assertEquals(island.getStudentsList().size(), 0);

        // Add a normal student
        island.addStudent(student);
        assertEquals(island.getStudentsList().get(0), student);
        assertEquals(island.getStudents()[0], student);
        assertEquals(island.getStudentsList().size(), 1);

        // Add the same studente as before
        island.addStudent(student);
        assertEquals(island.getStudentsList().size(), 1);
        assertEquals(island.getStudents().length, 1);

        // Add a null student
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
     * Test that a normal tower is added on the first free tile,
     * null or duplicates towers are not added,
     * if there are no free tiles the tower is not added
     */
    @Test
    public void addTowerTest()
    {
        //TODO problema se il colore delle torri è diverso
        island.mergeIsland(new Island());
        Tower tower = new Tower(TowerColor.BLACK);
        Tower tower1 = new Tower(TowerColor.BLACK);
        Tower tower2 = new Tower(TowerColor.BLACK);

        // At the beginning there is no tower on the islands
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(island.getTowers().size(), 0);

        // Add a tower
        island.addTower(tower);
        assertEquals(island.getIslands().get(0).getTower().get(), tower);
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().size(), 1);

        // Add a duplicate tower
        island.addTower(tower);
        assertEquals(island.getIslands().get(0).getTower().get(), tower);
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().size(), 1);

        // Add a null tower
        assertThrows(NullPointerException.class, () -> island.addTower(null));
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().size(), 1);

        // Add another tower
        island.addTower(tower1);
        assertEquals(island.getIslands().get(0).getTower().get(), tower);
        assertEquals(island.getIslands().get(1).getTower().get(), tower1);
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().get(1), tower1);
        assertEquals(island.getTowers().size(), 2);

        // Add another tower, but there are no free tiles so nothing canghes
        island.addTower(tower2);
        assertEquals(island.getIslands().get(0).getTower().get(), tower);
        assertEquals(island.getIslands().get(1).getTower().get(), tower1);
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().get(1), tower1);
        assertEquals(island.getTowers().size(), 2);
    }

    /**
     * Test that a contained tower is removed accurately,
     * if the tower to be removed is not contained, nothing changes and
     * if a null tower is passed a NullPointerException is thrown
     */
    @Test
    public void removeTowerTest()
    {
        island.mergeIsland(new Island());
        Tower tower = new Tower(TowerColor.BLACK);
        Tower tower1 = new Tower(TowerColor.BLACK);

        // Add two towers
        island.addTower(tower);
        island.addTower(tower1);
        assertEquals(island.getIslands().get(0).getTower().get(), tower);
        assertEquals(island.getIslands().get(1).getTower().get(), tower1);
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().get(1), tower1);
        assertEquals(island.getTowers().size(), 2);

        // Remove a contained tower
        island.removeTower(tower);
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertEquals(island.getIslands().get(1).getTower().get(), tower1);
        assertEquals(island.getTowers().get(0), tower1);
        assertEquals(island.getTowers().size(), 1);

        // Remove a not contained tower
        island.removeTower(tower);
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertEquals(island.getIslands().get(1).getTower().get(), tower1);
        assertEquals(island.getTowers().get(0), tower1);
        assertEquals(island.getTowers().size(), 1);

        // Remove a null tower
        assertThrows(NullPointerException.class, () -> island.addTower(null));
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertEquals(island.getIslands().get(1).getTower().get(), tower1);
        assertEquals(island.getTowers().get(0), tower1);
        assertEquals(island.getTowers().size(), 1);

        // Remove another contained tower
        island.removeTower(tower1);
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(island.getTowers().size(), 0);
    }

    /**
     * Test that when removeAllTowers() is called all the towers from the island are removed
     */
    @Test
    public void removeAllTowersTest()
    {
        island.mergeIsland(new Island());
        Tower tower = new Tower(TowerColor.BLACK);
        Tower tower1 = new Tower(TowerColor.BLACK);

        island.removeAllTowers();
        assertEquals(island.getTowers().size(), 0);

        // Add one tower and then remove
        island.addTower(tower);
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().size(), 1);
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        island.removeAllTowers();
        assertEquals(island.getTowers().size(), 0);
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());

        // Add two towers and then remove
        island.addTower(tower);
        island.addTower(tower1);
        assertEquals(island.getTowers().get(0), tower);
        assertEquals(island.getTowers().get(1), tower1);
        assertEquals(island.getTowers().size(), 2);
        island.removeAllTowers();
        assertEquals(island.getTowers().size(), 0);
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
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


}
