package it.polimi.ingsw.model;

/**
 * Enumeration of the all possible game actions in the game. Includes also the character cards.
 * 
 * IMPORTANT: THIS ACTIONS MUST BE ORDERED LIKE THE GAME FLOW
 */
public enum ExpertGameAction
{
    /**
     * Action that pretends to be something about a non-expert game
     */
    BASE_ACTION("Base action"),

    /**
     * CHARACTER CARDS SECTION
     */
    MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND("Move student from character card to island"),
    SELECT_ISLAND("Select island"),
    MOVE_NO_ENTRY_FROM_CHARACTER_CARD_TO_ISLAND("Move no entry tile from character card to island"),
    SWAP_STUDENT_FROM_ENTRANCE_TO_CHARACTER_CARD("swap student from entrance to character card"),
    SELECT_COLOR("Select color"),
    SWAP_STUDENT_FROM_ENTRANCE_TO_DINING("Swap student from entrance to dining"),
    MOVE_STUDENT_FROM_CHARACTER_CARD_TO_DINING("Move student from character card to dining");

    private String str;

    private ExpertGameAction(String str)
    {
        this.str = str;
    }

    @Override
    public String toString()
    {
        return str;
    }
}
