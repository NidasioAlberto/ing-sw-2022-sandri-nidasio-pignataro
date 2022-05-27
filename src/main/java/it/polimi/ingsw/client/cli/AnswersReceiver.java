package it.polimi.ingsw.client.cli;

import java.io.IOException;
import java.io.ObjectInputStream;
import it.polimi.ingsw.client.cli.utils.PrintHelper;

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
                PrintHelper.printMessage("New packet received");
            }
        } catch (IOException e)
        {
            System.err.println("[AnswersReceiver] Error while reading an object: " + e.getMessage());
        } catch (ClassNotFoundException e)
        {
            System.out.println("SEVERE ERROR! " + e.getMessage());
        } catch (Error e)
        {
            System.out.println("[AnswersReceiver] Generic error: " + e.getMessage());
        }
    }
}
