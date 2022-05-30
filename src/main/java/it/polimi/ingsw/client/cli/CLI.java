package it.polimi.ingsw.client.cli;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.Visualizer;
import it.polimi.ingsw.client.cli.utils.PrintHelper;
import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.GameMode;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.game.CharacterCardType;
import it.polimi.ingsw.protocol.answers.*;
import it.polimi.ingsw.protocol.commands.*;
import it.polimi.ingsw.protocol.messages.*;
import it.polimi.ingsw.protocol.updates.*;

public class CLI extends Visualizer implements Runnable
{
    ExecutorService executor;

    private boolean active = false;

    private boolean isMatchStarted = false;

    private Map<String, Integer> players;

    /**
     * Save the state of the match in order to display it every time I want.
     */
    private AssistantCardsUpdate assistantCards;

    private CharacterCardsUpdate characterCards;

    private CloudTilesUpdate cloudTiles;

    private IslandsUpdate islands;

    private Map<Integer, PlayedAssistantCardUpdate> playedAssistantCards = new HashMap<Integer, PlayedAssistantCardUpdate>();

    private Map<Integer, SchoolBoardUpdate> schoolBoards = new HashMap<Integer, SchoolBoardUpdate>();

    private MatchesListAnswer matchesList;

    /**
     * Name of the player.
     */
    private String name;
    /**
     * Used to color the SchoolBoard of the current player.
     */
    private int currentPlayerIndex;

    public CLI(Client client)
    {
        super(client);

        executor = Executors.newCachedThreadPool();

        players = new HashMap<>();
    }

