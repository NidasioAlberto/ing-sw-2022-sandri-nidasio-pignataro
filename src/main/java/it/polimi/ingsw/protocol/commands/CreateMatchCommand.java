package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.network.PlayerConnection;

import java.io.Serial;

/**
 * Command used to create and join a match.
 */
public class CreateMatchCommand extends Command
{
    @Serial
    private static final long serialVersionUID = -3543453577305970257L;

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

        // Check if the player is in another game
        if (connection.isInAMatch())
            throw new Exception("The player is already in a match");

        // Create a match and add a player to it
        connection.getServer().createMatch(matchId, playersNumber, gameMode, connection);
    }
}
