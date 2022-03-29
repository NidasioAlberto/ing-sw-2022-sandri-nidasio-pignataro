package it.polimi.ingsw.model;

import it.polimi.ingsw.model.character.*;
import it.polimi.ingsw.model.exceptions.TooManyPlayersException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;

public class Game
{
    public static final int MAX_PLAYERS = 4;
    public static final int ISLAND_TILES_NUMBER = 12;
    public static final int ASSISTANT_CARDS_DECK_SIZE = 10;

    /**
     * List of all the players in the game in table order (as they are added to the game).
     */
    protected List<Player> players;

    protected List<Island> islands;

    protected List<CloudTile> cloudTiles;

    protected List<Student> studentBag;

    protected List<CharacterCard> characterCards;

    protected List<Professor> professors;

    protected Optional<Integer> currentPlayerIndex;

    protected Optional<Integer> motherNatureIndex;

    protected Optional<Integer> currentCharacterCardIndex;

    protected Optional<GameAction> previousAction;

    public Game()
    {
        players = new ArrayList<>();
        islands = new ArrayList<>();
        cloudTiles = new ArrayList<>();
        studentBag = new ArrayList<>();
        characterCards = new ArrayList<>();
        professors = new ArrayList<>();
        currentPlayerIndex = Optional.of(null);
        motherNatureIndex = Optional.of(null);
        currentCharacterCardIndex = Optional.of(null);
        previousAction = Optional.of(null);
    }

    /**
     * Adds a player to the current game. If the game already contains the maximum number of players
     * possible, an exception is thrown.
     */
    public void addPlayer(Player player) throws TooManyPlayersException
    {
        if (players.size() > MAX_PLAYERS)
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
        return currentPlayerIndex.map(index -> players.get(index));
    }

    /**
     * TODO: Cosa dovrebbe fare?
     */
    public void setupCloudTiles()
    {}

