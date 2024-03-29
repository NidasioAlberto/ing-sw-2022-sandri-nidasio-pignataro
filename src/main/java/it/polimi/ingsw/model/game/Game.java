package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.protocol.updates.*;

import java.util.*;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Publisher;
import java.util.stream.*;

/**
 * This class represents a game played by the users.
 */
public class Game implements Publisher<ModelUpdate>
{
    public static final int ISLAND_TILES_NUMBER = 12;
    public static final int ASSISTANT_CARDS_DECK_SIZE = 10;
    public static final int CHARACTER_CARDS_NUMBER = 3;

    /**
     * List of all the players in the game in table order (as they are added to the game).
     */
    protected List<Player> players;

    /**
     * Number of players needed for the game.
     */
    private Integer playersNumber;

    /**
     * It is used to sort accurately the players based on turnOrder when two players play the same card.
     */
    private int bestPreviousPlayerIndex;

    protected GameMode gameMode;

    protected List<Island> islands;

    protected List<CloudTile> cloudTiles;

    protected List<Student> studentBag;

    protected List<CharacterCard> characterCards;

    protected List<Professor> professors;

    protected Optional<Integer> currentPlayerIndex;

    protected int currentPlayerIndexByTable;

    protected Optional<Integer> motherNatureIndex;

    protected Optional<Integer> currentCharacterCardIndex;

    protected boolean motherNatureMoved;

    protected Optional<Subscriber<? super ModelUpdate>> subscriber;

    public Game() throws NullPointerException
    {
        this(2, GameMode.CLASSIC);
    }

    /**
     * Constructor.
     * 
     * @param playersNumber Number of players for the game
     * @throws NullPointerException Thrown if the number of players passed is null
     */
    public Game(Integer playersNumber, GameMode gameMode) throws NullPointerException
    {
        if (playersNumber == null)
            throw new NullPointerException("[SchoolBoard] Null players number");
        if (playersNumber < 2 || playersNumber > 3)
            throw new IllegalArgumentException("[SchoolBoard] Invalid players number");

        players = new ArrayList<>();
        this.playersNumber = playersNumber;
        this.gameMode = gameMode;
        islands = new ArrayList<>();
        cloudTiles = new ArrayList<>();
        studentBag = new ArrayList<>();
        characterCards = new ArrayList<>();
        professors = new ArrayList<>();
        currentPlayerIndex = Optional.empty();
        motherNatureIndex = Optional.empty();
        currentCharacterCardIndex = Optional.empty();
        motherNatureMoved = false;
        subscriber = Optional.empty();
        bestPreviousPlayerIndex = 0;
    }

    /**
     * Adds a player to the current game. If the game already contains the maximum number of players possible, an exception is thrown.
     */
    public void addPlayer(Player player) throws TooManyPlayersException
    {
        if (players.size() >= playersNumber)
            throw new TooManyPlayersException(players.size());
        players.add(player);
    }

    /**
     * Remove a player from the game if not started yet.
     *
     * @param playerName of the player to remove.
     */
    public void removePlayer(String playerName)
    {
        // Check the game hasn't started yet and there is at least one player
        if (players.size() < playersNumber && players.size() > 0)
        {
            // Find the player to remove and remove it or throw an exception
            Player playerToRemove = players.stream().filter(player -> player.getNickname().equals(playerName)).findFirst()
                    .orElseThrow(() -> new NoSelectedPlayerException("[Game]"));
            players.remove(playerToRemove);
        }
    }

    /**
     * Changes the selected player. If the index is invalid, an exception is thrown.
     */
    public void selectPlayer(Integer index) throws IndexOutOfBoundsException
    {
        try
        {
            players.get(index);
        } catch (IndexOutOfBoundsException e)
        {
            throw new IndexOutOfBoundsException("[Game] The specified player index is not valid");
        }

        currentPlayerIndex = Optional.of(index);
    }

