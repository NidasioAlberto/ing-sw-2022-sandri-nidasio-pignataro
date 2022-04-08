package it.polimi.ingsw.model;

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
        assertEquals(0, island.getStudents().size());

        // Add a normal student
        island.addStudent(student);
        assertEquals(student, island.getStudents().get(0));
        assertEquals(1, island.getStudents().size());

        // Add the same studente as before
        island.addStudent(student);
        assertEquals(1, island.getStudents().size());

        // Add a null student
        assertThrows(NullPointerException.class, () -> island.addStudent(null));
        assertEquals(student, island.getStudents().get(0));
        assertEquals(1, island.getStudents().size());
    }

    /**
     * Test that students are added on the tile with fewer students
     */
    @Test
    public void addStudentOnCorrectIslandTest()
    {
        island.addTower(new Tower(TowerColor.WHITE));
        Island island2 = new Island();
        island2.addTower(new Tower(TowerColor.WHITE));
        island.mergeIsland(island2);
        Student student = new Student(SchoolColor.BLUE);
        Student student1 = new Student(SchoolColor.GREEN);
        Student student2 = new Student(SchoolColor.PINK);

        // At the beginning there are no students on the island
        assertEquals(0, island.getStudents().size());

        // The first student is added on the first islandTile
        island.addStudent(student);
        assertEquals(1, island.getStudents().size());
        assertEquals(1, island.getIslands().get(0).getStudents().size());
        assertEquals(student, island.getIslands().get(0).getStudents().get(0));
        assertEquals(0, island.getIslands().get(1).getStudents().size());

        // The second student is added on the second islandTile
        island.addStudent(student1);
        assertEquals(2, island.getStudents().size());
        assertEquals(1, island.getIslands().get(0).getStudents().size());
        assertEquals(student, island.getIslands().get(0).getStudents().get(0));
        assertEquals(1, island.getIslands().get(1).getStudents().size());
        assertEquals(student1, island.getIslands().get(1).getStudents().get(0));

        // The third student is added on the first islandTile
        island.addStudent(student2);
        assertEquals(3, island.getStudents().size());
        assertEquals(2, island.getIslands().get(0).getStudents().size());
        assertEquals(student, island.getIslands().get(0).getStudents().get(0));
        assertEquals(student2, island.getIslands().get(0).getStudents().get(1));
        assertEquals(1, island.getIslands().get(1).getStudents().size());
        assertEquals(student1, island.getIslands().get(1).getStudents().get(0));
    }

    /**
     * Test that a normal tower is added on the first free tile, null or duplicates towers are not
     * added, if there are no free tiles the tower is not added
     */
    @Test
    public void addTowerTest()
    {
        island.addTower(new Tower(TowerColor.WHITE));
        Island island2 = new Island();
        island2.addTower(new Tower(TowerColor.WHITE));
        island.mergeIsland(island2);
        island.removeAllTowers();
        Tower tower = new Tower(TowerColor.BLACK);
        Tower tower1 = new Tower(TowerColor.BLACK);
        Tower tower2 = new Tower(TowerColor.BLACK);

        // At the beginning there is no tower on the islands
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(0, island.getTowers().size());

        // Add a tower
        island.addTower(tower);
        assertEquals(tower, island.getIslands().get(0).getTower().get());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(1, island.getTowers().size());

        // Add a duplicate tower
        island.addTower(tower);
        assertEquals(tower, island.getIslands().get(0).getTower().get());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(1, island.getTowers().size());

        // Add a null tower
        assertThrows(NullPointerException.class, () -> island.addTower(null));
        assertEquals(tower, island.getIslands().get(0).getTower().get());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(1, island.getTowers().size());

        // Add a tower of different color from the one already present
        assertThrows(IllegalArgumentException.class,
                () -> island.addTower(new Tower(TowerColor.WHITE)));
        assertEquals(tower, island.getIslands().get(0).getTower().get());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(1, island.getTowers().size());

        // Add another tower
        island.addTower(tower1);
        assertEquals(tower, island.getIslands().get(0).getTower().get());
        assertEquals(tower1, island.getIslands().get(1).getTower().get());
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(tower1, island.getTowers().get(1));
        assertEquals(2, island.getTowers().size());

        // Add another tower, but there are no free tiles so nothing changes
        island.addTower(tower2);
        assertEquals(tower, island.getIslands().get(0).getTower().get());
        assertEquals(tower1, island.getIslands().get(1).getTower().get());
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(tower1, island.getTowers().get(1));
        assertEquals(2, island.getTowers().size());
    }

    /**
     * Test that a contained tower is removed accurately, if the tower to be removed is not
     * contained, nothing changes and if a null tower is passed a NullPointerException is thrown
     */
    @Test
    public void removeTowerTest()
    {
        Tower tower = new Tower(TowerColor.BLACK);
        Tower tower1 = new Tower(TowerColor.BLACK);
        island.addTower(tower);
        Island island2 = new Island();
        island2.addTower(tower1);
        island.mergeIsland(island2);

        // At the beginning there are two towers
        assertEquals(tower, island.getIslands().get(0).getTower().get());
        assertEquals(tower1, island.getIslands().get(1).getTower().get());
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(tower1, island.getTowers().get(1));
        assertEquals(2, island.getTowers().size());

        // Remove a contained tower
        island.removeTower(tower);
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertEquals(tower1, island.getIslands().get(1).getTower().get());
        assertEquals(tower1, island.getTowers().get(0));
        assertEquals(1, island.getTowers().size());

        // Remove a not contained tower
        island.removeTower(tower);
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertEquals(tower1, island.getIslands().get(1).getTower().get());
        assertEquals(tower1, island.getTowers().get(0));
        assertEquals(1, island.getTowers().size());

        // Remove a null tower
        assertThrows(NullPointerException.class, () -> island.addTower(null));
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertEquals(tower1, island.getIslands().get(1).getTower().get());
        assertEquals(tower1, island.getTowers().get(0));
        assertEquals(1, island.getTowers().size());

        // Remove another contained tower
        island.removeTower(tower1);
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        assertEquals(0, island.getTowers().size());
    }

    /**
     * Test that when removeAllTowers() is called all the towers from the island are removed
     */
    @Test
    public void removeAllTowersTest()
    {
        island.addTower(new Tower(TowerColor.WHITE));
        Island island2 = new Island();
        island2.addTower(new Tower(TowerColor.WHITE));
        island.mergeIsland(island2);
        Tower tower = new Tower(TowerColor.BLACK);
        Tower tower1 = new Tower(TowerColor.BLACK);

        island.removeAllTowers();
        assertEquals(0, island.getTowers().size());

        // Add one tower and then remove
        island.addTower(tower);
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(1, island.getTowers().size());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());
        island.removeAllTowers();
        assertEquals(0, island.getTowers().size());
        assertTrue(island.getIslands().get(0).getTower().isEmpty());
        assertTrue(island.getIslands().get(1).getTower().isEmpty());

        // Add two towers and then remove
        island.addTower(tower);
        island.addTower(tower1);
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(tower1, island.getTowers().get(1));
        assertEquals(2, island.getTowers().size());
        island.removeAllTowers();
        assertEquals(0, island.getTowers().size());
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
        island.addTower(new Tower(TowerColor.WHITE));

        // At the beginning there is only one island
        assertEquals(1, island.getIslands().size());

        // Merge island with an island already contained
        island.mergeIsland(island);
        assertEquals(1, island.getIslands().size());

        // Merge with a null island
        assertThrows(NullPointerException.class, () -> island.mergeIsland(null));
        assertEquals(1, island.getIslands().size());

        // Merge with another normal island
        Island island1 = new Island();
        island1.addTower(new Tower(TowerColor.WHITE));
        island.mergeIsland(island1);
        assertEquals(2, island.getIslands().size());
        assertEquals(island1.getIslands().get(0), island.getIslands().get(1));

        // Merge with another island that contains two islandTiles
        Island island2 = new Island();
        island2.addTower(new Tower(TowerColor.WHITE));
        Island island3 = new Island();
        island3.addTower(new Tower(TowerColor.WHITE));
        island2.mergeIsland(island3);
        island.mergeIsland(island2);
        assertEquals(4, island.getIslands().size());
        assertEquals(island2.getIslands().get(0), island.getIslands().get(2));
        assertEquals(island2.getIslands().get(1), island.getIslands().get(3));

        // Merge with another island that contains islandTiles already contained in island
        Island island4 = new Island();
        island4.addTower(new Tower(TowerColor.WHITE));
        island4.mergeIsland(island2);
        assertEquals(3, island4.getIslands().size());
        island.mergeIsland(island4);
        assertEquals(5, island.getIslands().size());
        assertEquals(island2.getIslands().get(0), island.getIslands().get(2));
        assertEquals(island2.getIslands().get(1), island.getIslands().get(3));
        assertEquals(island4.getIslands().get(0), island.getIslands().get(4));
    }

    /**
     * Test that when merging islands, towers and students are merged accurately and that exceptions
     * are thrown in the correct way
     */
    @Test
    public void mergeIslandTest2()
    {
        Student student = new Student(SchoolColor.PINK);
        Tower tower = new Tower(TowerColor.BLACK);
        island.addStudent(student);
        island.addTower(tower);
        assertEquals(1, island.getStudents().size());
        assertEquals(student, island.getStudents().get(0));
        assertEquals(1, island.getTowers().size());
        assertEquals(tower, island.getTowers().get(0));

        Island island1 = new Island();
        Student student1 = new Student(SchoolColor.GREEN);
        Tower tower1 = new Tower(TowerColor.BLACK);
        island1.addStudent(student1);
        island1.addTower(tower1);
        assertEquals(1, island1.getStudents().size());
        assertEquals(student1, island1.getStudents().get(0));
        assertEquals(1, island1.getTowers().size());
        assertEquals(tower1, island1.getTowers().get(0));

        // Merge of two normal islands
        island.mergeIsland(island1);
        assertEquals(2, island.getStudents().size());
        assertEquals(1, island.getIslands().get(0).getStudents().size());
        assertEquals(student, island.getIslands().get(0).getStudents().get(0));
        assertEquals(1, island.getIslands().get(1).getStudents().size());
        assertEquals(student1, island.getIslands().get(1).getStudents().get(0));
        assertEquals(2, island.getTowers().size());
        assertEquals(tower, island.getTowers().get(0));
        assertEquals(tower1, island.getTowers().get(1));

        // Merge with an island without a tower is not possible
        assertThrows(IllegalArgumentException.class, () -> island.mergeIsland(new Island()));

        // An island without a tower can't merge
        Island island2 = new Island();
        assertThrows(IllegalArgumentException.class, () -> island2.mergeIsland(island));

        // Island with different tower colors can't merge
        island2.addTower(new Tower(TowerColor.WHITE));
        assertThrows(IllegalArgumentException.class, () -> island.mergeIsland(island2));
    }


    /**
     * Test that the method getStudentByColor works accurately
     */
    @Test
    public void getStudentsByColorTest()
    {
        Student student = new Student(SchoolColor.BLUE);

        // No student of this color on the island
        assertEquals(0, island.getStudentsByColor(SchoolColor.YELLOW));

        // Passing a null student
        assertEquals(0, island.getStudentsByColor(null));

        // One student of this color on the island
        island.addStudent(student);
        assertEquals(1, island.getStudentsByColor(SchoolColor.BLUE));

        island.addTower(new Tower(TowerColor.WHITE));
        Island island2 = new Island();
        island2.addTower(new Tower(TowerColor.WHITE));
        island.mergeIsland(island2);
        Student student1 = new Student(SchoolColor.GREEN);
        Student student2 = new Student(SchoolColor.GREEN);
        island.addStudent(student1);
        island.addStudent(student2);

        // Two students of this color on the island
        assertEquals(2, island.getStudentsByColor(SchoolColor.GREEN));
    }
}
