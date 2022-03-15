package it.polimi.ingsw;

import it.polimi.ingsw.model.CloudTile;
import it.polimi.ingsw.model.CloudTileType;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class to test the CloudTile class
 */
public class CloudTileTest
{
    /**
     * Test that a normal student is added and a null one is not
     */
    @Test
    public void addStudentNull()
    {
        CloudTile cloud = new CloudTile(CloudTileType.TILE_2_4);

        cloud.addStudent(new Student(SchoolColor.GREEN));
        assertEquals(cloud.getStudentsList().get(0).getColor(), SchoolColor.GREEN);

        cloud.addStudent(null);
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

        cloud.addStudent(student);
        assertEquals(cloud.getStudentsList().get(0), student);
        assertEquals(cloud.getStudents()[0], student);

        cloud.addStudent(student);
        assertEquals(cloud.getStudentsList().size(), 1);
    }

    /**
     * Test that students are not added when the tile dimension is reached
     */
    @Test
    public void addStudentMaxDimension()
    {
        CloudTile cloud = new CloudTile(CloudTileType.TILE_2_4);
        Student student1 = new Student(SchoolColor.RED);
        Student student2 = new Student(SchoolColor.BLUE);
        Student student3 = new Student(SchoolColor.YELLOW);
        Student student4 = new Student(SchoolColor.PINK);

        cloud.addStudent(student1);
        assertEquals(cloud.getStudentsList().get(0), student1);
        assertEquals(cloud.getStudents()[0], student1);

        cloud.addStudent(student2);
        assertEquals(cloud.getStudentsList().get(1), student2);
        assertEquals(cloud.getStudents()[1], student2);

        cloud.addStudent(student3);
        assertEquals(cloud.getStudentsList().get(2), student3);
        assertEquals(cloud.getStudents()[2], student3);

        cloud.addStudent(student4);
        assertEquals(cloud.getStudentsList().size(), 3);
    }

    /**
     * Test that a contained student is removed
     * instead if the student is not contained or it is null nothing changes
     */
    @Test
    public void removeStudentNull()
    {
        CloudTile cloud = new CloudTile(CloudTileType.TILE_2_4);
        Student student = new Student(SchoolColor.GREEN);

        cloud.addStudent(student);
        assertEquals(cloud.getStudentsList().get(0), student);

        cloud.removeStudent(null);
        assertEquals(cloud.getStudentsList().size(), 1);

        cloud.removeStudent(new Student(SchoolColor.YELLOW));
        assertEquals(cloud.getStudentsList().size(), 1);

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
}

