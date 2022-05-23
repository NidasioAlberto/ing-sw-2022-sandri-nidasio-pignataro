package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.network.PlayerConnection;

import java.io.Serial;

public class QuitGameCommand extends Command
{
    @Serial
    private static final long serialVersionUID = -3501302576734728809L;

    public void applyCommand(PlayerConnection connection) throws Exception
    {
        checkPlayerConnection(connection);

        connection.getServer().removePlayerFromMatch(connection);

        connection.close();
    }
}
