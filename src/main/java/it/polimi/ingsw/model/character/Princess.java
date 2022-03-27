package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Character card Princess. Effect: Take 1 Student from this card and place it in your Dining Room.
 * Then, draw a new Student from the Bag and place it on this card.
 */
public class Princess extends CharacterCard
{
    /**
     * This card has a list of students as payload
     */
    private List<Student> students;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Princess(Game game) throws NullPointerException
    {
        super(game);

        // Princess' cost
        this.cost = 2;

        // Instance the list
        students = new ArrayList<Student>();
    }

    @Override
    public boolean isPlayable()
    {
        return false;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        return false;
    }

    @Override
    public Game applyAction()
    {
        return this;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.PRINCESS;
    }
}
