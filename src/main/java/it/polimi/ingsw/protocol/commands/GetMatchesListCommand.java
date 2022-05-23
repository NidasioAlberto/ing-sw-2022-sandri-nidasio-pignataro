package it.polimi.ingsw.protocol.commands;

import java.io.Serial;
import java.util.Map;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.network.PlayerConnection;
import it.polimi.ingsw.protocol.answers.MatchesListAnswer;

public class GetMatchesListCommand extends Command
{
    @Serial
    private static final long serialVersionUID = -5502527539523157024L;

    public void applyCommand(PlayerConnection connection) throws IllegalArgumentException
    {
        checkPlayerConnection(connection);

        Map<String, Match> matches = connection.getServer().getAllMatches();
        MatchesListAnswer answer = new MatchesListAnswer(matches);
        connection.sendAnswer(answer);
    }
}
