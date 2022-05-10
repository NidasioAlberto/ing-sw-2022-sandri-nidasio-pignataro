package it.polimi.ingsw.protocol.updates;

import it.polimi.ingsw.model.AssistantCard;

import java.util.List;

public class AssistantCardsUpdate extends ModelUpdate {
    /**
     * List of a single player assistant cards
     */
    private List<AssistantCard> cards;

    public List<AssistantCard> getCards() {
        return cards;
    }

    /**
     * Constructor that allows the player destination
     * 
     * @param playerDestination Name of the player that has to receive the message
     * @param cards             Collection of specific player cards
     */
    public AssistantCardsUpdate(String playerDestination, List<AssistantCard> cards) {
        super(playerDestination);

        if (cards == null)
            throw new NullPointerException("[AssistantCardsUpdate] Null cards list");
        // It is possible according to Java 8 documentation
        if (cards.contains(null))
            throw new NullPointerException(
                    "[AssistantCardsUpdate] Null assistant card inside the list");

        this.cards = cards;
    }

    /**
     * Constructor that doesn't allow any player destination
     * 
     * @param cards Collection of specific player cards
     */
    public AssistantCardsUpdate(List<AssistantCard> cards) {
        super();

        if (cards == null)
            throw new NullPointerException("[AssistantCardsUpdate] Null cards list");
        // It is possible according to Java 8 documentation
        if (cards.contains(null))
            throw new NullPointerException(
                    "[AssistantCardsUpdate] Null assistant card inside the list");

        this.cards = cards;
    }

    @Override
    public void handleUpdate(Object handler) {

    }
}
