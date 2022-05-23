package it.polimi.ingsw.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import it.polimi.ingsw.model.exceptions.NoLegitActionException;

/**
 * This class represents the physical island tile displayed in the game. It differs from the island
 * because the last one can actually be the composition of more tiles.
 */
public class IslandTile implements Serializable
{
    @Serial
    private static final long serialVersionUID = -6662308214804626348L;

    /**
     * List of students positioned on this tile
     */
    private List<Student> students;

    /**
     * The tower that can be or not on the island
     */
    private Tower tower;

    /**
     * Constructor
     */
    public IslandTile()
    {
        // Instantiate the internal state
        students = new ArrayList<Student>();

        // At first there is no tower on the tile
        tower = null;
    }

    /**
     * Method to add a new student to the tile (not null)
     * 
     * @param student The student to be added
     * @throws NullPointerException thrown if the student is null
     */
    public void addStudent(Student student) throws NullPointerException
    {
        if (student == null)
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
     * @throws NullPointerException if the tower is null
     */
    public void addTower(Tower tower) throws NullPointerException, NoLegitActionException
    {
        if (tower == null)
            throw new NullPointerException("[IslandTile] Null tower");

        // Check if the optional is empty
        if (this.tower == null)
            this.tower = tower;
        else
            throw new NoLegitActionException();
    }

    /**
     * Method to remove the tower, no matter what tower it is. Keep in mind that this method does
     * not move the removed tower back its player's board!
     */
    public void removeTower()
    {
        this.tower = null;
    }

    /**
     * Method to remove the tower passed via parameter. If not equal I don't remove the tower.
     * 
     * @param tower The tower that has to be removed
     */
    public void removeTower(Tower tower)
    {
        // Delete the tower only if they are the same object
        if (this.tower != null && this.tower.equals(tower))
        {
            this.tower = null;
        }
    }

    public List<Student> getStudents()
    {
        return new ArrayList<Student>(students);
    }

    public Optional<Tower> getTower()
    {
        return tower == null ? Optional.empty() : Optional.of(tower);
    }
}
