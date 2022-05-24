package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.network.PlayerConnection;

import java.io.Serial;

public class JoinMatchCommand extends Command
{
    @Serial
    private static final long serialVersionUID = -6556916209678345591L;

    String matchId;

    public JoinMatchCommand(String matchId)
    {
        this.matchId = matchId;
    }

    public void applyCommand(PlayerConnection connection) throws Exception
    {
        checkPlayerConnection(connection);

        // Check if the player has a name
        if (connection.getPlayerName().isPresent())
            connection.getServer().addPlayerToMatch(matchId, connection);
        else throw new IllegalArgumentException("A match can't be created until the username has been configured");
    }
}
