package it.polimi.ingsw.protocol.answers;

import it.polimi.ingsw.client.Visualizer;

public class ErrorAnswer extends Answer
{
    private String errorMessage;

    public ErrorAnswer(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    @Override
    public String toString()
    {
        return "[ErrorAnswer] Error: " + errorMessage;
    }

    @Override
    public void handleAnswer(Visualizer handler)
    {
        handler.displayError(this);
    }
}
