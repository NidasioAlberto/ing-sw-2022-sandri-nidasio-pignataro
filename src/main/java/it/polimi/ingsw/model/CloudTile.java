package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the cloud tile. During the setup phase, these tiles
 * are positioned with some students on them depending on the actual game players.
 */
public class CloudTile
{
    /**
     * List of students discs on the tile
     */
    private List<Student> students;

    /**
     * Cloud tile type dependent on the number of players
     */
    private CloudTileType type;

    /**
     * Constructor
     * @param type The CloudTileType (2_4 or 3)
     */
    public CloudTile(CloudTileType type)
    {
        //Instantiate the list
        students = new ArrayList<Student>();

        //Assign the type
        this.type = type == null ? CloudTileType.TILE_2_4 : type;
    }

    /**
     * Method to add the student on the tile
     * @param student The student to be added
     */
    public void addStudent(Student student)
    {
        //Check if the student is not null and not contained already
        //and that the maximum dimension is not exceeded
        if(student != null && !students.contains(student) && students.size() < type.getPlayerNum())
        {
            students.add(student);
        }
    }

    //TODO DELETE THE addStudents method from CloudTile (it is not useful because addStudent alone is plenty)

    /**
     * Method to remove a specific student from the cloud tile
     * @param student The student that has to be removed
     */
    public void removeStudent(Student student)
    {
        //Check if the student is not null and is contained inside the list
        if(student != null && students.contains(student))
        {
            students.remove(student);
        }
    }

    /**
     * Removes all the students from the tile
     */
    public void removeStudents()
    {
        students.clear();
    }

    /**
     * Getters
     */
    public CloudTileType getType() { return type; }

    //TODO ADD THIS METHOD TO THE UML
    public List<Student> getStudentsList() { return new ArrayList<Student>(students); }

    //TODO ADD THIS METHOD TO THE UML
    public Student[] getStudents()
    {
        //Create an intermediate list
        List<Student> list = getStudentsList();

        //Create the result array
        Student[] result = new Student[list.size()];

        //Fill the array
        list.toArray(result);

        return result;
    }
}
