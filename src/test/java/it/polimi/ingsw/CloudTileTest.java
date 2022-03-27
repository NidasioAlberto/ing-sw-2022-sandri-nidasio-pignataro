package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the CloudTile class
 */
public class CloudTileTest
{
    @Test
    /**
     * For each CloudTileType, create a CloudTile for that type and check it.
     */
    public void checkTypeTest()
    {
        for (CloudTileType type : CloudTileType.values())
        {
            CloudTile cloud = new CloudTile(type);
            assertEquals(cloud.getType(), type);
        }
    }

    /**
     * Test that a normal student is added and a null one is not
     */
    @Test
    public void addStudentNull()
    {
        CloudTile cloud = new CloudTile(CloudTileType.TILE_2_4);

        // Add a normal student
        cloud.addStudent(new Student(SchoolColor.GREEN));
        assertEquals(cloud.getStudentsList().get(0).getColor(), SchoolColor.GREEN);

        // Add a null student
        assertThrows(NullPointerException.class, () ->  cloud.addStudent(null));
        assertEquals(cloud.getStudentsList().get(0).getColor(), SchoolColor.GREEN);
        assertEquals(cloud.getStudentsList().size(), 1);
    }

    /**
     * Test that duplicates are not added
     */
    @Test
    public void addStudentDuplicate()
    {
        CloudTile cloud = new CloudTile(CloudTileType.TILE_2_4);
        Student student = new Student(SchoolColor.RED);

        // Add a normal student
        cloud.addStudent(student);
        assertEquals(cloud.getStudentsList().get(0), student);
        assertEquals(cloud.getStudents()[0], student);

        // Add a duplicate student
        cloud.addStudent(student);
        assertEquals(cloud.getStudentsList().get(0), student);
        assertEquals(cloud.getStudents()[0], student);
        assertEquals(cloud.getStudentsList().size(), 1);
    }

    /**
     * Test that students are not added when the tile dimension is reached
     */
    @Test
    public void addStudentMaxDimension()
    {
        CloudTile cloud_2_4 = new CloudTile(CloudTileType.TILE_2_4);
        CloudTile cloud_3 = new CloudTile(CloudTileType.TILE_3);
        Student student1 = new Student(SchoolColor.RED);
        Student student2 = new Student(SchoolColor.BLUE);
        Student student3 = new Student(SchoolColor.YELLOW);
        Student student4 = new Student(SchoolColor.PINK);
        Student student5 = new Student(SchoolColor.GREEN);

        // A CloudTile of type TILE_2_4 must have 3 students max
        cloud_2_4.addStudent(student1);
        assertEquals(cloud_2_4.getStudentsList().get(0), student1);
        assertEquals(cloud_2_4.getStudents()[0], student1);

        cloud_2_4.addStudent(student2);
        assertEquals(cloud_2_4.getStudentsList().get(0), student1);
        assertEquals(cloud_2_4.getStudentsList().get(1), student2);
        assertEquals(cloud_2_4.getStudents()[1], student2);

        cloud_2_4.addStudent(student3);
        assertEquals(cloud_2_4.getStudentsList().get(0), student1);
        assertEquals(cloud_2_4.getStudentsList().get(1), student2);
        assertEquals(cloud_2_4.getStudentsList().get(2), student3);
        assertEquals(cloud_2_4.getStudents()[2], student3);

        cloud_2_4.addStudent(student4);
        assertEquals(cloud_2_4.getStudentsList().get(0), student1);
        assertEquals(cloud_2_4.getStudentsList().get(1), student2);
        assertEquals(cloud_2_4.getStudentsList().get(2), student3);
        assertEquals(cloud_2_4.getStudentsList().size(), 3);

        // A CloudTile of type TILE_3 must have 4 students max
        cloud_3.addStudent(student1);
        assertEquals(cloud_3.getStudentsList().get(0), student1);
        assertEquals(cloud_3.getStudents()[0], student1);

        cloud_3.addStudent(student2);
        assertEquals(cloud_3.getStudentsList().get(0), student1);
        assertEquals(cloud_3.getStudentsList().get(1), student2);
        assertEquals(cloud_3.getStudents()[1], student2);

        cloud_3.addStudent(student3);
        assertEquals(cloud_3.getStudentsList().get(0), student1);
        assertEquals(cloud_3.getStudentsList().get(1), student2);
        assertEquals(cloud_3.getStudentsList().get(2), student3);
        assertEquals(cloud_3.getStudents()[2], student3);

        cloud_3.addStudent(student4);
        assertEquals(cloud_3.getStudentsList().get(0), student1);
        assertEquals(cloud_3.getStudentsList().get(1), student2);
        assertEquals(cloud_3.getStudentsList().get(2), student3);
        assertEquals(cloud_3.getStudentsList().get(3), student4);
        assertEquals(cloud_3.getStudents()[3], student4);

        cloud_3.addStudent(student5);
        assertEquals(cloud_3.getStudentsList().get(0), student1);
        assertEquals(cloud_3.getStudentsList().get(1), student2);
        assertEquals(cloud_3.getStudentsList().get(2), student3);
        assertEquals(cloud_3.getStudentsList().get(3), student4);
        assertEquals(cloud_3.getStudentsList().size(), 4);
    }

    /**
     * Test that a contained student is removed instead if the student is not contained or it is
     * null nothing changes
     */
    @Test
    public void removeStudentNull()
    {
        CloudTile cloud = new CloudTile(CloudTileType.TILE_2_4);
        Student student = new Student(SchoolColor.GREEN);

        // Add a normal student
        cloud.addStudent(student);
        assertEquals(cloud.getStudentsList().get(0), student);

        // Remove a null student
        assertThrows(NullPointerException.class, () ->  cloud.removeStudent(null));
        assertEquals(cloud.getStudentsList().get(0), student);
        assertEquals(cloud.getStudentsList().size(), 1);

        // Remove a not contained student
        cloud.removeStudent(new Student(SchoolColor.YELLOW));
        assertEquals(cloud.getStudentsList().get(0), student);
        assertEquals(cloud.getStudentsList().size(), 1);

        // Remove a contained student
        cloud.removeStudent(student);
        assertEquals(cloud.getStudentsList().size(), 0);
    }

    /**
     * Test the removeStudents method
     */
    @Test
    public void removeStudents()
    {
        CloudTile cloud = new CloudTile(CloudTileType.TILE_2_4);
        Student student1 = new Student(SchoolColor.GREEN);
        Student student2 = new Student(SchoolColor.BLUE);

        cloud.removeStudents();
        assertEquals(cloud.getStudentsList().size(), 0);

        cloud.addStudent(student1);
        assertEquals(cloud.getStudentsList().get(0), student1);
        assertEquals(cloud.getStudents()[0], student1);

        cloud.removeStudents();
        assertEquals(cloud.getStudentsList().size(), 0);

        cloud.addStudent(student1);
        assertEquals(cloud.getStudentsList().get(0), student1);
        assertEquals(cloud.getStudents()[0], student1);
        cloud.addStudent(student2);
        assertEquals(cloud.getStudentsList().get(1), student2);
        assertEquals(cloud.getStudents()[1], student2);
        cloud.removeStudents();
        assertEquals(cloud.getStudentsList().size(), 0);
    }

    /**
     * The cloud tile constructor should throw NullPointerException if an invalid parameter is
     * passed.
     */
    @Test
    public void nullParameterTest()
    {
        assertThrows(NullPointerException.class, () -> new CloudTile(null));
    }
}
