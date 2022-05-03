package it.polimi.ingsw.protocol.commands;

import java.util.Map;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.network.PlayerConnection;
import it.polimi.ingsw.protocol.answers.MatchesListAnswer;

public class GetMatchesListCommand extends Command
{
    public void applyCommand(PlayerConnection connection) throws IllegalArgumentException
    {
        checkPlayerConnection(connection);

        Map<String, Match> matches = connection.getServer().getAllMatches();
        MatchesListAnswer answer = new MatchesListAnswer(matches);
        connection.sendAnswer(answer);
    }
}
