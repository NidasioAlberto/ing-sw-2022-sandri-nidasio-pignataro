package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;

import java.util.NoSuchElementException;

/**
 * Character card Grandma Herbs. Effect: Place a No Entry tile on an Island of your choice. The
 * first time Mother Nature ends her movement there, put the No Entry Tile back onto this card DO
 * NOT calculate influence on that Island, or place any Towers.
 * IMPORTANT: THIS CARD IS THE ONLY ONE THAT IS ALWAYS REPLACING A METHOD: computeInfluence, TO MONITOR IF A NO ENTRY TILE
 * MUST RETURN TO THE CARD ITSELF.
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
        //Initial noEntryTiles
        this.noEntryTiles = 4;
    }

    @Override
    public boolean isPlayable()
    {
        // If there aren't noEntryTiles the card isn't playable
        if (noEntryTiles <= 0)
            return false;

        // Otherwise, this card can be played every time
        return true;
    }

    @Override
    public boolean isValidAction(ExpertGameAction action)
    {
        // If active I accept only MOVE_NO_ENTRY_FROM_CHARACTER_CARD_TO_ISLAND
        return action == ExpertGameAction.MOVE_NO_ENTRY_FROM_CHARACTER_CARD_TO_ISLAND;
    }

    @Override
    public void applyAction()
    {
        // If the card is not currently activated I do nothing
        if(!activated)
            return;

        // If there aren't noEntryTiles an exception is thrown
        if (noEntryTiles <= 0)
            throw new NoSuchElementException("[GrandmaHerbs] There are no more noEntryTiles");

        //Put the no entry tile on the selected island
        int island = instance.getSelectedPlayer().orElseThrow(
                () -> new NoSuchElementException("[GrandmaHerbs] No selected player")
        ).getSelectedIsland().orElseThrow(
                () -> new NoSuchElementException("[GrandmaHerbs] No selected island")
        );

        //Add the noEntryTile to the selected island
        instance.islands.get(island).addNoEntryTile();

        //Remove the noEntryTile from the card
        noEntryTiles--;

        //Then disable the card
        deactivate();
    }

    @Override
    public void computeInfluence(int island)
    {
        //I check if the index is correct
        if(island < 0 || island >= instance.islands.size())
            throw new IndexOutOfBoundsException("[GrandmaHerbs] island index out of bounds");

        //I check if on the island there is a no entry tile
        if(instance.islands.get(island).getNoEntryTiles() > 0)
        {
            //If so I return it back to the card and remove it from the island
            noEntryTiles++;
            instance.islands.get(island).removeNoEntryTile();

            //The influence is not calculated
            return;
        }

        //Then I compute the normal influence
        instance.computeInfluence(island);
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.GRANDMA_HERBS;
    }

    public int getNoEntryTiles()
    {
        return noEntryTiles;
    }
}
