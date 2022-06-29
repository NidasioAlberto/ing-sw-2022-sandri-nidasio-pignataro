package it.polimi.ingsw.model;

import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a cloud tile. During the setup phase, multiple cloud tiles are positioned with some students on them depending on the number
 * of game players.
 */
public class CloudTile implements Serializable
{
    @Serial
    private static final long serialVersionUID = 8708745491450029913L;

    /**
     * List of student discs on the tile.
     */
    private List<Student> students;

    /**
     * Cloud tile type dependent on the number of players.
     */
    private CloudTileType type;

    /**
     * Created a CloudTile object.
     * 
     * @param type The tile type indicating how many students the tile can hold (2 or 3).
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
     * Adds the given student on the tile.
     * 
     * @param student The student to be added.
     * @throws NullPointerException Ff the student is null.
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
     * Removes a specific student from the cloud tile.
     * 
     * @param student The student that has to be removed.
     * @throws NullPointerException If the student is null.
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

    /**
     * Returns the cloud tile's type, indicating its capacity in term of students.
     * 
     * @return The cloud tile's type.
     */
    public CloudTileType getType()
    {
        return type;
    }

    /***
     * Returns the students on the tile without removing them.
     * 
     * @return The tile's students.
     */
    public List<Student> getStudents()
    {
        return new ArrayList<Student>(students);
    }

    /**
     * Draws the student at position index of students if present.
     * 
     * @param index Index of the student.
     * @return The representation of the student at the given index.
     */
    private String drawStudent(int index)
    {
        if (students.size() > index)
        {
            return PrintHelper.drawColor(students.get(index).getColor(), GamePieces.STUDENT.toString());
        } else
            return " ";
    }

    /**
     * Draws a 4x6 representation of the cloud tile. Uses escape characters to be printed everywhere on the terminal.
     */
    @Override
    public String toString()
    {
        String rep = "";

        // Previous version, doesn't work on Windows
        /*
         * rep += "╭────╮" + PrintHelper.moveCursorRelative(-1, -6); rep += "│" + drawStudent(0) + "  " + drawStudent(1) + "│" +
         * PrintHelper.moveCursorRelative(-1, -6); rep += "│" + drawStudent(2) + "  " + drawStudent(3) + "│" + PrintHelper.moveCursorRelative(-1, -6);
         * rep += "╰────╯";
         */

        rep += GamePieces.TOP_LEFT_CORNER_CURVE + "────" + GamePieces.TOP_RIGHT_CORNER_CURVE + PrintHelper.moveCursorRelative(-1, -6);
        rep += "│" + drawStudent(0) + "  " + drawStudent(1) + "│" + PrintHelper.moveCursorRelative(-1, -6);
        rep += "│" + drawStudent(2) + "  " + drawStudent(3) + "│" + PrintHelper.moveCursorRelative(-1, -6);
        rep += GamePieces.BOTTOM_LEFT_CORNER_CURVE + "────" + GamePieces.BOTTOM_RIGHT_CORNER_CURVE;

        return rep;
    }
}
