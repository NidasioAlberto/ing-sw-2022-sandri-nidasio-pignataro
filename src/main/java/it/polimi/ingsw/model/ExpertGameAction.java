package it.polimi.ingsw.model;

/**
 * Enumeration of the all possible game actions in the game. Includes also the character cards.
 * 
 * IMPORTANT: THIS ACTIONS MUST BE ORDERED LIKE THE GAME FLOW
 */
public enum ExpertGameAction
{
    /**
     * Action that pretends to be something about a non-expert game.
     */
    BASE_ACTION("Base action"),

    /**
     * Moves the selected student from the character card to the selected island.
     */
    MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND("Move student from character card to island"),

    /**
     * Selects an island for the other actions.
     */
    SELECT_ISLAND("Select island"),

    /**
     * Moves a no entry pawn from the character card to the selected island.
     */
    MOVE_NO_ENTRY_FROM_CHARACTER_CARD_TO_ISLAND("Move no entry tile from character card to island"),

    /**
     * Swaps the selected student from the entrance to the card.
     */
    SWAP_STUDENT_FROM_ENTRANCE_TO_CHARACTER_CARD("Swap student from entrance to character card"),

    /**
     * Selects a student colors.
     */
    SELECT_COLOR("Select color"),

    /**
     * Swaps the selected student from the entrance to the dining room.
     */
    SWAP_STUDENT_FROM_ENTRANCE_TO_DINING("Swap student from entrance to dining"),

    /**
     * Moves the selected student form the character card to the dining room.
     */
    MOVE_STUDENT_FROM_CHARACTER_CARD_TO_DINING("Move student from character card to dining");

    private String str;

    /**
     * Creates an ExpertGameAction object.
     * 
     * @param str The action in string from.
     */
    private ExpertGameAction(String str)
    {
        this.str = str;
    }

    /**
     * Returns the action string representation.
     * 
     * @return The action string representation.
     */
    @Override
    public String toString()
    {
        return str;
    }
}
