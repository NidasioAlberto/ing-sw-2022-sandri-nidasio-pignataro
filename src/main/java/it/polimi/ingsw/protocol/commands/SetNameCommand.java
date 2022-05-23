package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.network.PlayerConnection;
import it.polimi.ingsw.protocol.answers.ErrorAnswer;

import java.io.Serial;

public class SetNameCommand extends Command
{
    @Serial
    private static final long serialVersionUID = 6186392409377027610L;

    String playerName;

    public SetNameCommand(String playerName)
    {
        this.playerName = playerName;
    }

    public void applyCommand(PlayerConnection connection) throws NullPointerException
    {
        // Check if the player has already set a name
        if (connection.getPlayerName().isPresent())
            connection.sendAnswer(new ErrorAnswer("Attempting to change the player name"));
        else
            connection.setPlayerName(playerName);
    }
}
