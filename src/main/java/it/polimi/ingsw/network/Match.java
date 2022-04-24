package it.polimi.ingsw.network;

import java.util.List;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.model.GameMode;

public class Match
{
    private List<PlayerConnection> players;

    private Controller gameController;

    Match(int playersNumber, GameMode mode)
    {
        // ...
    }

    public void addPlayer(PlayerConnection player)
    {
        // ...
    }

    public void removePlayer(PlayerConnection player)
    {
        // ...
    }

    public void sendMessage(String player, String message)
    {
        // ...
    }

    public void sendAllMessage(String message)
    {
        // ...
    }

    public void actionCall(ActionMessage action, PlayerConnection player)
    {
        gameController.performAction(action, player.getPlayerName());
    }
}
