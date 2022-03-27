package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

/**
 * Character card Centaur. Effect: When resolving a computeInfluenceing on an Island, Towers do not
 * count towards influence.
 */
public class Centaur extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    Centaur(Game game) throws NullPointerException
    {
        super(game);

        // Centaur's cost
        this.cost = 3;
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
    public void computeInfluence()
    {

    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.CENTAUR;
    }
}
