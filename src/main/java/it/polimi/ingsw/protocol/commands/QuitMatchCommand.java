package it.polimi.ingsw.protocol.commands;

import it.polimi.ingsw.network.PlayerConnection;
import it.polimi.ingsw.protocol.answers.EndMatchAnswer;

public class QuitMatchCommand extends Command
{

    public void applyCommand(PlayerConnection connection) throws Exception
    {
        checkPlayerConnection(connection);

        connection.getServer().removePlayerFromMatch(connection);

        // Notify the player that the match ended
        connection.sendAnswer(new EndMatchAnswer("Quitted from the match"));
    }
}
