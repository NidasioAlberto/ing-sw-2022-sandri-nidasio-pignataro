package it.polimi.ingsw.client.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.Visualizer;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.protocol.answers.*;
import it.polimi.ingsw.protocol.commands.*;
import it.polimi.ingsw.protocol.messages.*;
import it.polimi.ingsw.protocol.updates.*;

public class CLI extends Visualizer implements Runnable
{
    ExecutorService executor;

    private boolean active = false;

    public CLI(Client client)
    {
        super(client);

        executor = Executors.newCachedThreadPool();
    }

    public void start()
    {
        try
        {
            client.connect();

            active = true;
            executor.submit(client);
            executor.submit(this);
        } catch (IOException e)
        {
            System.out.println("[Client] Unable to connect to the server");
        }
    }

    public synchronized void stop() throws IOException
    {
        active = false;
        client.stop();
    }

    public synchronized boolean isActive()
    {
        return active;
    }

    @Override
    public void run()
    {
        Scanner scanner = new Scanner(System.in);

        while (isActive())
        {
            try
            {
                choosePacket(scanner);
            } catch (Exception e)
            {
                System.out.println("Unable to parse parse the input: " + e.toString());
            }
        }
    }

    void choosePacket(Scanner scanner) throws IOException
    {
        System.out.println("Choose between:");
        System.out.println("\t1 - Commands");
        System.out.println("\t2 - Actions");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 1:
                chooseCommand(scanner);
                break;
            case 2:
                chooseAction(scanner);
                break;
            default:
                System.out.println("You should choose a command between 1 and 2.\n");
                break;
        }
    }

    void chooseCommand(Scanner scanner) throws IOException
    {
        System.out.println("Choose between:");
        System.out.println("\t1 - Set name");
        System.out.println("\t2 - Create match");
        System.out.println("\t3 - Get matches list");
        System.out.println("\t4 - Join match");
        System.out.println("\t5 - Quit match");
        System.out.println("\t6 - Quit game");
        System.out.println("\t7 - Undo choice");

        int choice = Integer.parseInt(scanner.nextLine());
        scanner.reset();

        switch (choice)
        {
            case 1:
            {
                System.out.println("Player name: ");
                String playerName = scanner.nextLine();
                client.sendCommand(new SetNameCommand(playerName));
                break;
            }
            case 2:
            {
                System.out.print("Match name: ");
                String matchId = scanner.nextLine();
                System.out.print("Game mode [" + GameMode.CLASSIC.toString() + "," + GameMode.EXPERT.toString() + "]: ");
                GameMode gameMode = GameMode.valueOf(scanner.nextLine());
                System.out.print("Players number [2-3]: ");
                int playersNumber = Integer.parseInt(scanner.nextLine());
                client.sendCommand(new CreateMatchCommand(matchId, playersNumber, gameMode));
                break;
            }
            case 3:
            {
                client.sendCommand(new GetMatchesListCommand());
                break;
            }
            case 4:
            {
                System.out.println("Match name: ");
                String matchId = scanner.nextLine();
                client.sendCommand(new JoinMatchCommand(matchId));
                break;
            }
            case 5:
            {
                client.sendCommand(new QuitMatchCommand());
                break;
            }
            case 6:
            {
                client.sendCommand(new QuitGameCommand());
                System.exit(0);
                break;
            }
            case 7:
            {
                break;
            }
            default:
            {
                System.out.println("You should choose a command between 1 and 7.\n");
                chooseCommand(scanner);
                break;
            }
        }
    }

    void chooseAction(Scanner scanner) throws IOException
    {
        System.out.println("Choose between:");
        System.out.println("\t1 - Play assistant card");
        System.out.println("\t2 - Move student from entrance to dining");
        System.out.println("\t3 - Move student from entrance to island");
        System.out.println("\t4 - Move mother nature");
        System.out.println("\t5 - Select cloud tile");
        System.out.println("\t6 - End turn");
        System.out.println("\t7 - Play character card");
        System.out.println("\t8 - Character card action");
        System.out.println("\t9 - Character cards effects");
        System.out.println("\t10 - Undo choice");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 1:
            {
                System.out.println("Selected card: ");
                int selectedCard = Integer.parseInt(scanner.nextLine());
                client.sendAction(new PlayAssistantCardMessage(selectedCard));
                break;
            }
            case 2:
            {
                SchoolColor selectedColor = selectSchoolColors(scanner);
                client.sendAction(new MoveStudentFromEntranceToDiningMessage(selectedColor));
                break;
            }
            case 3:
            {
                SchoolColor selectedColor = selectSchoolColors(scanner);
                int selectedIsland = selectIsland(scanner);
                client.sendAction(new MoveStudentFromEntranceToIslandMessage(selectedColor, selectedIsland));
                break;
            }
            case 4:
            {
                int selectedIsland = selectIsland(scanner);
                client.sendAction(new MoveMotherNatureMessage(selectedIsland));
                break;
            }
            case 5:
            {
                System.out.println("Selected cloud tile: ");
                int selectedCloudTile = Integer.parseInt(scanner.nextLine());
                client.sendAction(new SelectCloudTileMessage(selectedCloudTile));
                break;
            }
            case 6:
            {
                client.sendAction(new EndTurnMessage());
                break;
            }
            case 7:
            {
                System.out.println("Selected character card: ");
                int selectedCharacterCard = Integer.parseInt(scanner.nextLine());
                client.sendAction(new PlayCharacterCardMessage(selectedCharacterCard));
                break;
            }
            case 8:
            {
                System.out.println("Expert game action:");
                for (int i = 0; i < ExpertGameAction.values().length; i++)
                    System.out.println(i + " - " + ExpertGameAction.values()[i]);
                int action = Integer.parseInt(scanner.nextLine());
                int selectedIsland = selectIsland(scanner);
                List<SchoolColor> selectedColors = chooseSchoolColors(scanner);
                client.sendAction(new CharacterCardActionMessage(ExpertGameAction.values()[action], selectedIsland, selectedColors));
                break;
            }
            case 9:
            {
                System.out.println(characterCardsEffects());
                break;
            }
            case 10:
            {
                break;
            }
            default:
            {
                System.out.println("You should choose an action between 1 and 10.\n");
                chooseAction(scanner);
                break;
            }
        }
    }

    List<SchoolColor> chooseSchoolColors(Scanner scanner)
    {
        List<SchoolColor> selectedColors = new ArrayList<>();

        do
        {
            selectedColors.add(selectSchoolColors(scanner));
            System.out.println("Type 'Y' to select another");
        } while (scanner.nextLine().equals("Y") && selectedColors.size() < SchoolColor.values().length);

        return selectedColors;
    }

    SchoolColor selectSchoolColors(Scanner scanner)
    {
        System.out.println("Choose a color:");
        for (int i = 0; i < SchoolColor.values().length; i++)
            System.out.println(i + " - " + SchoolColor.values()[i]);
        return SchoolColor.values()[Integer.parseInt(scanner.nextLine())];
    }

    int selectIsland(Scanner scanner)
    {

        System.out.print("Selected island: ");
        return Integer.parseInt(scanner.nextLine());
    }

    /**
     * List of Character Cards' effects.
     * 
     * @return the printable version of the effects.
     */
    String characterCardsEffects()
    {
        String rules = "";

        rules += "These are the effects of the character cards: \n";
        rules += "- MONK: Take 1 Student from this card and place it on an island of your choice. "
                + "Then, draw a new Student from the bag and place it on this card;\n";
        rules += "- SHAMAN: During this turn, you take control of any number of Professors even if you "
                + "have the same number of Students as the player who currently controls them;\n";
        rules += "- HERALD: Choose an Island and resolve the Island as if Mother Nature had ended her movement there."
                + " Mother Nature will still move and the Island where she ends her movement will also be resolved;\n";
        rules += "- POSTMAN: You may move Mother Nature up to 2 additional Islands than is indicated " + "by the Assistant card you've played;\n";
        rules += "- GRANDMA_HERBS: Place a No Entry tile on an Island of your choice. The  first time Mother Nature "
                + "ends her movement there, put the No Entry Tile back onto this card DO NOT calculate influence on "
                + "that Island, or place any Towers;\n";
        rules += "- CENTAUR: When resolving a computeInfluence on an Island, Towers do not count towards influence;\n";
        rules += "- JOKER: You may take up to 3 Students from this card and replace them with the same number " + "of Students from your Entrance;\n";
        rules += "- KNIGHT: During the influence calculation this turn, you count as having 2 more influence;\n";
        rules += "- MUSHROOM_MAN: Choose a color of Student; during the influence calculation this turn, " + "that color adds no influence;\n";
        rules += "- MINSTREL: You may exchange up to 2 Students between your Entrance and your Dining Room;\n";
        rules += "- PRINCESS: Take 1 Student from this card and place it in your Dining Room. Then, draw a new Student"
                + " from the Bag and place it on this card;\n";
        rules += "- THIEF: Choose a type of Student; every player (including yourself) must return 3 Students of that "
                + "type from their Dining Room to the bag. If any player has fewer than 3 Students of that type,"
                + " return as many Students as they have.\n";

        return rules;
    }

    // Updates

    @Override
    public void displayAssistantCards(AssistantCardsUpdate update)
    {}

    @Override
    public void displayCharacterCardPayload(CharacterCardPayloadUpdate update)
    {}

    @Override
    public void displayCharacterCards(CharacterCardsUpdate update)
    {}

    @Override
    public void displayCloudTiles(CloudTilesUpdate update)
    {}

    @Override
    public void displayIslands(IslandsUpdate update)
    {}

    @Override
    public void displayPlayedAssistantCard(PlayedAssistantCardUpdate update)
    {}

    @Override
    public void displaySchoolboard(SchoolBoardUpdate update)
    {}

    // Answers

    @Override
    public void displayEndMatch(EndMatchAnswer answer)
    {}

    @Override
    public void displayError(ErrorAnswer answer)
    {}

    @Override
    public void displayJoinedMatch(JoinedMatchAnswer answer)
    {}

    @Override
    public void displayMatchesList(MatchesListAnswer answer)
    {}

    @Override
    public void displaySetName(SetNameAnswer answer)
    {}

    @Override
    public void displayStartMatch(StartMatchAnswer answer)
    {}

    public static void main(String[] args)
    {
        Client client = new Client();
        CLI cli = new CLI(client);
        client.setVisualizer(cli);

        cli.start();
    }
}
