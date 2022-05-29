package it.polimi.ingsw.controller.fsm;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;
import it.polimi.ingsw.model.exceptions.NoSelectedPlayerException;
import it.polimi.ingsw.model.exceptions.WrongPlayerException;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The game is in this phase when there is only one active player.
 * All the actions aren't legit.
 * When the game enters this phase a timeout starts, if it is not interrupted the only active
 * player is the winner, else the game goes back to the phase before this one.
 */
public class SuspendedPhase implements Phase
{

    private Phase previousPhase;

    private Future<?> timeout = null;

    public SuspendedPhase(Phase previousPhase, Controller controller)
    {
        this.previousPhase = previousPhase;

        timeout = Executors.newCachedThreadPool().submit(() -> {
        try
        {
            // 1 minute timeout
            Thread.sleep(60000);

            // The timeout wasn't interrupted so the game ends
            controller.endGame();
        } catch (InterruptedException e)
        {
            // The timeout was interrupted so the game continues from the previous phase
            onValidAction(controller.getGameHandler());
        }
        });
    }

    @Override
    public void onValidAction(GameActionHandler handler)
    {
       handler.setGamePhase(previousPhase);
    }

    @Override
    public boolean isLegitAction(GameActionHandler handler, String playerName,
                                 BaseGameAction baseAction)
    {
        // I check if it is the allowed player from the initial passed list
        int playerIndex = handler.getGame().getSelectedPlayerIndex()
                .orElseThrow(() -> new NoSelectedPlayerException("[PlanPhase]"));

        // If the player is not the selected one I throw an exception
        if (!handler.getGame().getPlayerTableList().get(playerIndex).getNickname()
                .equals(playerName))
            throw new WrongPlayerException();

        // There aren't legit actions in this phase
        return false;
    }

    public Future<?> getTimeout()
    {
        return timeout;
    }
}
