package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.exceptions.WrongPlayerException;

import java.util.ArrayList;
import java.util.List;

public class PlanPhase implements Phase
{
    /**
     * We need to maintain some counting about the players that already played an assistant card
     */
    private int count;

    /**
     * Index of the player who played the first turn the round before
     */
    private int bestPlayerIndex;

    public PlanPhase()
    {
        count = 0;
        bestPlayerIndex = 0;
    }

    @Override
    public void onValidAction(GameActionHandler handler)
    {
        // I take the selected player index. If it is the last one i can
        // change the selected player referring to the sorted list and switch
        // to the startTurnPhase
        int playerIndex = handler.getGame().getSelectedPlayerIndex()
                .orElseThrow(() -> new NoSelectedPlayerException("[PlanPhase]"));

        // The first time I enter in plan phase I save the active player who played
        // the first turn the previous round
        if (count == 0)
        {
            bestPlayerIndex = playerIndex;
        }

        // List of active players that haven't played this turn yet
        List<Player> playersToPlay = new ArrayList<>();

        // Search and add the active players to the list
        int i = (playerIndex + 1) % handler.getGame().getPlayersNumber();
        while(i != bestPlayerIndex)
        {
            if (handler.getGame().getPlayerTableList().get(i).isActive())
                playersToPlay.add(handler.getGame().getPlayerTableList().get(i));
            i = (i + 1) % handler.getGame().getPlayersNumber();
        }

        // If we are not dealing with the last active one i switch player and maintain the current state
        if (playersToPlay.size() > 0)
        {
            // I select the next active player
            handler.getGame().selectPlayer(handler.getGame().getPlayerTableList().indexOf(playersToPlay.get(0)));
            handler.getGame().setCurrentPlayerIndexByTable(handler.getGame().getPlayerTableList().indexOf(playersToPlay.get(0)));
            count++;

        }
        else
        {
            // If not i select the first player (now about the sorted list) who is active
            for (int j = 0; i < handler.getGame().getPlayersNumber(); j++)
            {
                if (handler.getGame().getSortedPlayerList().get(j).isActive())
                {
                    handler.getGame().selectPlayer(j);
                    handler.getGame().setCurrentPlayerIndexByTable(handler.getGame().getPlayerTableList().
                            indexOf(handler.getGame().getSelectedPlayer().get()));
                    handler.setGamePhase(new MoveStudentPhase());
                    break;
                }
            }
        }
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
            BaseGameAction baseAction)
    {
        // I check if it is the allowed player from the initial passed list
        int playerIndex = handler.getGame().getSelectedPlayerIndex()
                .orElseThrow(() -> new NoSelectedPlayerException("[PlanPhase]"));

        // I accept the action if and only if the player is correct and the action is an assistant
        // card play
        if (!handler.getGame().getPlayerTableList().get(playerIndex).getNickname()
                .equals(playerName))
            throw new WrongPlayerException();
        return baseAction == BaseGameAction.PLAY_ASSISTANT_CARD;
    }

    public int getCount()
    {
        return count;
    }
}
