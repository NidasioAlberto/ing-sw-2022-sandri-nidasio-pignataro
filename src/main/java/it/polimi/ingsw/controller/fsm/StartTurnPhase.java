package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.BaseGameAction;

public class StartTurnPhase implements Phase
{
    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // On valid action i only have to switch to the student movement phase
        handler.setGamePhase(new MoveStudentPhase());
    }

    @Override
    public void onEndGame(GameActionHandler handler)
    {
        // I the game ends i have only to switch to end game phase
        handler.setGamePhase(new EndGamePhase());
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        // Check if the player corresponds to the selected one in SORTED LIST
        Player currentPlayer = handler.getGame().getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[StartTurnPhase]"));

        // I only accept if and only if the player is the correct one
        // and the action type is a student movement
        return currentPlayer.getNickname().equals(playerName)
                && (baseAction == BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING
                        || baseAction == BaseGameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND
                        || baseAction == BaseGameAction.PLAY_CHARACTER_CARD
                        || baseAction == BaseGameAction.CHARACTER_CARD_ACTION);
    }
}
