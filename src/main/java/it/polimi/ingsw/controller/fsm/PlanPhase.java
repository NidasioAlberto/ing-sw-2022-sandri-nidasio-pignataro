package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;

import java.util.NoSuchElementException;

public class PlanPhase implements Phase
{
    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // I take the selected player index. If it is the last one i can
        // change the selected player referring to the sorted list and switch
        // to the startTurnPhase
        int playerIndex = handler.getGame().getSelectedPlayerIndex().orElseThrow(
                () -> new NoSelectedPlayerException("[PlanPhase] No such selected player, is the GameActionHandler instantiated?"));
    }

    @Override
    public void onEndGame(GameActionHandler handler)
    {

    }

    @Override
    public boolean isLegitAction(ActionMessage message, GameActionHandler handler)
    {
        return false;
    }
}
