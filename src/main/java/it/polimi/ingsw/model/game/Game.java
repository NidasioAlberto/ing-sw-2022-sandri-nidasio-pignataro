package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.*;

import java.util.*;
import java.util.stream.*;

public class Game
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

    protected GameMode gameMode;

    protected List<Island> islands;

    protected List<CloudTile> cloudTiles;

    protected List<Student> studentBag;

    protected List<CharacterCard> characterCards;

    protected List<Professor> professors;

    protected Optional<Integer> currentPlayerIndex;

    protected Optional<Integer> motherNatureIndex;

    protected Optional<Integer> currentCharacterCardIndex;

    protected boolean motherNatureMoved;

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
        if (playersNumber < 2 || playersNumber > 4)
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
    }

    /**
     * Adds a player to the current game. If the game already contains the maximum number of players
     * possible, an exception is thrown.
     */
    // TODO: Check the player's color against other players
    public void addPlayer(Player player) throws TooManyPlayersException
    {
        if (players.size() >= playersNumber)
            throw new TooManyPlayersException(players.size());
        players.add(player);
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
     * Returns the currently selected player, if any.
     * 
     * TODO: Forse non è il caso di esporre il player direttamente, e una copia non mi sembra il
     * caso. Magari dobbiamo ritornare il nickname?
     */
    public Optional<Player> getSelectedPlayer()
    {
        // Check if all the players selected a card
        //TODO CHECK THAT THIS MODIFY DIDN'T AFFECT OTHER CODE
        for(Player player : players)
        {
            if (player.getSelectedCard().isEmpty())
            {
                return currentPlayerIndex.map(index -> players.get(index));
            }
        }
        return currentPlayerIndex.map(index -> getSortedPlayerList().get(index));
    }

    /**
     * @return The currently selected player index referred to the tableList OR the sortedList
     * the controller will handle the difference and the game must refer to sortedList
     */
    public Optional<Integer> getSelectedPlayerIndex()
    {
        return currentPlayerIndex;
    }

    /**
     * Return the players list sorted by their turn order based on the played assistant cards.
     */
    // TODO: Ci deve essere un modo più bello per fare il sort e ritornare una nuova
    // lista.
    public List<Player> getSortedPlayerList() throws NoSuchElementException
    {
        List<Player> sortedList = new ArrayList<>(players);

        try
        {
            sortedList.sort((a, b) -> a.getSelectedCard().orElseThrow().getTurnOrder()
                    - b.getSelectedCard().orElseThrow().getTurnOrder());
        } catch (NoSuchElementException e)
        {
            throw new NoSelectedAssistantCardException("[Game]");
        }
        return sortedList;
    }

    /**
     * Returns the players list sorted by table order.
     */
    public List<Player> getPlayerTableList()
    {
        return new ArrayList<>(players);
    }

    /**
     * Puts the student passed via parameter inside the selected island. If the student or the
     * island are not found, an exception is thrown.
     * 
     * @param student The student to be added to the Entrance
     * @throws NoSuchElementException if a player or an island is not selected
     */
    public void putStudentToIsland(Student student) throws NoSuchElementException
    {
        // Move the student to the island
        try
        {
            islands.get(getSelectedPlayer()
                    .orElseThrow(() -> new NoSelectedPlayerException("[Game]"))
                    .getSelectedIsland()
                    .orElseThrow(() -> new NoSelectedIslandException("[Game]")))
                    .addStudent(student);
        } catch (IndexOutOfBoundsException e)
        {
            throw new IslandIndexOutOfBoundsException("[Game]");
        }
    }

    /**
     * Puts the student passed via parameter inside the dining room. If the student is null, an
     * exception is thrown.
     * 
     * @param student The student to be added to the Dining room
     * @throws NoSuchElementException if a player or an island is not selected
     */
    public void putStudentToDining(Student student)
            throws NoSuchElementException, NullPointerException
    {
        // Move the student
        getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[Game]"))
                .getBoard().addStudentToDiningRoom(student);
        // TODO forse qui va chiamata la conquerProfessor
    }

    /**
     * Takes the student from the selected player's entrance and removes it.
     * 
     * @return The student of the selected color
     */
    public Student pickStudentFromEntrance() throws NoSuchElementException
    {
        // Get the player selected color
        SchoolColor selectedColor = getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[Game]"))
                .getSelectedColors().stream().findFirst()
                .orElseThrow(() -> new NoSelectedColorException("[Game]"));

        // Get the student instance to be removed and returned
        Student result = getSelectedPlayer().get().getBoard().getStudentsInEntrance().stream()
                .filter(s -> s.getColor() == selectedColor).findFirst().orElseThrow(
                        () -> new NoSuchStudentInEntranceException("[Game]"));

        // Remove the instance from the entrance
        getSelectedPlayer().get().getBoard().removeStudentFromEntrance(result);

        return result;
    }

    /**
     * This method regulates the conquer of professors and should be called when a player moves some
     * student into his dining room.
     * 
     * @throws NoSuchElementException When no player is selected
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
                List<Player> sortedPlayers = players.stream()
                        .sorted((p1, p2) -> p2.getBoard().getStudentsNumber(color)
                                - p1.getBoard().getStudentsNumber(color))
                        .collect(Collectors.toList());

                // The player gets the professor only if he has the majority
                if (sortedPlayers.get(0).getBoard().getStudentsNumber(color) > sortedPlayers.get(1)
                        .getBoard().getStudentsNumber(color))
                {
                    // Remove the professor from the table
                    Professor toMove = professors.stream().filter(p -> p.getColor().equals(color))
                            .findFirst().orElse(null);
                    professors.remove(toMove);

                    // Add him to the player's board
                    sortedPlayers.get(0).getBoard().addProfessor(toMove);
                }
            } else
            {
                // Look for the player that has that professor
                Player currentKing = players.stream().filter(p -> p.getBoard().hasProfessor(color))
                        .findFirst().orElse(null);

                // Get the player with more students of the current color
                Player wannaBeKing =
                        players.stream()
                                .sorted((p1, p2) -> p2.getBoard().getStudentsNumber(color)
                                        - p1.getBoard().getStudentsNumber(color))
                                .findFirst().orElse(null);

                // If they are the same player there's nothing to do
                if (currentKing != wannaBeKing)
                {
                    // Move the student only if the wanna be king is not the current king and he has
                    // more
                    // students
                    if (wannaBeKing.getBoard().getStudentsNumber(color) > currentKing.getBoard()
                            .getStudentsNumber(color))
                    {
                        // I take the instance of the professor to be moved
                        Professor professor = currentKing.getBoard().getProfessors().stream()
                                .filter(p -> p.getColor().equals(color)).findFirst().get();

                        // Remove the professor from the current king
                        currentKing.getBoard().removeProfessor(professor);

                        // Add the professor to the new king
                        wannaBeKing.getBoard().addProfessor(professor);
                    }
                }
            }
        }
    }

    /**
     * Moves mother nature for the specified number of steps.
     */
    public void moveMotherNature(int steps)
    {
        // Change mother nature index
        int index = steps + motherNatureIndex.orElseThrow(() -> new NoSuchElementException(
                "[Game] Mother nature is not currently on the table, is the game set up?"));
        index %= islands.size();
        motherNatureIndex = Optional.of(index);

        // TODO va messo a false alla fine del turno
        motherNatureMoved = true;
    }

    /**
     * Method that tells the controller if the number of steps can be achieved by the selected
     * player.
     * 
     * @param steps the number of steps that needs to be checked
     * @return the validity of the operation
     */
    public boolean isValidMotherNatureMovement(int steps)
    {
        // I have to check if the current player can do this movement
        Player currentPlayer = getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[Game]"));
        AssistantCard selectedCard =
                currentPlayer.getSelectedCard().orElseThrow(() -> new NoSelectedAssistantCardException(
                        "[Game]"));

        return selectedCard.getSteps() >= steps && steps >= 1;
    }

    /**
     * Computes the influence on the island where the passed index points. This implies probably
     * moving towers.
     * 
     * @param island the island index where we compute the influence
     * @throws IndexOutOfBoundsException thrown when the island index is out of bounds
     */
    // TODO manca il merge delle isole quando possibile
    public void computeInfluence(int island) throws IndexOutOfBoundsException
    {
        if (island < 0 || island >= islands.size())
            throw new IslandIndexOutOfBoundsException("[Game]");

        Island currentIsland = islands.get(island);

        // TODO: Use Pair
        // Get the player with more influence, if there is any
        List<Player> sortedPlayers = players.stream().sorted(
                (p1, p2) -> computePlayerInfluence(p2, island) - computePlayerInfluence(p1, island))
                .collect(Collectors.toList());

        // Check if the first player has more influence than the second one
        if (computePlayerInfluence(sortedPlayers.get(0),
                island) > computePlayerInfluence(sortedPlayers.get(1), island))
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
            List<Tower> towersToAdd = influencer.getBoard().getTowers().subList(0,
                    Integer.max(towersToRemove.size(), influencer.getBoard().getTowers().size()));

            towersToAdd.forEach(t -> {
                // Remove the tower from the player's board
                influencer.getBoard().removeTower(t);

                // Add the tower to the island
                currentIsland.addTower(t);
            });
        }
    }

    /**
     * Calculates the influence where mother nature currently is.
     * 
     * @throws NoSuchElementException when mother nature is not set already
     */
    public void computeInfluence() throws NoSuchElementException
    {
        computeInfluence(motherNatureIndex.orElseThrow(() -> new NoSuchElementException(
                "[Game] No mother nature index, is the game initialized?")));
    }

    /**
     * Computes the given player influence for the island where mother nature currently is.
     * 
     * @throws NullPointerException if the given player is null
     * @throws IndexOutOfBoundsException thrown when the island index is out of bounds
     */
    public int computePlayerInfluence(Player player, int island)
            throws NullPointerException, IndexOutOfBoundsException
    {
        if (player == null)
            throw new NullPointerException("[Game] player null");

        if (island < 0 || island >= islands.size())
            throw new IslandIndexOutOfBoundsException("[Game]");

        Island currentIsland = islands.get(island);

        // Compute the influence of this player from students
        int influence = player.getBoard().getProfessors().stream()
                .map(p -> currentIsland.getStudentsByColor(p.getColor())).reduce(0, Integer::sum);

        // Add the influence from the towers
        influence += currentIsland.getTowers().stream()
                .filter(t -> t.getColor().equals(player.getColor())).count();

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
        CloudTile cloudTile = cloudTiles.get(getSelectedPlayer()
                .orElseThrow(() -> new NoSelectedPlayerException("[Game]"))
                .getSelectedCloudTile()
                .orElseThrow(() -> new NoSelectedCloudTileException("[Game]")));

        // Remove the students from the cloud tile
        List<Student> students = cloudTile.getStudents();
        cloudTile.removeStudents();

        // Put them in the current player's entrance
        students.forEach(s -> getSelectedPlayer().get().getBoard().addStudentToEntrance(s));
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
            if (i != motherNatureIndex.get()
                    && i != (motherNatureIndex.get() + ISLAND_TILES_NUMBER / 2)
                    && i != (motherNatureIndex.get() - ISLAND_TILES_NUMBER / 2))
                islands.get(i).addStudent(getStudentFromBag());
        });

        // 4. Populate the bag with the remaining students
        for (SchoolColor color : SchoolColor.values())
            IntStream.range(0, 24).forEach(i -> addStudentToBag(new Student(color)));

        // 5. Place the cloud tiles
        IntStream.range(0, players.size())
                .forEach(i -> cloudTiles.add(new CloudTile(CloudTileType.TILE_2_4)));

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

            IntStream.range(0, board.getMaxTowers())
                    .forEach(i -> board.addTower(new Tower(p.getColor())));
        });

        // 9. Each player gets a deck of cards
        // TODO: The wizard are assigned automatically! Is it correct?
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

        // If the game is in expert mode, create randomly 3 character cards
        if (gameMode.equals(GameMode.EXPERT))
        {
            List<CharacterCardType> types =
                    new ArrayList<>(Arrays.asList(CharacterCardType.values()));

            // Choose 3 random cards
            for (int j = 0; j < CHARACTER_CARDS_NUMBER; j++)
                characterCards.add(CharacterCard
                        .createCharacterCard(types.remove(getRandomNumber(0, types.size())), this));
        }
    }

    private int getRandomNumber(int startInclusive, int endExclusive)
    {
        return (int) Math
                .round(startInclusive + Math.random() * (endExclusive - startInclusive) - 0.5);
    }

    /**
     * Returns a random student from the bag.
     *
     * @return the student extracted from the bag.
     * @throws NoSuchElementException thrown if the student bag is empty
     */
    public Student getStudentFromBag() throws NoSuchElementException
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
     * This method clears the things that have to be cleared when a new turn begins
     * such as currentCharacterCard
     */
    public void clearTurn()
    {
        this.currentCharacterCardIndex = Optional.empty();
        this.motherNatureMoved = false;
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
            return Optional.of(characterCards.get(currentCharacterCardIndex.orElseThrow(
                    () -> new NoSelectedCharacterCardException("[Game]"))));
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
        return islands.get(motherNatureIndex.orElseThrow(() -> new NoSuchElementException(
                "[Game] No mother nature index, is the game initialized?")));
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
}
