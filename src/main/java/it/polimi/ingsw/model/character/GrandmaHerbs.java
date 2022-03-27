package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameAction;

/**
 * Character card Grandma Herbs. Effect: Place a No Entry tile on an Island of your choice. The
 * first time Mother Nature ends her movement there, put the No Entry Tile back onto this card DO
 * NOT calculate influence on that Island, or place any Towers.
 */
public class GrandmaHerbs extends CharacterCard
{
    /**
     * No entry tiles assigned to this card
     */
    private int noEntryTiles;

    /**
     * Constructor
     *
     * @param game the game instance to be decorated
     * @throws NullPointerException in case of a null decorated game instance
     */
    GrandmaHerbs(Game game) throws NullPointerException
    {
        super(game);

        // GrandmaHerbs cost
        this.cost = 2;
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

    // TODO ADD THIS TO UML. THE METHOD computeInfluence ALREADY VERIFIES IF THERE IS A NO ENTRY
    // TILE
    // BUT THIS TILE HAS TO RETURN TO THE CARD ONCE USED, SO THE CARD HAS TO CHECK THAT OPTION
    @Override
    public void computeInfluence()
    {

    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.GRANDMA_HERBS;
    }
}
