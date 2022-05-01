package it.polimi.ingsw.protocol.answers;

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
}