    public void start()
    {
        // Clear the screen
        PrintHelper.print(PrintHelper.ERASE_ENTIRE_SCREEN);
        printTitle(2, 2);

        try
        {
            client.connect();

            active = true;
            executor.submit(client);
            executor.submit(this);
        } catch (IOException e)
        {
            PrintHelper.print("[Client] Unable to connect to the server");
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
                if (isMatchStarted)
                    chooseAction(scanner);
                else
                    choosePacket(scanner);
            } catch (Exception e)
            {
                PrintHelper.printMessage("Unable to parse parse the input: " + e.toString());
            }
        }
    }

    public void choosePacket(Scanner scanner) throws IOException
    {
        PrintHelper.printM(26, 2, PrintHelper.ERASE_FROM_CURSOR_TILL_END_OF_SCREEN);
        String msg = "";
        msg += "Choose between:\n";
        msg += "\t1 - Commands\n";
        msg += "\t2 - Actions\n";
        PrintHelper.print(msg);
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 1:
                chooseCommand(scanner);
                break;
            case 2:
                chooseAction(scanner);
                break;
        }
    }

    public void chooseCommand(Scanner scanner) throws IOException
    {
        PrintHelper.printM(26, 2, PrintHelper.ERASE_FROM_CURSOR_TILL_END_OF_SCREEN);
        String msg = "";
        msg += "Choose between:\n";
        msg += "\t1 - Set name\n";
        msg += "\t2 - Create match\n";
        msg += "\t3 - Get matches list\n";
        msg += "\t4 - Join match\n";
        msg += "\t5 - Quit match\n";
        msg += "\t6 - Quit game\n";
        PrintHelper.print(msg);

        int choice = Integer.parseInt(scanner.nextLine());
        scanner.reset();

        switch (choice)
        {
            case 1:
            {
                PrintHelper.print("Player name: ");
                String playerName = scanner.nextLine();
                if (playerName.length() >= 18)
                {
                    PrintHelper.printMessage("The name is too long");
                    break;
                }
                client.sendCommand(new SetNameCommand(playerName));
                break;
            }
            case 2:
            {
                PrintHelper.print("Match name: ");
                String matchId = scanner.nextLine();
                PrintHelper.print("Game mode [" + GameMode.CLASSIC.toString() + "," + GameMode.EXPERT.toString() + "]: ");
                GameMode gameMode = GameMode.valueOf(scanner.nextLine());
                PrintHelper.print("Players number [2-3]: ");
                int playersNumber = Integer.parseInt(scanner.nextLine());
                client.sendCommand(new CreateMatchCommand(matchId, playersNumber, gameMode));
                break;
            }
            case 3:
            {
                client.sendCommand(new GetMatchesListCommand());
                // displayMatchesList(scanner);
                break;
            }
            case 4:
            {
                PrintHelper.print("Match name: ");
                String matchId = scanner.nextLine();
                PrintHelper.printMR(24, 1, PrintHelper.ERASE_FROM_CURSOR_TILL_BEGINNING_OF_SCREEN);
                client.sendCommand(new JoinMatchCommand(matchId));
                break;
            }
            case 5:
            {
                PrintHelper.print("Are you sure to quit match? Type 'Y' if you are sure\n");
                if (scanner.nextLine().equals("Y"))
                    client.sendCommand(new QuitMatchCommand());
                break;
            }
            case 6:
            {
                PrintHelper.print("Are you sure to quit the game? Type 'Y' if you are sure\n");
                if (scanner.nextLine().equals("Y"))
                {
                    client.sendCommand(new QuitGameCommand());
                    System.exit(0);
                }
                break;
            }
        }
    }

    public void chooseAction(Scanner scanner) throws IOException
    {
        PrintHelper.print(PrintHelper.ERASE_ENTIRE_SCREEN);

        // Every time I print the board
        displayBoard();

        String msg = "";
        msg += "Choose between:\n";
        msg += "\t1 - Play assistant card\n";
        msg += "\t2 - Move student from entrance to dining\n";
        msg += "\t3 - Move student from entrance to island\n";
        msg += "\t4 - Move mother nature\n";
        msg += "\t5 - Select cloud tile\n";
        msg += "\t6 - End turn\n";
        msg += "\t7 - Play character card\n";
        msg += "\t8 - Character card action\n";
        msg += "\t9 - Character cards effects\n";
        msg += "\t10 - Quit match\n";
        PrintHelper.printM(26, 2, msg);
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice)
        {
            case 1:
            {
                PrintHelper.print("Selected card: ");
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
                PrintHelper.print("Selected cloud tile: ");
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
                PrintHelper.print("Selected character card: ");
                int selectedCharacterCard = Integer.parseInt(scanner.nextLine());
                client.sendAction(new PlayCharacterCardMessage(selectedCharacterCard));
                break;
            }
            case 8:
            {
                PrintHelper.printM(26, 2, PrintHelper.ERASE_FROM_CURSOR_TILL_END_OF_SCREEN);
                PrintHelper.print("If you don't have to select an island nor colors choose 'Base action'\n");
                PrintHelper.print("Expert game action:\n");
                for (int i = 0; i < ExpertGameAction.values().length; i++)
                    PrintHelper.print("\t" + i + " - " + ExpertGameAction.values()[i] + "\n");
                int action = Integer.parseInt(scanner.nextLine());
                if (action < 0 || action >= ExpertGameAction.values().length)
                    break;
                PrintHelper.print("If you don't need to select an island just type '0'\n");
                int selectedIsland = selectIsland(scanner);
                PrintHelper.printMR(26, 0, PrintHelper.ERASE_FROM_CURSOR_TILL_END_OF_SCREEN);
                PrintHelper.printMR(26, 2, "If you don't need to select a color just type '0'\n");
                List<SchoolColor> selectedColors = chooseSchoolColors(scanner);
                client.sendAction(new CharacterCardActionMessage(ExpertGameAction.values()[action], selectedIsland, selectedColors));
                break;
            }
            case 9:
            {
                PrintHelper.printM(26, 2, PrintHelper.ERASE_FROM_CURSOR_TILL_END_OF_SCREEN);
                PrintHelper.print(characterCardsEffects());
                PrintHelper.print("\nPress enter when you are done\n");
                scanner.nextLine();
                break;
            }
            case 10:
            {
                PrintHelper.print("Are you sure to quit match? Type 'Y' if you are sure\n");
                if (scanner.nextLine().equals("Y"))
                {
                    isMatchStarted = false;
                    client.sendCommand(new QuitMatchCommand());
                }
                break;
            }
        }
    }

    private List<SchoolColor> chooseSchoolColors(Scanner scanner)
    {
        List<SchoolColor> selectedColors = new ArrayList<>();

        do
        {
            selectedColors.add(selectSchoolColors(scanner));
            PrintHelper.print("Type 'Y' to select another\n");
        } while (scanner.nextLine().equals("Y") && selectedColors.size() < SchoolColor.values().length);

        return selectedColors;
    }

    private SchoolColor selectSchoolColors(Scanner scanner)
    {
        PrintHelper.printM(27, 0, PrintHelper.ERASE_FROM_CURSOR_TILL_END_OF_SCREEN);
        String msg = "";
        msg += "Choose a color:\n";
        for (int i = 0; i < SchoolColor.values().length; i++)
            msg += "\t" + i + " - " + SchoolColor.values()[i] + "\n";
        PrintHelper.print(msg);

        return SchoolColor.values()[Integer.parseInt(scanner.nextLine())];
    }

    private int selectIsland(Scanner scanner)
    {

        PrintHelper.print("Selected island: ");
        return Integer.parseInt(scanner.nextLine());
    }

    /**
     * List of Character Cards' effects.
     * 
     * @return the printable version of the effects.
     */
    private String characterCardsEffects()
    {
        String rules = "";

        rules += "These are the effects of the character cards: \n";
        rules += "- " + CharacterCardType.MONK + ": Take 1 Student from this card and place it on an Island of your choice. "
                + "Then, draw a new Student from the bag and place it on this card;\n";
        rules += "- " + CharacterCardType.SHAMAN + ": During this turn, you take control of any number of Professors even if you "
                + "have the same number of Students as the player who currently controls them;\n";
        rules += "- " + CharacterCardType.HERALD + ": Choose an Island and resolve the Island as if Mother Nature had ended her movement there."
                + " Mother Nature will still move and the Island where she ends her movement will also be resolved;\n";
        rules += "- " + CharacterCardType.POSTMAN + ": You may move Mother Nature up to 2 additional Islands than is indicated "
                + "by the Assistant card you've played;\n";
        rules += "- " + CharacterCardType.GRANDMA_HERBS + ": Place a No Entry tile on an Island of your choice. The  first time Mother Nature "
                + "ends her movement there, put the No Entry Tile back onto this card DO NOT calculate influence on "
                + "that Island, or place any Towers;\n";
        rules += "- " + CharacterCardType.CENTAUR + ": When resolving a conquering on an Island, Towers do not count towards influence;\n";
        rules += "- " + CharacterCardType.JOKER + ": You may take up to 3 Students from this card and replace them with the same number "
                + "of Students from your Entrance;\n";
        rules += "- " + CharacterCardType.KNIGHT + ": During the influence calculation this turn, you count as having 2 more influence;\n";
        rules += "- " + CharacterCardType.MUSHROOM_MAN + ": Choose a color of Student; during the influence calculation this turn, "
                + "that color adds no influence;\n";
        rules += "- " + CharacterCardType.MINSTREL + ": You may exchange up to 2 Students between your Entrance and your Dining Room;\n";
        rules += "- " + CharacterCardType.PRINCESS + ": Take 1 Student from this card and place it in your Dining Room. Then, draw a new Student"
                + " from the Bag and place it on this card;\n";
        rules += "- " + CharacterCardType.THIEF + ": Choose a type of Student; every player (including yourself) must return 3 Students of that "
                + "type from their Dining Room to the bag. If any player has fewer than 3 Students of that type,"
                + " return as many Students as they have.\n";
        rules += "Original site with rules: https://craniointernational.com/products/eriantys/";

        return rules;
    }

    private void printTitle(int row, int column)
    {
        PrintHelper.printMR(row, column, " ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄        ▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄         ▄  ▄▄▄▄▄▄▄▄▄▄▄ ");
        PrintHelper.printMR(row + 1, column,
                "▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌▐░░▌      ▐░▌▐░░░░░░░░░░░▌▐░▌       ▐░▌▐░░░░░░░░░░░▌");
        PrintHelper.printMR(row + 2, column,
                "▐░█▀▀▀▀▀▀▀▀▀ ▐░█▀▀▀▀▀▀▀█░▌ ▀▀▀▀█░█▀▀▀▀ ▐░█▀▀▀▀▀▀▀█░▌▐░▌░▌     ▐░▌ ▀▀▀▀█░█▀▀▀▀ ▐░▌       ▐░▌▐░█▀▀▀▀▀▀▀▀▀ ");
        PrintHelper.printMR(row + 3, column,
                "▐░▌          ▐░▌       ▐░▌     ▐░▌     ▐░▌       ▐░▌▐░▌▐░▌    ▐░▌     ▐░▌     ▐░▌       ▐░▌▐░▌          ");
        PrintHelper.printMR(row + 4, column,
                "▐░█▄▄▄▄▄▄▄▄▄ ▐░█▄▄▄▄▄▄▄█░▌     ▐░▌     ▐░█▄▄▄▄▄▄▄█░▌▐░▌ ▐░▌   ▐░▌     ▐░▌     ▐░█▄▄▄▄▄▄▄█░▌▐░█▄▄▄▄▄▄▄▄▄ ");
        PrintHelper.printMR(row + 5, column,
                "▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌     ▐░▌     ▐░░░░░░░░░░░▌▐░▌  ▐░▌  ▐░▌     ▐░▌     ▐░░░░░░░░░░░▌▐░░░░░░░░░░░▌");
        PrintHelper.printMR(row + 6, column,
                "▐░█▀▀▀▀▀▀▀▀▀ ▐░█▀▀▀▀█░█▀▀      ▐░▌     ▐░█▀▀▀▀▀▀▀█░▌▐░▌   ▐░▌ ▐░▌     ▐░▌      ▀▀▀▀█░█▀▀▀▀  ▀▀▀▀▀▀▀▀▀█░▌");
        PrintHelper.printMR(row + 7, column,
                "▐░▌          ▐░▌     ▐░▌       ▐░▌     ▐░▌       ▐░▌▐░▌    ▐░▌▐░▌     ▐░▌          ▐░▌               ▐░▌");
        PrintHelper.printMR(row + 8, column,
                "▐░█▄▄▄▄▄▄▄▄▄ ▐░▌      ▐░▌  ▄▄▄▄█░█▄▄▄▄ ▐░▌       ▐░▌▐░▌     ▐░▐░▌     ▐░▌          ▐░▌      ▄▄▄▄▄▄▄▄▄█░▌");
        PrintHelper.printMR(row + 9, column,
                "▐░░░░░░░░░░░▌▐░▌       ▐░▌▐░░░░░░░░░░░▌▐░▌       ▐░▌▐░▌      ▐░░▌     ▐░▌          ▐░▌     ▐░░░░░░░░░░░▌");
        PrintHelper.printMR(row + 10, column,
                " ▀▀▀▀▀▀▀▀▀▀▀  ▀         ▀  ▀▀▀▀▀▀▀▀▀▀▀  ▀         ▀  ▀        ▀▀       ▀            ▀       ▀▀▀▀▀▀▀▀▀▀▀ ");
        PrintHelper.printMR(row + 11, column, "Authors: Alberto Nidasio, Matteo Pignataro, Alberto Sandri");
        PrintHelper.printMR(row + 12, column, "Original site with rules: https://craniointernational.com/products/eriantys/");
    }

    // Updates

    @Override
    public void displayAssistantCards(AssistantCardsUpdate update)
    {
        if (update != null)
        {
            assistantCards = update;
            PrintHelper.printMR(7, 25, update.toString());
        }
    }

    @Override
    public void displayCharacterCardPayload(CharacterCardPayloadUpdate update)
    {}

    @Override
    public void displayCharacterCards(CharacterCardsUpdate update)
    {
        if (update != null)
        {
            characterCards = update;
            PrintHelper.printMR(12, 2 + 33 * players.size() + 1, update.toString());
        }
    }

    @Override
    public void displayCloudTiles(CloudTilesUpdate update)
    {
        if (update != null)
        {
            cloudTiles = update;
            PrintHelper.printMR(7, 2, update.toString());
        }
    }

    @Override
    public void displayIslands(IslandsUpdate update)
    {
        if (update != null)
        {
            islands = update;
            PrintHelper.printMR(1, 2, update.toString());
        }
    }

    @Override
    public void displayPlayedAssistantCard(PlayedAssistantCardUpdate update)
    {
        if (update != null)
        {

            if (playedAssistantCards.keySet().contains(players.get(update.getPlayer())))
            {
                playedAssistantCards.replace(players.get(update.getPlayer()), update);
            } else
            {
                playedAssistantCards.put(players.get(update.getPlayer()), update);
            }

            PrintHelper.printMR(19, 2 + 33 * players.get(update.getPlayer()), update.toString());
        }
    }

    @Override
    public void displaySchoolboard(SchoolBoardUpdate update)
    {
        if (update != null)
        {
            if (!players.keySet().contains(update.getPlayer()))
                players.put(update.getPlayer(), update.getPlayerIndex());

            if (schoolBoards.keySet().contains(update.getPlayerIndex()))
                schoolBoards.replace(update.getPlayerIndex(), update);
            else
                schoolBoards.put(update.getPlayerIndex(), update);

            for (Integer playerIndex : schoolBoards.keySet())
                if (schoolBoards.get(playerIndex) != null)
                    PrintHelper.printMR(12, 2 + 33 * playerIndex, (playerIndex == currentPlayerIndex ? schoolBoards.get(playerIndex).toStringActive()
                            : schoolBoards.get(playerIndex).toString()));
        }
    }

    /**
     * Set the current player in the table order.
     *
     * @param update contains the current player.
     */
    @Override
    public void setCurrentPlayer(CurrentPlayerUpdate update)
    {
        if (update != null)
        {
            currentPlayerIndex = update.getCurrentPlayerIndex();

            for (String currentName : players.keySet())
                if (players.get(currentName) == currentPlayerIndex)
                {
                    if (name.equals(currentName))
                        PrintHelper.printMessage("It's your turn");
                    else PrintHelper.printMessage("It's " + currentName + "'s turn");
                    break;
                }

            // I need to call it because the current player could have changed
            displayBoard();
        }
    }

    // Answers

    @Override
    public void displayEndMatch(EndMatchAnswer answer)
    {
        PrintHelper.printMessage(answer.toString());
        clear();
    }

    @Override
    public void displayError(ErrorAnswer answer)
    {
        PrintHelper.printMessage(answer.toString());
    }

    @Override
    public void displayJoinedMatch(JoinedMatchAnswer answer)
    {
        PrintHelper.printMessage(answer.toString());
    }

    @Override
    public void displayMatchesList(MatchesListAnswer answer)
    {
        // Clear the screen
        PrintHelper.printMR(24, 1, PrintHelper.ERASE_FROM_CURSOR_TILL_BEGINNING_OF_SCREEN);
        printTitle(2, 2);

        matchesList = answer;

        if (!isMatchStarted)
        {
            PrintHelper.printMR(15, 2, "Matches list:");
            int counter = 0;

            // Create the string with all the matches
            for (String key : matchesList.getNumPlayers().keySet())
            {
                counter++;

                if (counter <= 8)
                    PrintHelper.printMR(15 + counter, 2, matchesList.singleMatchToString(key));
            }
        }
    }

    /**
     * Display all the matches not started yet. It is called when a player chooses 'Get matches list' in 'Command'.
     */
    public void displayMatchesList(Scanner scanner)
    {
        String msg = "[MatchesListAnswer] Matches list:\n";
        int counter = 0;

        // Clear all the terminal above the area where error are displayed
        PrintHelper.printMR(24, 1, PrintHelper.ERASE_FROM_CURSOR_TILL_BEGINNING_OF_SCREEN);

        // Create the string with all the matches
        for (String key : matchesList.getNumPlayers().keySet())
        {
            msg += matchesList.singleMatchToString(key);
            counter++;

            // Every 20 matches I display them and ask the player if wants to see more (if there are)
            // 20 matches in order to not relocate other elements in the terminal
            if (counter % 20 == 19 && counter != matchesList.getNumPlayers().size())
            {
                msg += "To see other matches type 'Y'\n";
                PrintHelper.printMR(1, 2, msg);
                if (scanner.nextLine().equals("Y"))
                {
                    msg = "";
                    PrintHelper.printMR(24, 1, PrintHelper.ERASE_FROM_CURSOR_TILL_BEGINNING_OF_SCREEN);
                } else
                    break;
            }
        }

        // At the end I print again the list of matches, it is necessary when there are less than 20 matches
        PrintHelper.printMR(24, 1, PrintHelper.ERASE_FROM_CURSOR_TILL_BEGINNING_OF_SCREEN);
        PrintHelper.printMR(1, 2, msg);
        PrintHelper.printMR(21, 1, PrintHelper.ERASE_ENTIRE_LINE);
    }

    @Override
    public void displaySetName(SetNameAnswer answer)
    {
        PrintHelper.printMessage(answer.toString());
        this.name = answer.getName();
    }

    @Override
    public void displayStartMatch(StartMatchAnswer answer)
    {
        if (!isMatchStarted)
        {
            isMatchStarted = true;
            PrintHelper.printMessage(answer.toString());

            if (answer.getPlayers().size() != 0)
                players = answer.getPlayers();
        }
    }

    public static void main(String[] args)
    {
        Client client = new Client();
        CLI cli = new CLI(client);
        client.setVisualizer(cli);

        cli.start();
    }

    /**
     * Display the entire board.
     */
    private void displayBoard()
    {
        // Clear the area
        PrintHelper.printMR(20, 1, PrintHelper.ERASE_FROM_CURSOR_TILL_BEGINNING_OF_SCREEN);

        if (assistantCards != null)
            PrintHelper.printMR(7, 25, assistantCards.toString());

        if (characterCards != null)
            PrintHelper.printMR(12, 2 + 33 * players.size() + 1, characterCards.toString());

        if (cloudTiles != null)
            PrintHelper.printMR(7, 2, cloudTiles.toString());

        if (islands != null)
            PrintHelper.printMR(1, 2, islands.toString());

        for (Integer playerIndex : playedAssistantCards.keySet())
            if (playerIndex != null && playedAssistantCards.get(playerIndex) != null)
                PrintHelper.printMR(19, 2 + 33 * playerIndex, playedAssistantCards.get(playerIndex).toString());

        for (Integer playerIndex : schoolBoards.keySet())
            if (playerIndex != null && schoolBoards.get(playerIndex) != null)
                PrintHelper.printMR(12, 2 + 33 * playerIndex, (playerIndex == currentPlayerIndex ? schoolBoards.get(playerIndex).toStringActive()
                        : schoolBoards.get(playerIndex).toString()));
    }

    /**
     * Method to call after every endMatchAnswer in order to clear the attributes of the previous match.
     */
    private void clear()
    {
        isMatchStarted = false;
        players.clear();
        assistantCards = null;
        characterCards = null;
        cloudTiles = null;
        islands = null;
        playedAssistantCards.clear();
        schoolBoards.clear();
        matchesList = null;
        currentPlayerIndex = 0;
    }
}
