package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

public class JoinedMatchAnswer extends Answer
{
    private String matchId;

    public JoinedMatchAnswer(String matchId)
    {
        this.matchId = matchId;
    }

    @Override
    public String toString()
    {
        return "[JoinedMatchAnswer] Joined match " + matchId;
    }

    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displayJoinedMatch(this);
    }
}
