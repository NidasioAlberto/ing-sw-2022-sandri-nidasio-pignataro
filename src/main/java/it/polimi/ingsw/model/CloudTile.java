package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the cloud tile. During the setup phase, these tiles are positioned with
 * some students on them depending on the actual game players.
 */
public class CloudTile
{
    /**
     * List of student discs on the tile.
     */
    private List<Student> students;

    /**
     * Cloud tile type dependent on the number of players.
     */
    private CloudTileType type;

    /**
     * Constructor.
     * 
     * @param type The CloudTileType (2_4 or 3).
     * @throws NullPointerException Thrown if the specified color is invalid.
     */
    public CloudTile(CloudTileType type) throws NullPointerException
    {
        if (type == null)
            throw new NullPointerException("[CloudTile] Null cloud tile type");

        students = new ArrayList<Student>();
        this.type = type;
    }

    /**
     * Method to add the student on the tile.
     * 
     * @param student The student to be added.
     * @throws NullPointerException if the student is null
     */
    public void addStudent(Student student) throws NullPointerException
    {
        // Check if the student is not null
        if (student == null)
            throw new NullPointerException("[CloudTile] Tried to add a null student");

        // Check if the student is not already contained and
        // that the maximum dimension is not exceeded
        if (!students.contains(student) && students.size() < type.getStudentCapacity())
        {
            students.add(student);
        }
    }

    /**
     * Method to remove a specific student from the cloud tile.
     * 
     * @param student The student that has to be removed.
     * @throws NullPointerException if the student is null
     */
    public void removeStudent(Student student) throws NullPointerException
    {
        // Check if the student is not null
        if (student == null)
            throw new NullPointerException("[CloudTile] Tried to remove a null student");

        // Check if the student is contained inside the list
        if (students.contains(student))
        {
            students.remove(student);
        }
    }

    /**
     * Removes all the students from the tile.
     */
    public void removeStudents()
    {
        students.clear();
    }

    public CloudTileType getType()
    {
        return type;
    }

    public List<Student> getStudentsList()
    {
        return new ArrayList<Student>(students);
    }

    public Student[] getStudents()
    {
        // Create an intermediate list
        List<Student> list = getStudentsList();

        // Create the result array
        Student[] result = new Student[list.size()];

        // Fill the array
        list.toArray(result);

        return result;
    }
}