    /**
     * Set the index of the current player in the table order. An update with this index is sent to the players.
     *
     * @param currentPlayerIndexByTable The index of the current player in the table order.
     */
    public void setCurrentPlayerIndexByTable(int currentPlayerIndexByTable)
    {
        this.currentPlayerIndexByTable = currentPlayerIndexByTable;

        if (subscriber.isPresent())
        {
            subscriber.get().onNext(new CurrentPlayerUpdate(currentPlayerIndexByTable));
        }
    }

    /**
     * Returns the currently selected player, if any. The player could be disconnected.
     */
    public Optional<Player> getSelectedPlayer()
    {
        return currentPlayerIndex.map(index -> getSortedPlayerList().get(index));
    }

    /**
     * @return The currently selected player index referred to the tableList OR the sortedList the controller will handle the difference and the game
     *         must refer to sortedList
     */
    public Optional<Integer> getSelectedPlayerIndex()
    {
        return currentPlayerIndex;
    }

    /**
     * Return the players list sorted by their turn order based on the played assistant cards. In case of same turnOrder plays first the player that
     * has played that card first. The size of the returned list is always the players number, but maybe some players are no more connected or have
     * never played a card, so you may have to check these things.
     */
    public List<Player> getSortedPlayerList() throws NoSuchElementException
    {
        List<Player> sortedList = new ArrayList<>(players);

        // If a player hasn't selected a card, its turnOrder will be 10, in order to be last
        sortedList.sort((a,
                b) -> a.getSelectedCard().orElse(new AssistantCard(Wizard.WIZARD_1, 10, 5)).getTurnOrder() == b.getSelectedCard()
                        .orElse(new AssistantCard(Wizard.WIZARD_1, 10, 5)).getTurnOrder() ? computeDistance(a) - computeDistance(b)
                                : a.getSelectedCard().orElse(new AssistantCard(Wizard.WIZARD_1, 10, 5)).getTurnOrder()
                                        - b.getSelectedCard().orElse(new AssistantCard(Wizard.WIZARD_1, 10, 5)).getTurnOrder());

        return sortedList;
    }

    /**
     * Compute the clockwise distance of the given player from the player that played the first assistant card the turn before.
     * 
     * @param player whose distance you want to calculate.
     * @return the distance.
     */
    private int computeDistance(Player player)
    {
        int currIndex = getPlayerTableList().indexOf(player);

        return currIndex - bestPreviousPlayerIndex >= 0 ? currIndex - bestPreviousPlayerIndex : playersNumber - bestPreviousPlayerIndex + currIndex;
    }

    /**
     * Returns the players list sorted by table order.
     */
    public List<Player> getPlayerTableList()
    {
        return new ArrayList<>(players);
    }

    /**
     * Puts the student passed via parameter inside the selected island. If the student or the island are not found, an exception is thrown.
     * 
     * @param student The student to be added to the Entrance
     * @throws NoSuchElementException if a player or an island is not selected
     */
    public void putStudentToIsland(Student student) throws NoSuchElementException
    {
        // Move the student to the island
        try
        {
            islands.get(getSelectedPlayer().orElseThrow(() -> new NoSelectedPlayerException("[Game]")).getSelectedIsland()
                    .orElseThrow(() -> new NoSelectedIslandException("[Game]"))).addStudent(student);
        } catch (IndexOutOfBoundsException e)
        {
            throw new IslandIndexOutOfBoundsException("[Game]");
        }

        // After the model update i have to notify the observer with island update
        if (subscriber.isPresent())
            subscriber.get().onNext(new IslandsUpdate(new ArrayList<Island>(islands),
                    motherNatureIndex.orElseThrow(() -> new NoSuchElementException("[Game] No mother nature index, is the game setup?"))));
    }

    /**
     * Puts the student passed via parameter inside the dining room. If the student is null, an exception is thrown.
     * 
     * @param student The student to be added to the Dining room
     * @throws NoSuchElementException if a player or an island is not selected
     */
    public void putStudentToDining(Student student) throws NoSuchElementException, NullPointerException
    {
        // Move the student
        getSelectedPlayer().orElseThrow(() -> new NoSelectedPlayerException("[Game]")).getBoard().addStudentToDiningRoom(student);

        conquerProfessors();

        // After the model update i have to notify the observer with the schoolboards update
        // and i need to send all of them because of conquer professors
        if (subscriber.isPresent())
        {
            for (Player player : players)
                subscriber.get().onNext(new SchoolBoardUpdate(player.getBoard(), player.getNickname(), players.indexOf(player)));
        }
    }

