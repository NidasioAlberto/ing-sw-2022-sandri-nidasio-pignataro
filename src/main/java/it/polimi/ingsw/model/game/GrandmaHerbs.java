package it.polimi.ingsw.model.game;

import it.polimi.ingsw.client.cli.utils.GamePieces;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.exceptions.NoMoreNoEntryTilesException;
import it.polimi.ingsw.model.exceptions.NoSelectedIslandException;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.protocol.updates.CharacterCardPayloadUpdate;

/**
 * Character card Grandma Herbs. Effect: Place a No Entry tile on an Island of your choice. The first time Mother Nature ends her movement there, put
 * the No Entry Tile back onto this card DO NOT calculate influence on that Island, or place any Towers. IMPORTANT: THIS CARD IS THE ONLY ONE THAT IS
 * ALWAYS REPLACING A METHOD: computeInfluence, TO MONITOR IF A NO ENTRY TILE MUST RETURN TO THE CARD ITSELF.
 */
public class GrandmaHerbs extends CharacterCard
{
    public static int INITIAL_NO_ENTRY_NUMBER = 4;

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
        // Initial noEntryTiles
        this.noEntryTiles = INITIAL_NO_ENTRY_NUMBER;
    }

    @Override
    public boolean isPlayable()
    {
        // Before checking how many entry tiles are on this card, we check
        // how many tiles are not on the card
        int count = 0;
        for (Island island : instance.islands)
            count += island.getNoEntryTiles();

        // Update the counter
        noEntryTiles = INITIAL_NO_ENTRY_NUMBER - count;

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
        if (!activated)
            return;

        // If there aren't noEntryTiles an exception is thrown
        if (noEntryTiles <= 0)
            throw new NoMoreNoEntryTilesException("[GrandmaHerbs]");

        // Put the no entry tile on the selected island
        int island = instance.getSelectedPlayer().orElseThrow(() -> new NoSelectedPlayerException("[GrandmaHerbs]")).getSelectedIsland()
                .orElseThrow(() -> new NoSelectedIslandException("[GrandmaHerbs]"));

        // Add the noEntryTile to the selected island
        instance.islands.get(island).addNoEntryTile();

        // Remove the noEntryTile from the card
        noEntryTiles--;

        // Then disable the card
        deactivate();

        // Notify the subscriber
        notifySubscriber();
    }

    @Override
    public void notifySubscriber()
    {
        // I have to find this character card index inside the list
        int index = 0;
        for (index = 0; index < instance.characterCards.size() && this != instance.characterCards.get(index); index++);

        // Before the notification i update also the number of no entry tiles
        int count = 0;
        for (Island island : instance.islands)
            count += island.getNoEntryTiles();

        // Update the counter
        noEntryTiles = INITIAL_NO_ENTRY_NUMBER - count;

        if (instance.subscriber.isPresent())
            instance.subscriber.get().onNext(new CharacterCardPayloadUpdate(index, noEntryTiles));
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

    @Override
    public String toString()
    {
        String rep = super.toString();

        rep += PrintHelper.moveCursorRelative(2, -6) + noEntryTiles + GamePieces.NO_ENTRY_TILE;
        rep += PrintHelper.moveCursorRelative(-2, 4);

        return rep;
    }
}
