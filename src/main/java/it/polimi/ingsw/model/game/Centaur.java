package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.GameAction;
import it.polimi.ingsw.model.Island;
import it.polimi.ingsw.model.Player;

import java.util.NoSuchElementException;

/**
 * Character card Centaur. Effect: When resolving a computeInfluence on an Island, Towers do not
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
        // This card is playable only before the movement of mother nature
        return !instance.motherNatureMoved;
    }

    @Override
    public boolean isValidAction(GameAction action)
    {
        //TODO conviene far si che il controller alla fine del turno di ogni giocatore disattivi
        // tutte le carte cosi non serve che le carte si disattivino da sole

        // If the card is not activated the action isn't valid
        if(!activated)
        {
            return false;
        }

        // I don't have to intercept any action
        return true;

    }

    @Override
    public void applyAction()
    {}

    @Override
    public int computePlayerInfluence(Player player, int island)
            throws NoSuchElementException, IndexOutOfBoundsException
    {
        // If the card is activated i apply the effect of this method
        if (!activated)
            return instance.computePlayerInfluence(player, island);

        if (island < 0 || island >= instance.islands.size())
            throw new IndexOutOfBoundsException("[Centaur] island index out of bounds");

        if (player == null)
            throw new NullPointerException("[Centaur] player null");

        Island currentIsland = instance.islands.get(island);

        // Compute the influence of this player from students
        int influence = player.getBoard().getProfessors().stream()
                .map(p -> currentIsland.getStudentsByColor(p.getColor())).reduce(0, Integer::sum);

        return influence;
    }

    @Override
    public CharacterCardType getCardType()
    {
        return CharacterCardType.CENTAUR;
    }
}