    /**
     * Return the players list sorted by their turn order based on the played assistant cards. TODO:
     * Ci deve essere un modo più bello per fare il sort e ritornare una nuova lista.
     */
    public List<Player> getSortedPlayerList() throws NoSuchElementException
    {
        List<Player> sortedList = new ArrayList<>(players);

        try
        {
            sortedList.sort((a, b) -> a.getCardsList().get(a.getSelectedCard().orElseThrow())
                    .getTurnOrder()
                    - b.getCardsList().get(b.getSelectedCard().orElseThrow()).getTurnOrder());
        } catch (NoSuchElementException e)
        {
            throw new NoSuchElementException("[Game] One of the players have not selected a card");
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
     * Puts the student passed via parameter inside the selected island.
     * If the student or the island are not found, an exception is thrown.
     * @param student The student to be added to the Entrance
     * @throws NoSuchElementException if a player or an island is not selected
     */
    public void putStudentToIsland(Student student) throws NoSuchElementException
    {
        // Move the student to the island
        try
        {
            islands.get(getSelectedPlayer().orElseThrow(
                    () -> new NoSuchElementException("[Game] No selected player"))
                    .getSelectedIsland()
                    .orElseThrow(
                            () -> new NoSuchElementException("[Game] No selected island")))
                    .addStudent(student);
        } catch (IndexOutOfBoundsException e)
        {
            throw new NoSuchElementException("[Game] There is no island with the selected index");
        }
    }

    /**
     * Puts the student passed via parameter inside the dining room.
     * If the student or the island are not found, an exception is thrown.
     * @param student The student to be added to the Dining room
     * @throws NoSuchElementException if a player or an island is not selected
     */
    public void putStudentToDining(Student student) throws NoSuchElementException, NullPointerException
    {
        // Move the student
        getSelectedPlayer().orElseThrow(
                () -> new NoSuchElementException("[Game] No selected player")
        ).getBoard().addStudentToDiningRoom(student);
    }

    /**
     * Takes the student from the selected player's entrance and removes it
     * @return The student of the selected color
     */
    public Student pickStudentFromEntrance() throws NoSuchElementException
    {
        //Get the player selected color
        SchoolColor selectedColor = getSelectedPlayer().orElseThrow(
                () -> new NoSuchElementException("[Game] No selected player")
                ).getSelectedColors().stream().findFirst().orElseThrow(
                () -> new NoSuchElementException("[Game] No selected color")
        );

        //Get the student instance to be removed and returned
        Student result = getSelectedPlayer().get()
                .getBoard().getStudentsInEntrance().stream()
                .filter(s -> s.getColor() == selectedColor)
                .findFirst().orElseThrow(
                () -> new NoSuchElementException("[Game] No selected student in entrance")
        );

        //Remove the instance from the entrance
        getSelectedPlayer().get().getBoard().removeStudentFromEntrance(result);

        return result;
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
    }

    /**
     * Computes the influence on the island where mother nature currently is. This implies probably
     * moving towers.
     */
    public void computeInfluence()
    {
        Island currentIsland =
                islands.get(motherNatureIndex.orElseThrow(() -> new NoSuchElementException(
                        "[Game] Mother nature is not currently on the table, is the game set up?")));

        // TODO: Use Pair
        // Get the player with more influence, if there is any
        List<Player> sortedPlayers = players.stream()
                .sorted((p1, p2) -> computePlayerInfluence(p1) - computePlayerInfluence(p2))
                .toList();
        if (computePlayerInfluence(sortedPlayers.get(0)) > computePlayerInfluence(
                sortedPlayers.get(1)))
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
     * Computes the given player influence for the island where mother nature currently is.
     */
    private int computePlayerInfluence(Player player)
    {
        Island currentIsland =
                islands.get(motherNatureIndex.orElseThrow(() -> new NoSuchElementException(
                        "[Game] Mother nature is not currently on the table, is the game set up?")));

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
     * @throws NoSuchElementException Thrown if the player or the cloud tile is not selected
     */
    public void moveStudentsFromCloudTile() throws NoSuchElementException
    {
        CloudTile cloudTile = cloudTiles.get(getSelectedPlayer()
                .orElseThrow(() -> new NoSuchElementException(
                "[Game] Unable to get the current player, is a player selected?"))
                .getSelectedCloudTile().orElseThrow(
                        () -> new NoSuchElementException("[Game] No Cloud Tile selected")
                ));

        // Remove the students from the cloud tile
        List<Student> students = cloudTile.getStudentsList();
        cloudTile.removeStudents();

        // Put them in the current player's entrance
        students.forEach(s -> getSelectedPlayer().get().getBoard().addStudentToEntrance(s));
    }

    /**
     * Tells whether the given action can be played in the current game status.
     */
    public boolean isValidAction(GameAction action)
    {
        // A player must be selected all al cases
        Optional<Player> currentPlayer = getSelectedPlayer();
        if (currentPlayer.isEmpty())
            return false;

        switch (action)
        {
            case PLAY_ASSISTANT_CARD:
            {
                // To play an assistant card no previous action has to be played
                if (previousAction.isPresent())
                    return false;

                // The player must have not already selected a assistant card
                return currentPlayer.get().getSelectedCard().isEmpty();
            }
            case MOVE_STUDENT_FROM_ENTRANCE_TO_DINING:
            case MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND:
            {
                // An action must have already been played
                if (previousAction.isEmpty())
                    return false;

                // The previous action must be one of the 3 following action
                GameAction prevAction = previousAction.get();
                if (!prevAction.equals(GameAction.PLAY_ASSISTANT_CARD)
                        && !prevAction.equals(GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING)
                        && !prevAction.equals(GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND))
                    return false;

                // The player must not already have moved all the allowed students
                SchoolBoard currentBoard = currentPlayer.get().getBoard();
                return currentBoard.getRemainingMovableStudentsInEntrance() != 0;
            }
            case MOVE_MOTHER_NATURE:
            {
                // An action must have already been played
                if (previousAction.isEmpty())
                    return false;

                // The previous action must be one of the 2 following action
                GameAction prevAction = previousAction.get();
                if (!prevAction.equals(GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_DINING)
                        && !prevAction.equals(GameAction.MOVE_STUDENT_FROM_ENTRANCE_TO_ISLAND))
                    return false;

                // The player must have moved all of its students
                SchoolBoard currentBoard = currentPlayer.get().getBoard();
                return currentBoard.getRemainingMovableStudentsInEntrance() == 0;
            }
            case SELECT_CLOUD_TILE:
            {
                // An action must have already been played
                if (previousAction.isEmpty())
                    return false;

                // The previous must be MOVE_MOTHER_NATURE
                GameAction prevAction = previousAction.get();
                return prevAction.equals(GameAction.MOVE_MOTHER_NATURE);
            }
            case PLAY_CHARACTER_CARD:
                //TODO REMEMBER TO CLEAR THIS OPTIONAL EVERY PLAYER CHANGE
                if(currentCharacterCardIndex.isEmpty())
                    return true;
                else
                    return false;
            default:
                return false;
        }
    }

    /**
     * Sets up all the game's components.
     */
    public void setupGame()
    {
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

        // TODO: Check if mother nature is there or in the opposite island
        IntStream.range(0, ISLAND_TILES_NUMBER).forEach(i -> islands.get(i)
                .addStudent(getStudentFromBag()));

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
        // TODO: We suppose that at this method call the players are already populated

        // 8. Each players takes 8 or 6 towers
        players.forEach(p -> {
            SchoolBoard board = p.getBoard();

            IntStream.range(0, board.MAX_TOWERS)
                    .forEach(i -> board.addTower(new Tower(p.getColor())));
        });

        // 9. Each player gets a deck of cards
        // TODO: The wizard are assigned automatically! Is it correct?
        players.forEach(p -> {
            IntStream.range(0, ASSISTANT_CARDS_DECK_SIZE).forEach(i -> p.addCard(
                    new AssistantCard(Wizard.values()[players.indexOf(p)], i + 1, i / 2 + 1)));
        });

        // 10. Each player gets 7 or 9 students in his entrance
        players.forEach(p -> {
            IntStream.range(0, players.size() != 3 ? 7 : 9).forEach(i -> {
                p.getBoard().addStudentToEntrance(getStudentFromBag());
            });
        });
    }

    private int getRandomNumber(int startInclusive, int endExclusive)
    {
        return (int) Math
                .round(startInclusive + Math.random() * (startInclusive - endExclusive) - 0.5);
    }

    //TODO forse dovrebbe lanciare una eccezione quando sono finiti gli studenti
    // così si sa che la partita deve terminare
    /**
     * Returns a random student from the bag.
     *
     * @return the student extracted from the bag.
     */
    public Student getStudentFromBag()
    {
        return studentBag.remove(getRandomNumber(0, studentBag.size()));
    }

    /**
     * Add a student to the bag.
     *
     * @param student to be added to the bag.
     */
    public void addStudentToBag(Student student)
    {
        studentBag.add(student);
    }

    /**
     * TODO!
     */
    public Optional<GameAction> getGameAction()
    {
        return previousAction;
    }

    /**
     * Return a list of the available character cards in the game.
     */
    public List<CharacterCard> getCharacterCards()
    {
        return new ArrayList<>(characterCards);
    }

    /**
     * Returns the currently selected character card.
     */
    public Optional<CharacterCard> getCurrentCharacterCard()
    {
        try
        {
            return Optional.of(characterCards
                    .get(currentCharacterCardIndex.orElseThrow(() -> new NoSuchElementException(
                            "[Game] Mother nature is not currently on the table, is the game set up?"))));
        } catch (IndexOutOfBoundsException e)
        {
            return Optional.empty();
        }
    }
}
