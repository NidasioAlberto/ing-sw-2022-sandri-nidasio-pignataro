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

        island.addStudent(student);
        assertEquals(island.getStudentsList().get(0), student);
        assertEquals(island.getStudents()[0], student);

        assertThrows(NullPointerException.class, () -> island.addStudent(null));
        assertEquals(island.getStudentsList().size(), 1);

        island.addStudent(student);
        assertEquals(island.getStudentsList().size(), 1);
    }

    /**
     * Test that a tower is added only if it is not null and there is not another tower.
     */
    @Test
    public void addTowerTest()
    {
        Tower tower = new Tower(TowerColor.WHITE);

        assertThrows(NullPointerException.class, () -> island.addTower(null));
        assertTrue(island.getTower().isEmpty());

        island.addTower(tower);
        assertEquals(island.getTower().get(), tower);

        island.addTower(new Tower(TowerColor.BLACK));
        assertEquals(island.getTower().get(), tower);
    }

    /**
     * Test that after removeTower() is called there is no tower.
     */
    @Test
    public void removeTowerTest()
    {
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

        island.removeTower(null);
        assertTrue(island.getTower().isEmpty());

        island.removeTower(tower);
        assertTrue(island.getTower().isEmpty());

        island.addTower(tower);

        island.removeTower(null);
        assertEquals(island.getTower().get(), tower);

        island.removeTower(new Tower(TowerColor.WHITE));
        assertEquals(island.getTower().get(), tower);

        island.removeTower(tower);
        assertTrue(island.getTower().isEmpty());
    }
}
