package it.polimi.ingsw.network;

import java.util.List;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.protocol.answers.Answer;
import it.polimi.ingsw.protocol.answers.EndMatchAnswer;
import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.model.GameMode;

public class Match
{
    private List<PlayerConnection> players;

    private Controller gameController;

    Match(int playersNumber, GameMode mode)
    {
        // TODO
    }

    public void addPlayer(PlayerConnection player)
    {
        // TODO
    }

    public void removePlayer(PlayerConnection player)
    {
        // TODO
    }

    public void sendErrorMessage(String player, String message)
    {
        // Create and ErrorAnswer
    }

    public void sendAllErrorMessage(String message)
    {
        // Create and ErrorAnswer
    }

    public void sendAllAnswer(Answer command)
    {
        // TODO
    }

    public void sendError(String player, String message)
    {
        // TODO
    }

    public void endMatch(String message)
    {
        sendAllAnswer(new EndMatchAnswer(message));
        // TODO: Remove all players from the match
    }

    public void applyAction(ActionMessage action, PlayerConnection player)
    {
        gameController.performAction(action, player.getPlayerName().get());
    }

    public int getPlayersNumber()
    {
        return players.size();
    }

    public Controller getController()
    {
        return gameController;
    }
}
