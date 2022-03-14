package it.polimi.ingsw.model;

/**
 * This class represents the professor disc. It can be contended between the players
 * inside their school boards. It is one of the three main elements of the game
 * with students and towers
 */
public class Professor
{
    /**
     * The pawn color (enum)
     */
    private SchoolColor color;

    /**
     * Constructor
     * @param color the pawn color
     */
    public Professor(SchoolColor color)
    {
        this.color = color == null ? SchoolColor.BLUE : color;
    }

    /**
     * Pawn color getter
     * @return The SchoolColor of the pawn
     */
    public SchoolColor getColor() { return color; }
}
