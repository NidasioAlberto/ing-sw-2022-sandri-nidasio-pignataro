package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

/**
 * Character card Mushroom man. Effect: Choose a color of Student; during the influence calculation
 * this turn, that color adds no influence.
 */
public class MushroomMan extends CharacterCard
{
    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    MushroomMan(Game game) throws NullPointerException
    {
        super(game);

        // MushroomMan's cost
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
    public void applyAction()
    {}

    @Override
    public void computeInfluence()
    {

    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.MUSHROOM_MAN;
    }
}
