package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.AssistantCard;

import java.util.List;

public class AssistantCardsUpdate extends ModelUpdate
{
    /**
     * List of a single player assistant cards
     */
    private List<AssistantCard> cards;

    /**
     * Constructor
     * 
     * @param cards Collection of specific player cards
     */
    public AssistantCardsUpdate(List<AssistantCard> cards)
    {
        if (cards == null)
            throw new NullPointerException("[AssistantCardsUpdate] Null cards list");
        // It is possible according to Java 8 documentation
        if (cards.contains(null))
            throw new NullPointerException(
                    "[AssistantCardsUpdate] Null assistant card inside the list");

        this.cards = cards;
    }

    public List<AssistantCard> getCards()
    {
        return cards;
    }

    @Override
    public void handleUpdate(Object handler)
    {

    }
}
