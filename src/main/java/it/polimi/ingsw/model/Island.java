package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class represents the collection of island tiles (at least 1). At first
 * there is the relation 1 to 1 with islands and island tiles, but as the game keeps
 * going the island tiles accumulate into bigger islands.
 */
public class Island
{
    /**
     * Collection of tiles
     */
    private List<IslandTile> tiles;

    /**
     * Constructor
     */
    public Island()
    {
        //Instantiate the list
        tiles = new ArrayList<IslandTile>();

        //Add the first tile
        tiles.add(new IslandTile());
    }

    /**
     * Method to add a student to the island (group)
     * @param student The student that has to be added
     */
    public void addStudent(Student student)
    {
        //I check if the student is not already present inside one tile
        for(int i = 0; i < tiles.size(); i++)
        {
            if(tiles.get(i).getStudentsList().contains(student))
            {
                return;
            }
        }

        //If it is all right I can add the student in the tile with fewer students
        int min = 0;
        for(int i = 0; i < tiles.size(); i++)
        {
            if(tiles.get(i).getStudents().length < tiles.get(min).getStudents().length)
            {
                //I found an island with fewer students
                min = i;
            }
        }

        //At the end I add the student (in case already added (or null) the addStudent will filter)
        tiles.get(min).addStudent(student);
    }

    /**
     * This method adds the tower passed via argument to one of the tiles
     * (the first with tower free)
     * @param tower The tower that has to be added
     */
    public void addTower(Tower tower)
    {
        //I check if the tower is already present
        for(int i = 0; i < tiles.size(); i++)
        {
            if(tiles.get(i).getTower().isPresent() && tiles.get(i).getTower().get().equals(tower))
            {
                return;
            }
        }

        //Find the first free
        for(int i = 0; i < tiles.size(); i++)
        {
            //In case of an empty optional, I add the tower
            if(tiles.get(i).getTower().isEmpty())
            {
                tiles.get(i).addTower(tower);
                return;
            }
        }
    }

    /**
     * Method to remove a specific tower
     * @param tower The tower that has to be removed
     */
    public void removeTower(Tower tower)
    {
        tiles.stream().forEach(t -> t.removeTower(tower));
    }

    /**
     * Method to remove all the towers in the tiles
     */
    public void removeAllTowers()
    {
        tiles.stream().forEach(t -> t.removeTower());
    }

    /**
     * Method to merge the island tiles in this island
     * @param island The island to be merged
     */
    public void mergeIsland(Island island)
    {
        //I add the single tile if it is not already present in the list
        island.tiles.stream().forEach(t ->
        {
            if(!this.tiles.contains(t))
            {
                this.tiles.add(t);
            }
        });
    }

    /**
     * Getters
     * TODO CHECK THE FUNCTIONAL
     */
    //TODO ADD THIS METHOD TO THE UML
    public List<Student> getStudentsList()
    {
        //Create the result list
        List<Student> list = new ArrayList<Student>();

        //Fill the list
        tiles.stream().forEach((t -> list.addAll(t.getStudentsList())));

        return list;
    }

    public Student[] getStudents()
    {
        //Create the list where I put all the students
        List<Student> list = getStudentsList();

        //Create the final array
        Student[] result = new Student[list.size()];

        //Assign all the students
        list.toArray(result);

        return result;
    }

    public Tower[] getTowers()
    {
        //Create the list where I put all the towers
        List<Tower> list = new ArrayList<Tower>();

        //Foreach tile I add the tower
        tiles.stream().map(t -> t.getTower()).filter(Optional::isPresent).forEach(o -> list.add(o.get()));

        //Create the final array
        Tower[] result = new Tower[list.size()];

        //Assign all the towers
        list.toArray(result);

        return result;
    }

    public int getStudentsByColor(SchoolColor color)
    {
        return (int)tiles.stream().flatMap(t -> t.getStudentsList().stream()).filter(s -> s.getColor().equals(color)).count();
    }
}
