package it.polimi.ingsw.client.mock;

import java.io.IOException;
import java.io.ObjectInputStream;

public class AnswersReceiver implements Runnable
{
    private ObjectInputStream inputStream;

    AnswersReceiver(ObjectInputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                System.out.println(inputStream.readObject().toString());
            }
        } catch (IOException e)
        {
            System.err
                    .println("[AnswersReceiver] Error while reading an object: " + e.getMessage());
        } catch (ClassNotFoundException e)
        {
            System.out.println("SEVERE ERROR! " + e.getMessage());
        }
    }
}
