package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.network.PlayerConnection;

/**
 * Command used to create and join a match.
 */
public class CreateMatchCommand extends Command
{
    String matchId;

    int playersNumber;

    GameMode gameMode;

    public CreateMatchCommand(String matchId, int playersNumber, GameMode gameMode)
    {
        this.matchId = matchId;
        this.playersNumber = playersNumber;
        this.gameMode = gameMode;
    }

    public void applyCommand(PlayerConnection connection) throws Exception
    {
        checkPlayerConnection(connection);

        // Check if the player has a name
        if (!connection.getPlayerName().isPresent())
            throw new Exception("A match can't be created until the username has been configured");

        // Create a match and add a player to it
        connection.getServer().createMatch(matchId, playersNumber, gameMode, connection);
    }
}
