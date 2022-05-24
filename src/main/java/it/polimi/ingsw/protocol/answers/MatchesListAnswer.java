package it.polimi.ingsw.protocol.answers;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

import it.polimi.ingsw.client.Visualizer;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.network.Match;

public class MatchesListAnswer extends Answer
{
    @Serial
    private static final long serialVersionUID = 3123945080375660972L;

    Map<String, Integer> maxNumPlayers;

    Map<String, Integer> numPlayers;

    Map<String, GameMode> gameModes;

    public MatchesListAnswer(Map<String, Match> matches)
    {
        maxNumPlayers = new HashMap<>();
        numPlayers = new HashMap<>();
        gameModes = new HashMap<>();

        for (Map.Entry<String, Match> match : matches.entrySet())
        {
            maxNumPlayers.put(match.getKey(), match.getValue().getController().getPlayersNumber());
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
            return "[MatchesListAnswer] There are no matches";
        } else
        {
            String str = "[MatchesListAnswer] Matches list:\n";

            for (String key : numPlayers.keySet())
                str += "\t" + key + ": mode = " + gameModes.get(key) + " - players = " + numPlayers.get(key) + "/" + maxNumPlayers.get(key) + "\n";

            return str;
        }
    }

    /**
     * toString() of a single match.
     * @param key name of the match.
     * @return the match's string.
     */
    public String singleMatchToString(String key)
    {
        String str = "";

        if (gameModes.containsKey(key))
            str += "\t" + key + ": mode = " + gameModes.get(key) + " - players = " + numPlayers.get(key) + "/" + maxNumPlayers.get(key) + "\n";

        return str;
    }
    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displayMatchesList(this);
    }
}
