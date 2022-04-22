package it.polimi.ingsw.model.game;

/**
 * Represents the actions that the basic FSM game can understand, accept and/or reject
 */
public enum BaseGameAction
{
    PLAY_ASSISTANT_CARD,
    MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND,
    MOVE_STUDENT_FROM_ENTRANCE_TO_DINING,
    MOVE_MOTHER_NATURE,
    SELECT_CLOUD_TILE,
    PLAY_CHARACTER_CARD,
    CHARACTER_CARD_ACTION,
    END_TURN
}
