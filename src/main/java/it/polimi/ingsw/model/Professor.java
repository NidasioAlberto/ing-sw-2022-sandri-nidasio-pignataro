package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * This class represents the professor pawn. Each colored professor is contended between the players
 * inside their school boards. It is one of the three main elements of the game along with students
 * and towers.
 */
public class Professor implements Serializable
{
    private SchoolColor color;

    /**
     * Constructor.
     * 
     * @param color The pawn color.
     * @throws NullPointerException Thrown if the specified color is invalid.
     */
    public Professor(SchoolColor color) throws NullPointerException
    {
        if (color == null)
            throw new NullPointerException("[Professor] A null color was provided");

        this.color = color;
    }

    public SchoolColor getColor()
    {
        return color;
    }
}
