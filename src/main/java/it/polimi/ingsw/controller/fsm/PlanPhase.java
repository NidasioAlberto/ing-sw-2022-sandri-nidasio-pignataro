package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.BaseGameAction;

public class PlanPhase implements Phase
{
    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // I take the selected player index. If it is the last one i can
        // change the selected player referring to the sorted list and switch
        // to the startTurnPhase
        int playerIndex = handler.getGame().getSelectedPlayerIndex()
                .orElseThrow(() -> new NoSelectedPlayerException(
                        "[PlanPhase] No such selected player, is the GameActionHandler instantiated?"));

        // If we are not dealing with the last one i switch player and maintain the current state
        if (playerIndex < handler.getGame().getPlayerTableList().size() - 1)
        {
            handler.getGame().selectPlayer(playerIndex + 1);
        } else
        {
            // If not i select the first player (now about the sorted list)
            handler.getGame().selectPlayer(0);
            handler.setGamePhase(new StartTurnPhase());
        }
    }

    @Override
    public void onEndGame(GameActionHandler handler)
    {
        // On this event i just switch to the end state
        handler.setGamePhase(new EndGamePhase());
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        // I check if it is the allowed player FROM THE TABLE LIST
        int playerIndex = handler.getGame().getSelectedPlayerIndex()
                .orElseThrow(() -> new NoSelectedPlayerException(
                        "[PlanPhase] No such selected player, is the GameActionHandler instantiated?"));

        // I accept the action if and only if the player is correct and the action is an assistant
        // card play
        return handler.getGame().getPlayerTableList().get(playerIndex).getNickname()
                .equals(playerName) && baseAction == BaseGameAction.PLAY_ASSISTANT_CARD;
    }
}
