package it.polimi.ingsw.protocol.answers;

import java.util.HashMap;
import java.util.Map;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.network.Match;

public class MatchesListAnswer extends Answer
{
    Map<String, Integer> numPlayers;

    Map<String, GameMode> gameModes;

    public MatchesListAnswer(Map<String, Match> matches)
    {
        numPlayers = new HashMap<>();
        gameModes = new HashMap<>();

        for (Map.Entry<String, Match> match : matches.entrySet())
        {
            numPlayers.put(match.getKey(), match.getValue().getPlayersNumber());
            gameModes.put(match.getKey(), match.getValue().getController().getGameMode());
        }
    }

    public Map<String, Integer> getNumPlayers()
    {
        return numPlayers;
    }

    public Map<String, GameMode> getGameModes()
    {
        return gameModes;
    }

    @Override
    public String toString()
    {
        if (numPlayers.isEmpty())
        {
            return "[MatchesListCommand] There are no matches";
        } else
        {
            String str = "[MatchesListCommand] Matches list:\n";

            for (String key : numPlayers.keySet())
                str += "\t" + key + ": mode = " + gameModes.get(key) + " / players = "
                        + numPlayers.get(key) + "\n";

            return str;
        }
    }
}
