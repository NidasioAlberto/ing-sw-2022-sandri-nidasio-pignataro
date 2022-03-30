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
        assertEquals(SchoolColor.GREEN ,cloud.getStudentsList().get(0).getColor());

        // Add a null student
        assertThrows(NullPointerException.class, () ->  cloud.addStudent(null));
        assertEquals(SchoolColor.GREEN, cloud.getStudentsList().get(0).getColor());
        assertEquals(1, cloud.getStudentsList().size());
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
        assertEquals(student, cloud.getStudentsList().get(0));
        assertEquals(student, cloud.getStudents()[0]);

        // Add a duplicate student
        cloud.addStudent(student);
        assertEquals(student, cloud.getStudentsList().get(0));
        assertEquals(student, cloud.getStudents()[0]);
        assertEquals(1, cloud.getStudentsList().size());
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
        assertEquals(student1, cloud_2_4.getStudentsList().get(0));
        assertEquals(student1, cloud_2_4.getStudents()[0]);

        cloud_2_4.addStudent(student2);
        assertEquals(student1, cloud_2_4.getStudentsList().get(0));
        assertEquals(student2, cloud_2_4.getStudentsList().get(1));
        assertEquals(student2, cloud_2_4.getStudents()[1]);

        cloud_2_4.addStudent(student3);
        assertEquals(student1, cloud_2_4.getStudentsList().get(0));
        assertEquals(student2, cloud_2_4.getStudentsList().get(1));
        assertEquals(student3, cloud_2_4.getStudentsList().get(2));
        assertEquals(student3, cloud_2_4.getStudents()[2]);

        cloud_2_4.addStudent(student4);
        assertEquals(student1, cloud_2_4.getStudentsList().get(0));
        assertEquals(student2, cloud_2_4.getStudentsList().get(1));
        assertEquals(student3, cloud_2_4.getStudentsList().get(2));
        assertEquals(3, cloud_2_4.getStudentsList().size());

        // A CloudTile of type TILE_3 must have 4 students max
        cloud_3.addStudent(student1);
        assertEquals(student1, cloud_3.getStudentsList().get(0));
        assertEquals(student1, cloud_3.getStudents()[0]);

        cloud_3.addStudent(student2);
        assertEquals(student1, cloud_3.getStudentsList().get(0));
        assertEquals(student2, cloud_3.getStudentsList().get(1));
        assertEquals(student2, cloud_3.getStudents()[1]);

        cloud_3.addStudent(student3);
        assertEquals(student1, cloud_3.getStudentsList().get(0));
        assertEquals(student2, cloud_3.getStudentsList().get(1));
        assertEquals(student3, cloud_3.getStudentsList().get(2));
        assertEquals(student3, cloud_3.getStudents()[2]);

        cloud_3.addStudent(student4);
        assertEquals(student1, cloud_3.getStudentsList().get(0));
        assertEquals(student2, cloud_3.getStudentsList().get(1));
        assertEquals(student3, cloud_3.getStudentsList().get(2));
        assertEquals(student4, cloud_3.getStudentsList().get(3));
        assertEquals(student4, cloud_3.getStudents()[3]);

        cloud_3.addStudent(student5);
        assertEquals(student1, cloud_3.getStudentsList().get(0));
        assertEquals(student2, cloud_3.getStudentsList().get(1));
        assertEquals(student3, cloud_3.getStudentsList().get(2));
        assertEquals(student4, cloud_3.getStudentsList().get(3));
        assertEquals(4, cloud_3.getStudentsList().size());
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
        assertEquals(student, cloud.getStudentsList().get(0));

        // Remove a null student
        assertThrows(NullPointerException.class, () ->  cloud.removeStudent(null));
        assertEquals(student, cloud.getStudentsList().get(0));
        assertEquals(1, cloud.getStudentsList().size());

        // Remove a not contained student
        cloud.removeStudent(new Student(SchoolColor.YELLOW));
        assertEquals(student, cloud.getStudentsList().get(0));
        assertEquals(1, cloud.getStudentsList().size());

        // Remove a contained student
        cloud.removeStudent(student);
        assertEquals(0, cloud.getStudentsList().size());
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
        assertEquals(0, cloud.getStudentsList().size());

        cloud.addStudent(student1);
        assertEquals(student1, cloud.getStudentsList().get(0));
        assertEquals(student1, cloud.getStudents()[0]);

        cloud.removeStudents();
        assertEquals(0, cloud.getStudentsList().size());

        cloud.addStudent(student1);
        assertEquals(student1, cloud.getStudentsList().get(0));
        assertEquals(student1, cloud.getStudents()[0]);
        cloud.addStudent(student2);
        assertEquals(student2, cloud.getStudentsList().get(1));
        assertEquals(student2, cloud.getStudents()[1]);
        cloud.removeStudents();
        assertEquals(0, cloud.getStudentsList().size());
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
