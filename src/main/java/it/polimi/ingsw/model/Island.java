package it.polimi.ingsw.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

/**
 * This class represents the collection of island tiles (at least 1). At first there is the relation 1 to 1 with islands and island tiles, but as the
 * game keeps going the island tiles accumulate into bigger islands.
 */
public class Island implements Serializable
{
    @Serial
    private static final long serialVersionUID = -5547031297637105565L;

    /**
     * Collection of tiles.
     */
    private List<IslandTile> tiles;

    /**
     * Number of no entry tiles on this island.
     */
    private int noEntryTiles;

    /**
     * Created an Island object. It initializes one island child tile.
     */
    public Island()
    {
        // Instantiate the list
        tiles = new ArrayList<IslandTile>();

        // Add the first tile
        tiles.add(new IslandTile());

        noEntryTiles = 0;
    }

    /**
     * Method to add a student to the island (group).
     *
     * @param student The student that has to be added.
     * @throws NullPointerException If the student is null.
     */
    public void addStudent(Student student) throws NullPointerException
    {
        // I check if the student is not already present inside one tile
        for (int i = 0; i < tiles.size(); i++)
        {
            if (tiles.get(i).getStudents().contains(student))
            {
                return;
            }
        }

        // If it is all right I can add the student in the tile with fewer students
        int min = 0;
        for (int i = 0; i < tiles.size(); i++)
        {
            if (tiles.get(i).getStudents().size() < tiles.get(min).getStudents().size())
            {
                // I found an island with fewer students
                min = i;
            }
        }

        // At the end I add the student (in case already added (or null) the addStudent will filter)
        tiles.get(min).addStudent(student);
    }

    /**
     * This method adds the tower passed via argument to one of the tiles (the first with tower free).
     *
     * @param tower The tower that has to be added.
     * @throws NullPointerException If the tower is null.
     */
    public void addTower(Tower tower) throws NullPointerException, IllegalArgumentException
    {
        // Check if there is already a different color tower on this island
        if (tiles.stream().map(tile -> tile.getTower()).flatMap(Optional::stream).filter(t -> t.getColor() != tower.getColor()).count() > 0)
        {
            throw new IllegalArgumentException("[Island] The tower color is not correct");
        }

        // I check if the tower is already present
        for (int i = 0; i < tiles.size(); i++)
        {
            if (tiles.get(i).getTower().isPresent() && tiles.get(i).getTower().get().equals(tower))
            {
                return;
            }
        }

        // Find the first free
        for (int i = 0; i < tiles.size(); i++)
        {
            // In case of an empty optional, I add the tower
            if (tiles.get(i).getTower().isEmpty())
            {
                tiles.get(i).addTower(tower);
                return;
            }
        }
    }

    /**
     * Method to remove a specific tower. Keep in mind that this method does not move the removed tower its player board!
     *
     * @param tower The tower that has to be removed.
     */
    public void removeTower(Tower tower)
    {
        tiles.stream().forEach(t -> t.removeTower(tower));
    }

    /**
     * Method to remove all the towers in the tiles. Keep in mind that this method does not move the removed towers their player board!
     */
    public void removeAllTowers()
    {
        tiles.stream().forEach(t -> t.removeTower());
    }

    /**
     * Method to merge the island tiles in this island.
     *
     * @param island The island to be merged.
     * @throws NullPointerException If the island is null.
     */
    public void mergeIsland(Island island) throws NullPointerException, IllegalArgumentException
    {

        if (island == null)
        {
            throw new NullPointerException("[Island] A null island was provided");
        } else if (island.tiles.stream().filter(tile -> tile.getTower().isEmpty()).count() == island.tiles.size()
                || this.tiles.stream().filter(tile -> tile.getTower().isEmpty()).count() == this.tiles.size()
                || island.tiles.stream().map(tile -> tile.getTower()).flatMap(Optional::stream)
                        .filter(t -> t.getColor() != this.tiles.get(0).getTower().get().getColor()).count() > 0)
        {
            // There is at least one island that has all the tiles without tower
            // or the color of towers is different on the tiles
            throw new IllegalArgumentException("[Island] Not possible to merge islands, towers are not correct");
        } else
        {
            // I add the single tile if it is not already present in the list
            island.tiles.stream().forEach(t -> {
                if (!this.tiles.contains(t))
                {
                    this.tiles.add(t);
                }
            });

            // I add the noEntryTiles
            this.noEntryTiles += island.noEntryTiles;
        }
    }

