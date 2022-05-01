package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.network.PlayerConnection;

public class CreateMatchCommand extends Command
{
    String matchId;

    GameMode gameMode;

    int playersNumber;

    public CreateMatchCommand(String matchId, GameMode gameMode)
    {
        this.matchId = matchId;
        this.gameMode = gameMode;
        playersNumber = 0;
    }

    public void applyCommand(PlayerConnection connection) throws IllegalArgumentException
    {
        // Check if the player has a name
        if (connection.getPlayerName().isPresent())
        {
            connection.getServer().createMatch(matchId, playersNumber, gameMode);
            connection.getServer().addPlayerToMatch(matchId, connection);
        }
        // else
        // TODO: Notify the player
    }
}
