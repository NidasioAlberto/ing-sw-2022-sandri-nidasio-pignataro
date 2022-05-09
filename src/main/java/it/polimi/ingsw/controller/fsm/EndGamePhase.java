package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

public class EndGamePhase implements Phase
{
    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // Nothing is done in the game end state
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        // When the game is at the end, no action can be done
        return false;
    }
}
