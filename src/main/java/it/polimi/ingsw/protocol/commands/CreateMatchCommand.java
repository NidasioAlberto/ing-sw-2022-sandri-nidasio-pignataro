package it.polimi.ingsw.protocol.commands;

import org.json.JSONException;
import org.json.JSONObject;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.network.PlayerConnection;

public class CreateMatchCommand extends Command
{
    String matchId;

    int playersNumber;

    GameMode gameMode;

    CreateMatchCommand(JSONObject commandJson) throws JSONException
    {
        matchId = commandJson.getString("matchId");
        playersNumber = commandJson.getInt("playersNumber");
        gameMode = GameMode.valueOf(commandJson.getString("gameMode"));
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
