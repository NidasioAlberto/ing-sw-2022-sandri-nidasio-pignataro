package it.polimi.ingsw.model;

/**
 * This class represents the professor disc. It can be contended between the players
 * inside their school boards. It is one of the three main elements of the game
 * with students and towers
 */
public class Professor
{
    /**
     * The disc color (enum)
     */
    private SchoolColor color;

    /**
     * Constructor
     * @param color the disc color
     */
    public Professor(SchoolColor color)
    {
        this.color = color;
    }

    /**
     * Disc color getter
     * @return The SchoolColor of the disc
     */
    public SchoolColor getColor() { return color; }
}
