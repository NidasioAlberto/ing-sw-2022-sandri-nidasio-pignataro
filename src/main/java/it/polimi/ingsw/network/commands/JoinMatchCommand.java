package it.polimi.ingsw.network.commands;

import org.json.JSONException;
import org.json.JSONObject;
import it.polimi.ingsw.network.PlayerConnection;

public class JoinMatchCommand extends Command
{
    String matchId;

    JoinMatchCommand(JSONObject commandJson) throws JSONException
    {
        matchId = commandJson.getString("matchId");
    }

    public void applyCommand(PlayerConnection connection) throws IllegalArgumentException
    {
        // Check if the player has a name
        if (connection.getPlayerName().isPresent())
            connection.getServer().addPlayerToMatch(matchId, connection);
        // else
        // TODO: Notify the player
    }
}
