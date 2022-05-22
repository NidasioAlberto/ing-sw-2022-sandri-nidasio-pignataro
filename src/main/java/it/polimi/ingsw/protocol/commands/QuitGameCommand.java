package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.network.PlayerConnection;

public class QuitGameCommand extends Command
{
    public void applyCommand(PlayerConnection connection) throws Exception
    {
        checkPlayerConnection(connection);

        connection.getServer().removePlayerFromMatch(connection);

        connection.close();
    }
}
