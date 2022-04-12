package it.polimi.ingsw.model;

/**
 * Enumeration of the all possible game actions in the game. Includes also
 * the character cards.
 * IMPORTANT: THIS ACTIONS MUST BE ORDERED LIKE THE GAME FLOW
 */
public enum ExpertGameAction
{
    /**
     * BASE ACTION: Action that pretends to be something about a non-expert game
     */
    ACTION_BASE,

    /**
     * CHARACTER CARDS SECTION
     */
    PLAY_CHARACTER_CARD,
    MOVE_STUDENT_FROM_CHARACTER_CARD_TO_ISLAND,
    SELECT_ISLAND,
    MOVE_NO_ENTRY_FROM_CHARACTER_CARD_TO_ISLAND,
    SWAP_STUDENT_FROM_CHARACTER_CARD_TO_ENTRANCE,
    SELECT_COLOR,
    SWAP_STUDENT_FROM_ENTRANCE_TO_DINING,
    MOVE_STUDENT_FROM_CHARACTER_CARD_TO_DINING;
}
