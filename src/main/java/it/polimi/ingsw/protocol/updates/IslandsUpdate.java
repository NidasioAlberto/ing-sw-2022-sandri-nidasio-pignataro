package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.client.Visualizable;
import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.Student;
import it.polimi.ingsw.model.Tower;
import it.polimi.ingsw.model.TowerColor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class IslandsUpdate extends ModelUpdate
{
    @Serial
    private static final long serialVersionUID = -1290378826120993267L;

    /**
     * The whole island list of objects
     */
    private List<Island> islands;

    /**
     * The current mother nature position
     */
    private int motherNatureIndex;

    /**
     * Constructor
     * 
     * @param islands Collection of all the islands
     * @param motherNatureIndex Index of mother nature position inside the collection
     */
    public IslandsUpdate(List<Island> islands, int motherNatureIndex)
    {
        if (islands == null)
            throw new NullPointerException("[IslandsUpdate] Null island list");
        // It is possible according to java 8 documentation
        if (islands.contains(null))
            throw new NullPointerException("[IslandsUpdate] Null island inside the list");
        if (motherNatureIndex < 0 || motherNatureIndex >= islands.size())
            throw new IndexOutOfBoundsException("[IslandsUpdate] Mother nature index out of bounds");

        this.islands = islands;
        this.motherNatureIndex = motherNatureIndex;
    }

    public List<Island> getIslands()
    {
        return islands;
    }

    public int getMotherNatureIndex()
    {
        return motherNatureIndex;
    }

    @Override
    public void handleUpdate(Visualizable handler)
    {
        handler.displayIslands(this);
    }

    // TUI

    /**
     * Draws a 5x10 representation of each island with 1 column separation.
     */
    @Override
    public String toString()
    {
        String rep = "";

        // Clear the lines
        for (int i = 0; i < 5; i++)
        {
            rep += PrintHelper.ERASE_FROM_CURSOR_TILL_END_OF_LINE;
            rep += PrintHelper.moveCursorRelative(-1, 0);
        }
        rep += PrintHelper.moveCursorRelative(5, 0);

        // Draw islands
        for (Island island : islands)
        {
            // Draw the island
            rep += island.toString();

            // Draw its number
            rep += PrintHelper.moveCursorRelative(0, -7);
            rep += islands.indexOf(island);
            rep += PrintHelper.moveCursorRelative(0, 6 - (islands.indexOf(island) > 9 ? 1 : 0));

            // Move the cursor to draw the next island
            rep += PrintHelper.moveCursorRelative(4, 2);
        }

        // Draw mother nature
        rep += PrintHelper.moveToBeginningOfLine(-3);
        rep += PrintHelper.moveCursorRelative(0, 9 + motherNatureIndex * 12);
        rep += GamePieces.MOTHER_NATURE.toString();
        rep += PrintHelper.moveToBeginningOfLine(-2);

        return rep;
    }

    public static void main(String[] args)
    {
        List<Island> islands = new ArrayList<>();

        islands.add(new Island());
        islands.add(new Island());
        islands.add(new Island());
        islands.add(new Island());
        islands.add(new Island());

        Island island1 = new Island();
        island1.addStudent(new Student(SchoolColor.GREEN));
        island1.addStudent(new Student(SchoolColor.YELLOW));
        island1.addStudent(new Student(SchoolColor.BLUE));
        island1.addStudent(new Student(SchoolColor.BLUE));
        island1.addTower(new Tower(TowerColor.BLACK));
        island1.addNoEntryTile();
        island1.addNoEntryTile();
        islands.add(island1);

        Island island2 = new Island();
        island2.addStudent(new Student(SchoolColor.PINK));
        island2.addStudent(new Student(SchoolColor.PINK));
        island2.addStudent(new Student(SchoolColor.RED));
        island2.addStudent(new Student(SchoolColor.GREEN));
        island2.addTower(new Tower(TowerColor.GREY));
        island2.addNoEntryTile();
        island2.addNoEntryTile();
        islands.add(island2);

        islands.add(new Island());

        Island island3 = new Island();
        island3.addStudent(new Student(SchoolColor.PINK));
        island3.addStudent(new Student(SchoolColor.PINK));
        island3.addStudent(new Student(SchoolColor.PINK));
        island3.addStudent(new Student(SchoolColor.BLUE));
        island3.addTower(new Tower(TowerColor.WHITE));
        island3.addNoEntryTile();
        island3.addNoEntryTile();
        islands.add(island3);

        islands.add(new Island());
        islands.add(new Island());

        IslandsUpdate update = new IslandsUpdate(islands, 1);

        System.out.print(update);
    }
}
