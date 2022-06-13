package it.polimi.ingsw.model;

/**
 * Represents the actions that the basic FSM game can understand, accept and/or reject
 */
public enum BaseGameAction
{
    /**
     * Plays an assistant card.
     */
    PLAY_ASSISTANT_CARD,

    /**
     * Moves a student from the entrance to an island.
     */
    MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND,

    /**
     * Moves a student from the entrance to the dining room.
     */
    MOVE_STUDENT_FROM_ENTRANCE_TO_DINING,

    /**
     * Moves mother nature to currently selected island.
     */
    MOVE_MOTHER_NATURE,

    /**
     * Selects a cloud tile.
     */
    SELECT_CLOUD_TILE,

    /**
     * Plays a character card.
     */
    PLAY_CHARACTER_CARD,

    /**
     * Plays a character card's action.
     */
    CHARACTER_CARD_ACTION,

    /**
     * Ends the current player's turn.
     */
    END_TURN
}
