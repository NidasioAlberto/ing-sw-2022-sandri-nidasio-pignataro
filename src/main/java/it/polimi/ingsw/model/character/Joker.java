package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Character card Joker. Effect:
 * You may take up to 3 Students from this card
 * and replace them with the same number of Students
 * from your Entrance.
 */
public class Joker extends CharacterCard
{
    /**
     * This class has a student list
     */
    private List<Student> students;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Joker(Game game) throws NullPointerException
    {
        super(game);

        //Joker's cost
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
    public CharacterCardType getCardType() { return CharacterCardType.JOKER; }
}