    /**
     * Returns the list of student currently on the island cluster.
     * 
     * @return All the students on the Island.
     */
    public List<Student> getStudents()
    {
        // Create the result list
        List<Student> list = new ArrayList<Student>();

        // Fill the list
        tiles.stream().forEach((t -> list.addAll(t.getStudents())));

        return list;
    }

    /**
     * Returns all the towers on the island cluster.
     * 
     * @return All teh towers on the island.
     */
    public List<Tower> getTowers()
    {
        return tiles.stream().map(tile -> tile.getTower()).flatMap(Optional::stream).collect(Collectors.toList());
    }

    /**
     * Counts the number of students of the specified colors on all tiles.
     */
    public int getStudentsByColor(SchoolColor color)
    {
        return (int) tiles.stream().flatMap(t -> t.getStudents().stream()).filter(s -> s.getColor().equals(color)).count();
    }

    /**
     * Returns the number of no entry tiles on the island cluster.
     * 
     * @return The number of no entry tile on the island.
     */
    public int getNoEntryTiles()
    {
        return noEntryTiles;
    }

    /**
     * Add one no entry tile to the island by increasing an internal counter.
     */
    public void addNoEntryTile()
    {
        noEntryTiles++;
    }

    /**
     * Removes one no entry tile form the island.
     */
    public void removeNoEntryTile()
    {
        noEntryTiles = noEntryTiles > 0 ? noEntryTiles - 1 : 0;
    }

    /**
     * Returns the list of island tiles inside of the cluster.
     * 
     * @return The list of island tiles.
     */
    public List<IslandTile> getIslands()
    {
        return new ArrayList<>(tiles);
    }

    /**
     * Returns the number of students for the given color in string format.
     * 
     * @param color Color to filer the students.
     * @return Number of students with the given color.
     */
    private String drawStudentsNumber(SchoolColor color)
    {
        return PrintHelper.drawStudentsNumber(getStudentsByColor(color), color);
    }

    /**
     * Returns the number of towers in string format.
     * 
     * @return Number of towers on the island.
     */
    private String drawTowers()
    {
        if (getTowers().size() == 0)
            return "  ";
        else
            return getTowers().size() + PrintHelper.drawColor(getTowers().get(0).getColor(), GamePieces.TOWER.toString());

    }

    /**
     * Returns the number of no entry tiles in string format.
     * 
     * @return Number of no entry tiles on the island.
     */
    private String drawNoEntryTiles()
    {
        if (getNoEntryTiles() == 0)
            return "  ";
        // Print red noEntryTiles
        else
            return getNoEntryTiles() + "\u001B[31m" + GamePieces.NO_ENTRY_TILE + "\u001B[97m";
    }

    /**
     * Draws a 4x10 representation of the island. Uses escape characters to be printed everywhere on the terminal.
     */
    @Override
    public String toString()
    {
        String rep = "";

        rep += "  ______ " + PrintHelper.moveCursorRelative(-1, -9);
        rep += " " + GamePieces.SLASH + drawStudentsNumber(SchoolColor.BLUE) + " " + drawStudentsNumber(SchoolColor.GREEN) + " "
                + GamePieces.BACKSLASH + PrintHelper.moveCursorRelative(-1, -9);
        rep += GamePieces.SLASH + drawStudentsNumber(SchoolColor.PINK) + " " + drawStudentsNumber(SchoolColor.RED) + " "
                + drawStudentsNumber(SchoolColor.YELLOW) + GamePieces.BACKSLASH + PrintHelper.moveCursorRelative(-1, -10);
        rep += GamePieces.BACKSLASH + drawTowers() + " " + drawNoEntryTiles() + "   " + GamePieces.SLASH + PrintHelper.moveCursorRelative(-1, -10);
        rep += " " + GamePieces.BACKSLASH + "______" + GamePieces.SLASH + " ";

        return rep;
    }
}
