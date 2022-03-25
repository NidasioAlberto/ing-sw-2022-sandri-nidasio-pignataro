package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class represents the physical island tile displayed in the game. It differs from the island
 * because the last one can actually be the composition of more tiles.
 */
public class IslandTile
{
    /**
     * List of students positioned on this tile
     */
    private List<Student> students;

    /**
     * The optional tower that can be or not on the island
     */
    private Optional<Tower> tower;

    /**
     * Constructor
     */
    public IslandTile()
    {
        // Instantiate the internal state
        students = new ArrayList<Student>();

        // At first there is no tower on the tile
        tower = Optional.empty();
    }

    /**
     * Method to add a new student to the tile (not null)
     * 
     * @param student The student to be added
     * @throws NullPointerException thrown if the student is null
     */
    public void addStudent(Student student) throws NullPointerException
    {
        if(student == null)
            throw new NullPointerException("[IslandTile] Null student");

        // Check if the passed student is not null and if the list contains
        // already that student
        if (!students.contains(student))
        {
            students.add(student);
        }
    }

    /**
     * Method to add a tower to the tile if not already present
     * 
     * @param tower The tower (not null) to be added
     */
    public void addTower(Tower tower) throws NullPointerException
    {
        if(tower == null)
            throw new NullPointerException("[IslandTile] Null tower");

        // Check if the optional is empty and if the passed tower is not null
        if (tower != null && this.tower.isEmpty())
        {
            this.tower = Optional.of(tower);
        }
    }

    /**
     * Method to remove the tower, no matter what tower it is
     */
    public void removeTower()
    {
        this.tower = Optional.empty();
    }

    /**
     * Method to remove the tower passed via parameter. If not equal I don't remove the tower.
     * 
     * @param tower The tower that has to be removed
     */
    public void removeTower(Tower tower)
    {
        // Delete the tower only if they are the same object
        if (this.tower.isPresent() && this.tower.get().equals(tower))
        {
            this.tower = Optional.empty();
        }
    }

    /**
     * Getters
     */
    public Student[] getStudents()
    {
        // Create the returned array
        Student[] result = new Student[students.size()];
        // Put all the students inside
        students.toArray(result);
        return result;
    }

    public List<Student> getStudentsList()
    {
        return new ArrayList<Student>(students);
    }

    public Optional<Tower> getTower()
    {
        return tower;
    }
}
