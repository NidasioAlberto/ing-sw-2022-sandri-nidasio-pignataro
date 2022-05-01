package it.polimi.ingsw.client.mock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.protocol.commands.CreateMatchCommand;
import it.polimi.ingsw.protocol.commands.GetMatchesListCommand;

public class MockClient
{
    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("127.0.0.1", 1234);

            Thread receiver =
                    new Thread(new AnswersReceiver(new ObjectInputStream(socket.getInputStream())));
            receiver.start();

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            while (true)
                choosePacket(outputStream);
        } catch (Exception e)
        {
            System.err.println("[Main] Error: " + e.getMessage());
        }
    }

    static void choosePacket(ObjectOutputStream outputStream)
    {
        System.out.println("Choose between:");
        System.out.println("\t1 - Commands");
        System.out.println("\t2 - Actions");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.close();

        switch (choice)
        {
            case 1:

            case 2:
        }
    }

    static void chooseCommand(ObjectOutputStream outputStream) throws IOException
    {
        System.out.println("Choose between:");
        System.out.println("\t1 - Create match");
        System.out.println("\t2 - Get matches list");
        System.out.println("\t3 - Join match");
        System.out.println("\t4 - Quit game");
        System.out.println("\t5 - Set name");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice)
        {
            case 1:
                System.out.println("Match name: ");
                String matchId = scanner.nextLine();
                System.out.println("Game mode [" + GameMode.CLASSIC.toString() + ","
                        + GameMode.EXPERT.toString() + "]: ");
                GameMode gameMode = GameMode.valueOf(scanner.nextLine());
                outputStream.writeObject(new CreateMatchCommand(matchId, gameMode));
                break;
            case 2:
                outputStream.writeObject(new GetMatchesListCommand());
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }

        scanner.close();
    }

    static void chooseAction(ObjectOutputStream outputStream)
    {

        System.out.println("Choose between:");
        System.out.println("\t1 - ");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.close();
    }
}
