package it.polimi.ingsw.network.commands;

import org.json.JSONException;
import org.json.JSONObject;
import it.polimi.ingsw.network.PlayerConnection;

public class SetNameCommand extends Command
{
    String playerName;

    SetNameCommand(JSONObject commandJson) throws JSONException
    {
        playerName = commandJson.getString("playerName");
    }

    public void applyCommand(PlayerConnection connection) throws NullPointerException
    {
        connection.setPlayerName(playerName);
    }
}