    /**
     * Takes the student from the selected player's entrance and removes it.
     * 
     * @return The student of the selected color
     */
    public Student pickStudentFromEntrance() throws NoSuchElementException
    {
        // Get the selected player
        Player selectedPlayer = getSelectedPlayer().orElseThrow(() -> new NoSelectedPlayerException("[Game]"));

        // Get the player selected color
        SchoolColor selectedColor = selectedPlayer.getSelectedColors().stream().findFirst().orElseThrow(() -> new NoSelectedColorException("[Game]"));

        // Get the student instance to be removed and returned
        Student result = getSelectedPlayer().get().getBoard().getStudentsInEntrance().stream().filter(s -> s.getColor() == selectedColor).findFirst()
                .orElseThrow(() -> new NoSuchStudentInEntranceException("[Game]"));

        // Remove the instance from the entrance
        getSelectedPlayer().get().getBoard().removeStudentFromEntrance(result);

        // After the model update i have to notify the observer with the schoolboard.
        // i must do it also in here because if the students goes into an island we should have
        // notification
        // of the schoolboard where it was before.
        if (subscriber.isPresent())
            subscriber.get().onNext(new SchoolBoardUpdate(selectedPlayer.getBoard(), selectedPlayer.getNickname(), players.indexOf(selectedPlayer)));

        return result;
    }

    /**
     * This method regulates the conquer of professors and should be called when a player moves some student into his dining room.
     * 
     * @throws NoSuchElementException When no player is selected or no professor is found
     */
    public void conquerProfessors() throws NoSuchElementException
    {
        // Check for every professor if there is a player who can have it
        for (SchoolColor color : SchoolColor.values())
        {
            // If the professor is still on the table, assign it to the player with the most
            // students, if there is any
            if (professors.stream().filter(p -> p.getColor().equals(color)).count() > 0)
            {
                // Get the player with more students of the current color
                List<Player> sortedPlayers =
                        players.stream().sorted((p1, p2) -> p2.getBoard().getStudentsNumber(color) - p1.getBoard().getStudentsNumber(color))
                                .collect(Collectors.toList());

                // The player gets the professor only if he has the majority
                if (sortedPlayers.get(0).getBoard().getStudentsNumber(color) > sortedPlayers.get(1).getBoard().getStudentsNumber(color))
                {
                    // Remove the professor from the table
                    Professor toMove = professors.stream().filter(p -> p.getColor().equals(color)).findFirst().orElse(null);
                    professors.remove(toMove);

                    // Add him to the player's board
                    sortedPlayers.get(0).getBoard().addProfessor(toMove);
                }
            } else
            {
                // Look for the player that has that professor
                Player currentKing = players.stream().filter(p -> p.getBoard().hasProfessor(color)).findFirst()
                        .orElseThrow(() -> new NoSuchElementException("[Game] No professor everywhere, is the game setup?"));

                // Get the player with more students of the current color
                Player wannaBeKing = players.stream()
                        .sorted((p1, p2) -> p2.getBoard().getStudentsNumber(color) - p1.getBoard().getStudentsNumber(color)).findFirst().orElse(null);

                // If they are the same player there's nothing to do
                if (currentKing != wannaBeKing)
                {
                    // Move the student only if the wanna be king is not the current king and he has
                    // more
                    // students
                    if (wannaBeKing.getBoard().getStudentsNumber(color) > currentKing.getBoard().getStudentsNumber(color))
                    {
                        // I take the instance of the professor to be moved
                        Professor professor =
                                currentKing.getBoard().getProfessors().stream().filter(p -> p.getColor().equals(color)).findFirst().get();

                        // Remove the professor from the current king
                        currentKing.getBoard().removeProfessor(professor);

                        // Add the professor to the new king
                        wannaBeKing.getBoard().addProfessor(professor);
                    }
                }
            }
        }

        // NO notify because the call is covered in putStudentToDining
    }

