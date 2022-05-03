package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.network.PlayerConnection;

public class JoinMatchCommand extends Command
{
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
    }
}
