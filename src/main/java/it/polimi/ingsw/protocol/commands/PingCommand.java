package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.network.PlayerConnection;

public class PingCommand extends Command
{
    @Override
    public void applyCommand(PlayerConnection connection) throws Exception
    {
        connection.restartWatchdog();
    }
}