    /**
     * Moves mother nature for the specified number of steps.
     */
    public void moveMotherNature(int steps)
    {
        // Change mother nature index
        int index = steps + motherNatureIndex
                .orElseThrow(() -> new NoSuchElementException("[Game] Mother nature is not currently on the table, is the game set up?"));
        index %= islands.size();
        motherNatureIndex = Optional.of(index);

        // It is set to false at the end of each turn
        motherNatureMoved = true;

        // After moving mother nature we need to notify the observer
        if (subscriber.isPresent())
            subscriber.get().onNext(new IslandsUpdate(new ArrayList<Island>(islands), motherNatureIndex.get()));
    }

    /**
     * Method that tells the controller if the number of steps can be achieved by the selected player.
     * 
     * @param steps the number of steps that needs to be checked
     * @return the validity of the operation
     */
    public boolean isValidMotherNatureMovement(int steps)
    {
        // I have to check if the current player can do this movement
        Player currentPlayer = getSelectedPlayer().orElseThrow(() -> new NoSelectedPlayerException("[Game]"));
        AssistantCard selectedCard = currentPlayer.getSelectedCard().orElseThrow(() -> new NoSelectedAssistantCardException("[Game]"));

        return selectedCard.getSteps() >= steps && steps >= 1;
    }

    /**
     * Computes the influence on the island where the passed index points. This implies probably moving towers.
     * 
     * @param island the island index where we compute the influence
     * @throws IndexOutOfBoundsException thrown when the island index is out of bounds
     */
    public void computeInfluence(int island) throws IndexOutOfBoundsException
    {
        // Flag to check if a player has finished he towers
        boolean towerFinished = false;

        if (island < 0 || island >= islands.size())
            throw new IslandIndexOutOfBoundsException("[Game]");

        Island currentIsland = islands.get(island);

        // Check if the island has a no entry tile
        // and if so i delete it and update the virtual view
        if (currentIsland.getNoEntryTiles() > 0)
        {
            currentIsland.removeNoEntryTile();

            // If the subscriber is present i have to notify
            if (subscriber.isPresent())
            {
                subscriber.get().onNext(new IslandsUpdate(new ArrayList<Island>(islands), motherNatureIndex.get()));

                // I update also all the character cards payload
                for (CharacterCard card : characterCards)
                    card.notifySubscriber();
            }
            return;
        }

        // Get the player with more influence, if there is any
        List<Player> sortedPlayers = players.stream().sorted((p1, p2) -> computePlayerInfluence(p2, island) - computePlayerInfluence(p1, island))
                .collect(Collectors.toList());

        // Check if the first player has more influence than the second one
        if (computePlayerInfluence(sortedPlayers.get(0), island) > computePlayerInfluence(sortedPlayers.get(1), island))
        {
            // This player has more influence then all others
            Player influencer = sortedPlayers.get(0);

            // Remove all currently placed towers on the island
            List<Tower> towersToRemove = currentIsland.getTowers();
            currentIsland.removeAllTowers();

            // Move every tower to its original player's board
            towersToRemove.forEach(t -> {
                // Find the tower's player and put the tower in his board
                players.forEach(p -> {
                    if (p.getColor().equals(t.getColor()))
                        p.getBoard().addTower(t);
                });
            });

            // Move the influencer's towers to the island
            List<Tower> towersToAdd =
                    influencer.getBoard().getTowers().subList(0, Integer.min(towersToRemove.size(), influencer.getBoard().getTowers().size()));

            if (towersToAdd.size() == 0)
            {
                try
                {
                    // The island has no tower
                    Tower towerToMove = influencer.getBoard().getTowers().get(0);
                    currentIsland.addTower(towerToMove);
                    influencer.getBoard().removeTower(towerToMove);
                } catch (EndGameException e)
                {
                    // The exception is thrown if a player finishes the towers
                    towerFinished = true;
                }

            } else
            {
                try
                {
                    towersToAdd.forEach(t -> {
                        // Add the tower to the island
                        currentIsland.addTower(t);

                        // Remove the tower from the player's board
                        influencer.getBoard().removeTower(t);
                    });
                } catch (EndGameException e)
                {
                    // The exception is thrown if a player finishes the towers
                    towerFinished = true;
                }
            }

            // Check if there are islands that can be merged
            for (int i = 0; i < islands.size(); i++)
            {
                Island currIsland = islands.get(i);
                Island nextIsland = islands.get((i + 1) % islands.size());

                // Check if two consecutive islands have the same color of towers
                if (currIsland.getTowers().size() > 0 && nextIsland.getTowers().size() > 0
                        && currIsland.getTowers().get(0).getColor() == nextIsland.getTowers().get(0).getColor())
                {
                    // Mother must step back if it is placed after the current island or
                    // if it is on the last island or if the current island is the last one
                    if (motherNatureIndex.get() > islands.indexOf(currIsland) || motherNatureIndex.get() == (islands.size() - 1)
                            || islands.indexOf(currIsland) == (islands.size() - 1))
                    {
                        // In case of 0 index, mother nature goes to islands.size - 2 because we will remove an island
                        motherNatureIndex =
                                getMotherNatureIndex().get() == 0 ? Optional.of(islands.size() - 2) : Optional.of(getMotherNatureIndex().get() - 1);
                    }

                    // Merge the two islands and remove one
                    currIsland.mergeIsland(nextIsland);
                    islands.remove(nextIsland);

                    // There could be three consecutive islands that could be merged
                    i--;
                }
            }

            // After moving the towers we need to notify the observer
            if (subscriber.isPresent())
            {
                // I do that for all the players and not just the 2 because it is much simpler
                // and in the bast case we send 2 boards instead of 4
                for (Player player : players)
                    subscriber.get().onNext(new SchoolBoardUpdate(player.getBoard(), player.getNickname(), players.indexOf(player)));

                subscriber.get().onNext(new IslandsUpdate(new ArrayList<Island>(islands), motherNatureIndex.get()));
            }

            // If there are only 3 islands or a player has finished the towers the game ends
            if (islands.size() <= 3 || towerFinished)
                throw new EndGameException("[Game]");
        }
    }

