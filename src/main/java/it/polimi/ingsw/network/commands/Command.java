package it.polimi.ingsw.network.commands;

import org.json.JSONObject;
import it.polimi.ingsw.network.PlayerConnection;

public abstract class Command
{
    /**
     * Factory method to generate a command from a given JSON object.
     * 
     * @param command JSON command object.
     * @return Appropriately created command base on the JSON content.
     * @throws IllegalArgumentException Thrown if the command was not recognized.
     */
    static public Command buildCommand(JSONObject command) throws IllegalArgumentException
    {
        String commandId = command.getString("commandId");

        if (commandId == CreateMatchCommand.class.getName())
            return new CreateMatchCommand(command);
        else if (commandId == GetMatchesListCommand.class.getName())
            return new GetMatchesListCommand();
        else if (commandId == JoinMatchCommand.class.getName())
            return new JoinMatchCommand(command);
        else if (commandId == QuitGameCommand.class.getName())
            return new QuitGameCommand();
        else if (commandId == SetNameCommand.class.getName())
            return new SetNameCommand(command);

        throw new IllegalArgumentException("[Command] Unrecognized command " + commandId);
    }

    /**
     * Checks if the given player connection is valid.
     * 
     * @throws NullPointerException Thrown if the handler is null.
     */
    public void checkPlayerConnection(PlayerConnection connection) throws NullPointerException
    {
        if (connection == null)
            throw new NullPointerException("[Command] Player connection is null");
    }

    /**
     * Method that has to be called to handle the command.
     * 
     * @param connection The player's connection used to apply the command.
     * @throws NullPointerException Thrown if handler is null.
     */
    abstract public void applyCommand(PlayerConnection connection) throws NullPointerException;
}
