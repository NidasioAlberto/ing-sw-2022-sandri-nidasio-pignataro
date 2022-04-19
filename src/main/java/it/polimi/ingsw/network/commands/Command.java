package it.polimi.ingsw.network.commands;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Command
{
    /**
     * Factory method to generate a command from a given string containing a JSON object.
     * 
     * @param commandString JSON string.
     * @return Appropriately created command base on the string content.
     * @throws JSONException Thrown if a parsing error occurs.
     * @throws IllegalArgumentException Thrown if the command was not recognized.
     */
    static public Command buildCommand(String commandString)
            throws JSONException, IllegalArgumentException
    {
        JSONObject commandJson = new JSONObject(commandString).getJSONObject("command");

        String commandId = commandJson.getString("commandId");

        switch (commandId)
        {
            case "CreateMatch":
                return new CreateMatchCommand(commandJson);
            case "GetMatchesList":
                return new GetMatchesListCommand(commandJson);
            case "JoinMatch":
                return new JoinMatchCommand(commandJson);
            case "QuitGame":
                return new QuitGameCommand(commandJson);
            case "SetName":
                return new SetNameCommand(commandJson);
        }

        throw new IllegalArgumentException("[Commands] unrecognized command " + commandId);
    }
}