    /**
     * Calculates the influence where mother nature currently is.
     * 
     * @throws NoSuchElementException when mother nature is not set already
     */
    public void computeInfluence() throws NoSuchElementException
    {
        computeInfluence(motherNatureIndex.orElseThrow(() -> new NoSuchElementException("[Game] No mother nature index, is the game initialized?")));
    }

    /**
     * Computes the given player influence for the island where mother nature currently is.
     * 
     * @throws NullPointerException if the given player is null
     * @throws IndexOutOfBoundsException thrown when the island index is out of bounds
     */
    public int computePlayerInfluence(Player player, int island) throws NullPointerException, IndexOutOfBoundsException
    {
        if (player == null)
            throw new NullPointerException("[Game] player null");

        if (island < 0 || island >= islands.size())
            throw new IslandIndexOutOfBoundsException("[Game]");

        Island currentIsland = islands.get(island);

        // Compute the influence of this player from students
        int influence = player.getBoard().getProfessors().stream().map(p -> currentIsland.getStudentsByColor(p.getColor())).reduce(0, Integer::sum);

        // Add the influence from the towers
        influence += currentIsland.getTowers().stream().filter(t -> t.getColor().equals(player.getColor())).count();

        return influence;
    }

    /**
     * Moves the students from the cloud tile selected by the current player to his entrance.
     * 
     * @throws IndexOutOfBoundsException Thrown if a tile cannot be found
     * @throws NoSuchElementException Thrown if the player or the cloud tile is not selected
     */
    public void moveStudentsFromCloudTile() throws NoSuchElementException
    {
        // Take the selected player
        Player selectedPlayer = getSelectedPlayer().orElseThrow(() -> new NoSelectedPlayerException("[Game]"));

        CloudTile cloudTile = cloudTiles.get(selectedPlayer.getSelectedCloudTile().orElseThrow(() -> new NoSelectedCloudTileException("[Game]")));

        // Remove the students from the cloud tile
        List<Student> students = cloudTile.getStudents();
        cloudTile.removeStudents();

        // Put them in the current player's entrance
        students.forEach(s -> getSelectedPlayer().get().getBoard().addStudentToEntrance(s));

        // At the end i notify the observer
        if (subscriber.isPresent())
        {
            subscriber.get().onNext(new SchoolBoardUpdate(selectedPlayer.getBoard(), selectedPlayer.getNickname(), players.indexOf(selectedPlayer)));
            subscriber.get().onNext(new CloudTilesUpdate(new ArrayList<CloudTile>(cloudTiles)));
        }
    }

