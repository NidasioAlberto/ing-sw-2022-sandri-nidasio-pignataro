package it.polimi.ingsw.model;

/**
 * This class represents the student disc. It can be assigned to islands
 * and boards and represents one of the three main elements of the game with
 * professors and towers
 */
public class Student
{
    /**
     * The disc color (enum)
     */
    private SchoolColor color;

    /**
     * Constructor
     * @param color the disc color
     */
    public Student(SchoolColor color)
    {
        this.color = color;
    }

    /**
     * Disc color getter
     * @return The SchoolColor of the disc
     */
    public SchoolColor getColor() { return color; }
}
