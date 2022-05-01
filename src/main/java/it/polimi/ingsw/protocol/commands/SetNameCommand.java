package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.network.PlayerConnection;

public class SetNameCommand extends Command
{
    String playerName;

    public SetNameCommand(String playerName)
    {
        this.playerName = playerName;
    }

    public void applyCommand(PlayerConnection connection) throws NullPointerException
    {
        connection.setPlayerName(playerName);
    }
}