    /**
     * Sets up all the game's components.
     */
    public void setupGame() throws NotEnoughPlayersException
    {
        // 0. Check if all the expected players have been added to the game
        if (players.size() != playersNumber)
            throw new NotEnoughPlayersException();

        // 1. Place the islands
        IntStream.range(0, ISLAND_TILES_NUMBER).forEach(i -> islands.add(new Island()));

        // 2. Place mother nature on a random spot
        motherNatureIndex = Optional.of(getRandomNumber(0, ISLAND_TILES_NUMBER));

        // 3. Place students on the islands
        studentBag.add(new Student(SchoolColor.BLUE));
        studentBag.add(new Student(SchoolColor.BLUE));
        studentBag.add(new Student(SchoolColor.GREEN));
        studentBag.add(new Student(SchoolColor.GREEN));
        studentBag.add(new Student(SchoolColor.PINK));
        studentBag.add(new Student(SchoolColor.PINK));
        studentBag.add(new Student(SchoolColor.RED));
        studentBag.add(new Student(SchoolColor.RED));
        studentBag.add(new Student(SchoolColor.YELLOW));
        studentBag.add(new Student(SchoolColor.YELLOW));
        IntStream.range(0, ISLAND_TILES_NUMBER).forEach((i) -> {
            if (i != motherNatureIndex.get() && i != (motherNatureIndex.get() + ISLAND_TILES_NUMBER / 2)
                    && i != (motherNatureIndex.get() - ISLAND_TILES_NUMBER / 2))
                islands.get(i).addStudent(getStudentFromBag());
        });

        // 4. Populate the bag with the remaining students
        for (SchoolColor color : SchoolColor.values())
            IntStream.range(0, 24).forEach(i -> addStudentToBag(new Student(color)));

        // 5. Place the cloud tiles
        IntStream.range(0, players.size()).forEach(i -> cloudTiles
                .add(playersNumber == 3 ? new CloudTile(CloudTileType.TILE_3_PLAYERS) : new CloudTile(CloudTileType.TILE_2_4_PLAYERS)));

        // 6. Place the professors
        for (SchoolColor color : SchoolColor.values())
            professors.add(new Professor(color));

        // 7. Each player takes a school board when they are added to the game
        // When setupGame is called, all the players must already have a board
        for (Player player : players)
            player.getBoard().setPlayersNumber(playersNumber);

        // 8. Each player takes 8 or 6 towers
        players.forEach(p -> {
            SchoolBoard board = p.getBoard();

            IntStream.range(0, board.getMaxTowers()).forEach(i -> board.addTower(new Tower(p.getColor())));
        });

        // 9. Each player gets a deck of cards
        players.forEach(p -> {
            IntStream.range(0, ASSISTANT_CARDS_DECK_SIZE).forEach(i -> {
                p.addCard(new AssistantCard(Wizard.values()[players.indexOf(p)], i + 1, i / 2 + 1));
            });
        });

        // 10. Each player gets 7 or 9 students in his entrance
        players.forEach(p -> {
            IntStream.range(0, playersNumber != 3 ? 7 : 9).forEach(i -> {
                p.getBoard().addStudentToEntrance(getStudentFromBag());
            });
        });

        // At the end of setup phase i notify the observer
        if (subscriber.isPresent())
        {
            subscriber.get().onNext(new IslandsUpdate(new ArrayList<Island>(islands), motherNatureIndex.get()));
            subscriber.get().onNext(new CloudTilesUpdate(new ArrayList<CloudTile>(cloudTiles)));
            for (Player player : players)
            {
                subscriber.get().onNext(new SchoolBoardUpdate(player.getBoard(), player.getNickname(), players.indexOf(player)));
                subscriber.get().onNext(new AssistantCardsUpdate(player.getNickname(), new ArrayList<AssistantCard>(player.getCards())));
            }
        }

        // If the game is in expert mode, create randomly 3 character cards
        if (gameMode.equals(GameMode.EXPERT))
        {
            List<CharacterCardType> types = new ArrayList<>(Arrays.asList(CharacterCardType.values()));

            // UNCOMMENT IF YOU WANT TO FORCE A CERTAIN TYPE OF CARD
            // characterCards.add(CharacterCard.createCharacterCard(CharacterCardType.GRANDMA_HERBS, this));
            // types.remove(CharacterCardType.GRANDMA_HERBS);

            // Choose 3 random cards
            for (int j = 0; j < CHARACTER_CARDS_NUMBER; j++)
                characterCards.add(CharacterCard.createCharacterCard(types.remove(getRandomNumber(0, types.size())), this));

            // Init all the random cards
            for (CharacterCard card : characterCards)
                card.init();

            // If we are in expert mode i can send notify the observer
            if (subscriber.isPresent())
            {
                List<CharacterCard> characterCardsList = new ArrayList<>();

                for (CharacterCard card : characterCards)
                    // I clone all the character card to avoid serializing the game instance
                    characterCardsList.add((CharacterCard) card.clone());

                subscriber.get().onNext(new CharacterCardsUpdate(new ArrayList<CharacterCard>(characterCardsList)));
                // Notify eventually about the payload situation
                for (CharacterCard card : characterCards)
                    card.notifySubscriber();
            }
        }
    }

