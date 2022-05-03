package it.polimi.ingsw.protocol.answers;

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
}
