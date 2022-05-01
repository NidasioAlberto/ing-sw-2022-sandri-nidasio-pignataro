package it.polimi.ingsw.client.mock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.protocol.commands.*;
import it.polimi.ingsw.protocol.messages.*;

public class MockClient
{
    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("127.0.0.1", 2345);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            ExecutorService executor = Executors.newCachedThreadPool();
            executor.submit(new AnswersReceiver(inputStream));

            Scanner scanner = new Scanner(System.in);
            while (true)
            {
                try
                {
                    choosePacket(outputStream, scanner);
                } catch (Exception e)
                {
                    System.out.println("Unable to parse parse the input: " + e.toString());
                }
            }
            // scanner.close();
        } catch (Exception e)
        {
            System.err.println("[Main] Error: " + e.getMessage());
        }
    }

    static void choosePacket(ObjectOutputStream outputStream, Scanner scanner) throws IOException
    {
        System.out.println("Choose between:");
        System.out.println("\t1 - Commands");
        System.out.println("\t2 - Actions");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 1:
                chooseCommand(outputStream, scanner);
                break;
            case 2:
                chooseAction(outputStream, scanner);
                break;
        }
    }

    static void chooseCommand(ObjectOutputStream outputStream, Scanner scanner) throws IOException
    {
        System.out.println("Choose between:");
        System.out.println("\t1 - Create match");
        System.out.println("\t2 - Get matches list");
        System.out.println("\t3 - Join match");
        System.out.println("\t4 - Quit game");
        System.out.println("\t5 - Set name");

        int choice = Integer.parseInt(scanner.nextLine());
        scanner.reset();

        switch (choice)
        {
            case 1:
            {
                System.out.print("Match name: ");
                String matchId = scanner.nextLine();
                System.out.print("Game mode [" + GameMode.CLASSIC.toString() + ","
                        + GameMode.EXPERT.toString() + "]: ");
                GameMode gameMode = GameMode.valueOf(scanner.nextLine());
                outputStream.writeObject(new CreateMatchCommand(matchId, gameMode));
                break;
            }
            case 2:
            {
                outputStream.writeObject(new GetMatchesListCommand());
                break;
            }
            case 3:
            {
                System.out.println("Match name: ");
                String matchId = scanner.nextLine();
                outputStream.writeObject(new JoinMatchCommand(matchId));
                break;
            }
            case 4:
            {
                outputStream.writeObject(new QuitGameCommand());
                break;
            }
            case 5:
            {
                System.out.println("Player name: ");
                String playerName = scanner.nextLine();
                outputStream.writeObject(new SetNameCommand(playerName));
                break;
            }
        }
    }

    static void chooseAction(ObjectOutputStream outputStream, Scanner scanner) throws IOException
    {
        System.out.println("Choose between:");
        System.out.println("\t1 - Character card action");
        System.out.println("\t2 - End turn");
        System.out.println("\t3 - Move mother nature");
        System.out.println("\t4 - Move student from entrance to dining");
        System.out.println("\t5 - Move student from entrance to island");
        System.out.println("\t6 - Play assistant card");
        System.out.println("\t7 - Play character card");
        System.out.println("\t8 - Select cloud tile");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 1:
            {
                System.out.println("Expert game action:");
                for (int i = 0; i < ExpertGameAction.values().length; i++)
                    System.out.println(i + " - " + ExpertGameAction.values()[i]);
                int action = Integer.parseInt(scanner.nextLine());
                int selectedIsland = selectIsland(scanner);
                List<SchoolColor> selectedColors = chooseSchoolColors(scanner);
                outputStream.writeObject(new CharacterCardActionMessage(
                        ExpertGameAction.values()[action], selectedIsland, selectedColors));
                break;
            }
            case 2:
            {
                outputStream.writeObject(new EndTurnMessage());
                break;
            }
            case 3:
            {
                int selectedIsland = selectIsland(scanner);
                outputStream.writeObject(new MoveMotherNatureMessage(selectedIsland));
                break;
            }
            case 4:
            {
                SchoolColor selectedColor = selectSchoolColors(scanner);
                outputStream.writeObject(new MoveStudentFromEntranceToDiningMessage(selectedColor));
                break;
            }
            case 5:
            {
                SchoolColor selectedColor = selectSchoolColors(scanner);
                int selectedIsland = selectIsland(scanner);
                outputStream.writeObject(
                        new MoveStudentFromEntranceToIslandMessage(selectedColor, selectedIsland));
                break;
            }
            case 6:
            {
                System.out.println("Selected card: ");
                int selectedCard = Integer.parseInt(scanner.nextLine());
                outputStream.writeObject(new PlayAssistantCardMessage(selectedCard));
                break;
            }
            case 7:
            {
                System.out.println("Selected character card: ");
                int selectedCharacterCard = Integer.parseInt(scanner.nextLine());
                outputStream.writeObject(new PlayCharacterCardMessage(selectedCharacterCard));
                break;
            }
            case 8:
            {
                System.out.println("Selected cloud tile: ");
                int selectedCloudTile = Integer.parseInt(scanner.nextLine());
                outputStream.writeObject(new SelectCloudTileMessage(selectedCloudTile));
                break;
            }
        }
    }

    static List<SchoolColor> chooseSchoolColors(Scanner scanner)
    {
        List<SchoolColor> selectedColors = new ArrayList<>();

        do
        {
            selectedColors.add(selectSchoolColors(scanner));
            System.out.println("Type 'Y' to select another");
        } while (scanner.nextLine() == "Y" && selectedColors.size() < SchoolColor.values().length);

        return selectedColors;
    }

    static SchoolColor selectSchoolColors(Scanner scanner)
    {
        System.out.println("Choose a color:");
        for (int i = 0; i < SchoolColor.values().length; i++)
            System.out.println(i + " - " + ExpertGameAction.values()[i]);
        return SchoolColor.values()[Integer.parseInt(scanner.nextLine())];
    }

    static int selectIsland(Scanner scanner)
    {

        System.out.print("Selected island: ");
        return Integer.parseInt(scanner.nextLine());
    }
}
