package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class StartMatchAnswer extends Answer
{
    @Serial
    private static final long serialVersionUID = 2455878640788529559L;

    private Map<String, Integer> players;

    public StartMatchAnswer()
    {
        players = new HashMap<>();
    }

    public StartMatchAnswer(Map<String, Integer> players)
    {
        this.players = new HashMap<>(players);
    }

    @Override
    public String toString()
    {
        return "[StartMatchAnswer] Match started";
    }

    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displayStartMatch(this);
    }

    public Map<String, Integer> getPlayers()
    {
        return new HashMap<>(players);
    }
}
