package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.game.CharacterCard;

import java.util.List;

public class CharacterCardsUpdate extends ModelUpdate
{
    /**
     * List of all the character cards
     */
    private List<CharacterCard> cards;

    /**
     * Constructor
     * 
     * @param cards Collection of all the character cards
     */
    public CharacterCardsUpdate(List<CharacterCard> cards)
    {
        if (cards == null)
            throw new NullPointerException("[CharacterCardsUpdate] Null cards list");
        // It is possible according to Java 8 documentation
        if (cards.contains(null))
            throw new NullPointerException("[CharacterCardsUpdate] Null card inside the list");

        this.cards = cards;
    }

    public List<CharacterCard> getCards()
    {
        return cards;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }
}
