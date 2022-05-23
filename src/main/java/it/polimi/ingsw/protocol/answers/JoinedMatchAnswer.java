package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

import java.io.Serial;

public class JoinedMatchAnswer extends Answer
{
    @Serial
    private static final long serialVersionUID = -5016036062321547220L;

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
