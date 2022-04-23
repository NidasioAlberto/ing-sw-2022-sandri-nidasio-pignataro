package it.polimi.ingsw.network;

import java.util.List;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.messages.ActionMessage;

public class Match
{
    private List<PlayerConnection> players;

    private Controller gameController;

    public void addPlayer(PlayerConnection player)
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

    public void actionCall(ActionMessage message, PlayerConnection player)
    {
        gameController.performAction(message, player.getPlayerName());
    }
}
