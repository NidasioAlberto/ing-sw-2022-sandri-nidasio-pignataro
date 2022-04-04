package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IslandTileTest
{
    IslandTile island;

    @BeforeEach
    public void init()
    {
        island = new IslandTile();
    }

    /**
     * Test that a normal student is added instead if the student is already contained or it is null
     * nothing changes.
     */
    @Test
    public void addStudentTest()
    {
        Student student = new Student(SchoolColor.GREEN);
        Student student2 = new Student(SchoolColor.BLUE);

        // At the beginning there are no students on the island
        assertEquals(0, island.getStudents().size());

        // Add a normal student
        island.addStudent(student);
        assertEquals(student, island.getStudents().get(0));

        // Add a null student
        assertThrows(NullPointerException.class, () -> island.addStudent(null));
        assertEquals(student, island.getStudents().get(0));

        // Add a duplicate student
        island.addStudent(student);
        assertEquals(student, island.getStudents().get(0));
        assertEquals(1, island.getStudents().size());

        // Add a second normal student
        island.addStudent(student2);
        assertEquals(student, island.getStudents().get(0));
        assertEquals(student2, island.getStudents().get(1));
        assertEquals(2, island.getStudents().size());
    }

    /**
     * Test that a tower is added only if it is not null and there is not another tower.
     */
    @Test
    public void addTowerTest()
    {
        Tower tower = new Tower(TowerColor.WHITE);

        // At the beginning there is no tower on the island
        assertTrue(island.getTower().isEmpty());

        // Add a null tower
        assertThrows(NullPointerException.class, () -> island.addTower(null));
        assertTrue(island.getTower().isEmpty());

        // Add a normal tower
        island.addTower(tower);
        assertEquals(tower, island.getTower().get());

        // Add another tower
        island.addTower(new Tower(TowerColor.BLACK));
        assertEquals(tower, island.getTower().get());

        // Add a duplicate tower
        island.addTower(tower);
        assertEquals(tower, island.getTower().get());
    }

    /**
     * Test that after removeTower() is called there is no tower.
     */
    @Test
    public void removeTowerTest()
    {
        assertTrue(island.getTower().isEmpty());
        island.removeTower();
        assertTrue(island.getTower().isEmpty());

        island.addTower(new Tower(TowerColor.WHITE));
        island.removeTower();
        assertTrue(island.getTower().isEmpty());
    }

    /**
     * Test that removeTower(Tower tower) removes the tower on the island only if is equal to the
     * parameter.
     */
    @Test
    public void removeTowerTest2()
    {
        Tower tower = new Tower(TowerColor.GREY);

        assertTrue(island.getTower().isEmpty());
        island.removeTower(null);
        assertTrue(island.getTower().isEmpty());

        island.removeTower(tower);
        assertTrue(island.getTower().isEmpty());

        island.addTower(tower);

        island.removeTower(null);
        assertEquals(tower, island.getTower().get());

        island.removeTower(new Tower(TowerColor.GREY));
        assertEquals(tower, island.getTower().get());

        island.removeTower(tower);
        assertTrue(island.getTower().isEmpty());
    }
}