    private int getRandomNumber(int startInclusive, int endExclusive)
    {
        return (int) Math.round(startInclusive + Math.random() * (endExclusive - startInclusive) - 0.5);
    }

    /**
     * Returns a random student from the bag.
     *
     * @return the student extracted from the bag.
     * @throws NoSuchElementException thrown if the student bag is empty
     */
    public Student getStudentFromBag()
    {
        if (studentBag.size() == 0)
            throw new EndGameException("[Game] Student bag empty");

        return studentBag.remove(getRandomNumber(0, studentBag.size()));
    }

    /**
     * Add a student to the bag.
     *
     * @param student to be added to the bag.
     */
    public void addStudentToBag(Student student) throws NullPointerException
    {
        if (student == null)
            throw new NullPointerException("[Game] Can't add a null student to the bag");
        studentBag.add(student);
    }

    /**
     * Fills all the cloud tiles with students from the bag. It should be called at the beginning of each round.
     */
    public void fillClouds()
    {
        int studentsToPut = playersNumber == 3 ? 4 : 3;

        for (CloudTile cloud : cloudTiles)
        {
            if (cloud.getStudents().size() == 0)
                for (int i = 0; i < studentsToPut; i++)
                    try
                    {
                        cloud.addStudent(getStudentFromBag());
                    } catch (EndGameException e)
                    {
                    }
        }

        // At the end i notify the observer
        if (subscriber.isPresent())
            subscriber.get().onNext(new CloudTilesUpdate(new ArrayList<CloudTile>(cloudTiles)));
    }

    /**
     * Removes a professor from the list
     * 
     * @param index the professor to be removed
     * @return the removed professor
     * @throws IndexOutOfBoundsException thrown if the index is out of bounds
     */
    public Professor removeProfessor(int index) throws IndexOutOfBoundsException
    {
        if (index < 0 || index >= professors.size())
            throw new IndexOutOfBoundsException("[Game] professor index out of bounds");
        return professors.remove(index);
    }

