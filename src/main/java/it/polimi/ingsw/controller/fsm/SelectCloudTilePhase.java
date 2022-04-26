package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;

public class SelectCloudTilePhase implements Phase
{
    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // If the action is valid i have to switch current player
        // and set the current state to moveStudentPhase unless the
        // current player is the last one. In that case i have to
        // switch to PlanPhase
        int playerIndex = handler.getGame().getSelectedPlayerIndex().get();

        if(playerIndex < handler.getGame().getPlayerTableList().size() - 1)
        {
            handler.getGame().selectPlayer(playerIndex + 1);
            handler.setGamePhase(new MoveStudentPhase());
        }
        else
        {
            handler.getGame().selectPlayer(0);
            // Now that the turn is over i want the plan phase to go with game order
            handler.setGamePhase(new PlanPhase(handler.getGame().getSortedPlayerList()));
        }
    }

    @Override
    public void onEndGame(GameActionHandler handler)
    {
        // On end game i switch to the corresponding state
        handler.setGamePhase(new EndGamePhase());
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        // Check if the player corresponds to the selected one in SORTED LIST
        Player currentPlayer = handler.getGame().getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[SelectCloudTilePhase]"));

        return currentPlayer.getNickname().equals(playerName) &&
                (baseAction == BaseGameAction.SELECT_CLOUD_TILE ||
                 baseAction == BaseGameAction.CHARACTER_CARD_ACTION ||
                 baseAction == BaseGameAction.PLAY_CHARACTER_CARD);
    }
}
