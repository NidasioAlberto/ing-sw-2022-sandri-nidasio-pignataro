package it.polimi.ingsw.network;

import java.util.*;
import it.polimi.ingsw.controller.messages.ActionMessage;
import it.polimi.ingsw.model.GameMode;

public class Server
{
    private Map<String, Match> matches;

    private List<PlayerConnection> lobby;

    public void createMatch(String name, int playersNumber, GameMode mode)
    {
        // ...
    }

    public Match getMatch(String name)
    {
        return new Match();
    }

    public Set<String> getMatches()
    {
        return matches.keySet();
    }

    public void quitGame(PlayerConnection player)
    {
        // ...
    }

    public void actionCall(ActionMessage message, PlayerConnection player)
    {
        // ...
    }
}