    /**
     * This method clears the things that have to be cleared when a new turn begins such as currentCharacterCard and the fact that mother nature has
     * moved.
     */
    public void clearTurn()
    {
        clearCharacterCard();
        this.motherNatureMoved = false;
        bestPreviousPlayerIndex = getPlayerTableList().indexOf(getSortedPlayerList().get(0));
    }

    /**
     * Method to clear the current character card. It is used by the controller when a player tries to activate two character cards in the same turn.
     */
    public void clearCharacterCard()
    {
        this.currentCharacterCardIndex = Optional.empty();
    }

    /**
     * Return a list of the available character cards in the game.
     */
    public List<CharacterCard> getCharacterCards()
    {
        return new ArrayList<>(characterCards);
    }

    /**
     * Sets the current character card index.
     */
    public void setCurrentCharacterCard(Integer characterCardIndex)
    {
        currentCharacterCardIndex = Optional.of(characterCardIndex);
    }

    /**
     * Returns the currently selected character card.
     * 
     * @throws NoSuchElementException thrown if there is no character card selected
     */
    public Optional<CharacterCard> getCurrentCharacterCard() throws NoSuchElementException
    {
        try
        {
            return Optional.of(characterCards.get(currentCharacterCardIndex.orElseThrow(() -> new NoSelectedCharacterCardException("[Game]"))));
        } catch (NoSelectedCharacterCardException e)
        {
            return Optional.empty();
        }
    }

    public List<Island> getIslands()
    {
        return new ArrayList<Island>(islands);
    }

    /**
     * @return A copy of the current professors list
     */
    public List<Professor> getProfessors()
    {
        return new ArrayList<Professor>(professors);
    }

    public List<CloudTile> getCloudTiles()
    {
        return new ArrayList<CloudTile>(cloudTiles);
    }

    public List<Student> getStudentBag()
    {
        return new ArrayList<Student>(studentBag);
    }

    public Island getCurrentIsland() throws NoSuchElementException
    {
        return islands
                .get(motherNatureIndex.orElseThrow(() -> new NoSuchElementException("[Game] No mother nature index, is the game initialized?")));
    }

    public Optional<Integer> getMotherNatureIndex()
    {
        return motherNatureIndex;
    }

    public GameMode getGameMode()
    {
        return gameMode;
    }

    public int getPlayersNumber()
    {
        return playersNumber;
    }

    public void notifyPlayers()
    {
        if (subscriber.isPresent())
        {
            for (Player player : players)
            {
                subscriber.get().onNext(new SchoolBoardUpdate(player.getBoard(), player.getNickname(), players.indexOf(player)));
                player.notifySubscriber();
            }

            subscriber.get().onNext(new CurrentPlayerUpdate(currentPlayerIndexByTable));
            subscriber.get().onNext(new IslandsUpdate(new ArrayList<Island>(islands),
                    motherNatureIndex.orElseThrow(() -> new NoSuchElementException("[Game] No mother nature index, is the game setup?"))));
            subscriber.get().onNext(new CloudTilesUpdate(new ArrayList<CloudTile>(cloudTiles)));

            if (gameMode == GameMode.EXPERT)
            {
                List<CharacterCard> characterCardsList = new ArrayList<>();

                for (CharacterCard card : characterCards)
                {
                    // I clone all the character card to avoid serializing the game instance
                    characterCardsList.add((CharacterCard) card.clone());
                }

                subscriber.get().onNext(new CharacterCardsUpdate(new ArrayList<CharacterCard>(characterCardsList)));

                for (CharacterCard card : characterCards)
                {
                    // I update also all the character cards payload
                    card.notifySubscriber();
                }
            }
        }
    }

    @Override
    public void subscribe(Subscriber<? super ModelUpdate> subscriber)
    {
        if (subscriber == null)
            throw new NullPointerException("[Game] Null subscriber");

        // I subscribe the observer to the observable only if doesn't already exist one
        if (this.subscriber.isEmpty())
            this.subscriber = Optional.of(subscriber);
    }
}
