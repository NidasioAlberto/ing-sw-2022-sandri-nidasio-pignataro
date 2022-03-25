package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Character card Monk. Effect:
 * Take 1 Student from this card and place it on an island
 * of your choice. Then, draw a new Student from the bag
 * and place it on this card
 */
public class Monk extends CharacterCard
{
    /**
     * Payload of this card is students that the player selects
     * TODO DISCUSS THIS THING, BECAUSE WE NEED TO DEFINE HOW THE MOVE OF STUDENT IS DONE
     */
    private List<Student> students;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    public Monk(Game game) throws NullPointerException
    {
        super(game);

        //Monk's cost
        this.cost = 1;

        //Instance the list
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
    public CharacterCardType getCardType() { return CharacterCardType.MONK; }
}
