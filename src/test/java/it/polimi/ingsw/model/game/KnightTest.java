package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.EndGameException;
import it.polimi.ingsw.model.exceptions.IslandIndexOutOfBoundsException;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.network.Match;
import it.polimi.ingsw.network.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class KnightTest
{
    Game game;
    CharacterCard knight;
    Player player1;
    Player player2;
    SchoolBoard board1;
    SchoolBoard board2;

    @BeforeEach
    public void init()
    {
        // I have to initialize a Game, a Player and a School Board to ensure
        // the character card can behave correctly
        board1 = new SchoolBoard(TowerColor.BLACK, GameMode.EXPERT);
        board2 = new SchoolBoard(TowerColor.WHITE, GameMode.EXPERT);
        player1 = new Player("pippo", board1);
        player2 = new Player("peppo", board2);
        game = new Game(2, GameMode.EXPERT);

        // Add the player to the game
        try
        {
            game.addPlayer(player1);
            game.addPlayer(player2);
        } catch (Exception e)
        {
        }

        // Setup the game
        Match match = new Match(new Server(), "test", 2, GameMode.CLASSIC);
        game.subscribe(match);
        game.setupGame();

        // Now i can instantiate the character card
        knight = CharacterCard.createCharacterCard(CharacterCardType.KNIGHT, game);
        knight.init();
    }

    @Test
    public void constructorTest()
    {
        // Null pointers inside the factory method
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(null, game));
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(CharacterCardType.KNIGHT, null));

        // Type confirmation
        assertEquals(CharacterCardType.KNIGHT, knight.getCardType());

        // Cost
        assertEquals(2, knight.cost);

        // Not already used
        assertEquals(false, knight.firstUsed);
        assertEquals(false, knight.isActivated());
    }

    @Test
    public void generalCardTest()
    {
        // If we don't select a player we expect noSuchElementException
        assertThrows(NoSuchElementException.class, () -> knight.activate());

        // Player selection and card activation
        game.selectPlayer(0);

        // If i activate the card with no coins i should get an error
        assertThrows(NotEnoughCoinsException.class, () -> knight.activate());

        // Add the coins to activate the card
        player1.getBoard().addCoins(10);

        try
        {
            knight.activate();
        } catch (Exception e)
        {
        }

        // After activation the player should have 2 coin less
        assertEquals(9, player1.getBoard().getCoins());

        // The card should have the activation flag to true and also the first used flag
        assertEquals(true, knight.firstUsed);
        assertEquals(true, knight.isActivated());
        // The card should cost now 3 coins
        assertEquals(3, knight.cost);

        // If i try to activate the card while already activated nothing should happen
        try
        {
            knight.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, knight.firstUsed);
        assertEquals(true, knight.isActivated());
        assertEquals(3, knight.cost);
        assertEquals(9, player1.getBoard().getCoins());

        // Finally if I deactivate the card the cost will be 3 coins
        knight.deactivate();

        try
        {
            knight.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, knight.firstUsed);
        assertEquals(true, knight.isActivated());
        assertEquals(3, knight.cost);
        assertEquals(6, player1.getBoard().getCoins());
    }


    @Test
    public void isPlayableTest()
    {
        // The card should be playable because mother nature has not been moved yet
        assertEquals(true, knight.isPlayable());

        // If i move mother nature, the card should not be playable anymore
        game.motherNatureMoved = true;

        assertEquals(false, knight.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        // I verify that all the expert actions are rejected except the base action one
        for (ExpertGameAction action : ExpertGameAction.values())
        {
            if (action == ExpertGameAction.BASE_ACTION)
                assertEquals(true, knight.isValidAction(action));
            else
                assertEquals(false, knight.isValidAction(action));
        }
    }

    @Test
    public void applyActionTest()
    {
        // This card doesn't actually have an action, so it should deactivate once
        // mother nature has been moved

        // I active the card
        game.selectPlayer(0);
        player1.getBoard().addCoins(10);
        knight.activated = true;

        // Now the card should be active and i can call applyAction.
        knight.applyAction();

        // Because mother nature has not been moved yet, i expect the card to be still active
        assertEquals(true, knight.isActivated());

        // Now i move mother nature
        game.motherNatureMoved = true;

        knight.applyAction();

        // I expect the card to be deactivated
        assertEquals(false, knight.isActivated());
    }

    @Test
    public void computePlayerInfluenceTest()
    {
        // Call the method with invalid parameters
        assertThrows(IndexOutOfBoundsException.class, () -> knight.computePlayerInfluence(player1, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> knight.computePlayerInfluence(player1, 20));
        assertThrows(NullPointerException.class, () -> knight.computePlayerInfluence(null, 0));

        // For each island the player influence should be +2 the actual influence when the card is
        // inactive

        // Check the deactivated status
        for (int i = 0; i < game.islands.size(); i++)
        {
            assertEquals(knight.computePlayerInfluence(player1, i), game.computePlayerInfluence(player1, i));
        }

        // Now i activate the card and check again
        game.selectPlayer(0);
        player1.getBoard().addCoins(10);
        knight.activated = true;

        // If i deselect the player the card should give me an error
        game.currentPlayerIndex = Optional.empty();

        assertThrows(NoSuchElementException.class, () -> knight.computePlayerInfluence(player1, 0));

        // Now i reselect the player
        game.selectPlayer(0);

        // Should all be +2
        for (int i = 0; i < game.islands.size(); i++)
        {
            assertEquals(knight.computePlayerInfluence(player1, i), game.computePlayerInfluence(player1, i) + 2);
        }
    }

    @Test
    public void computeInfluenceTest()
    {
        knight.activated = false;

        assertThrows(IslandIndexOutOfBoundsException.class, () -> knight.computeInfluence(12));
        Island island = game.getCurrentIsland();
        int islandIndex = game.getIslands().indexOf(island);

        // Save the current status
        List<Tower> towers = island.getTowers();

        // The game is set up and mother nature has been positioned
        // Each player should have the same influence, hence no tower movement has to be
        // performed
        assertDoesNotThrow(() -> knight.computeInfluence());
        for (int i = 0; i < towers.size(); i++)
            assertEquals(towers.get(i), game.getCurrentIsland().getTowers().get(i));

        // Now put a tower on the island
        game.getCurrentIsland().addTower(new Tower(TowerColor.BLACK));
        towers = island.getTowers();

        // Check the influences
        assertEquals(1, knight.computePlayerInfluence(player1, islandIndex));
        assertEquals(0, knight.computePlayerInfluence(player2, islandIndex));

        // The player should have more influence but still nothing should move
        assertDoesNotThrow(() -> knight.computeInfluence());
        for (int i = 0; i < towers.size(); i++)
            assertEquals(towers.get(i).getColor(), game.getCurrentIsland().getTowers().get(i).getColor());

        // At the beginning there are 12 islands
        assertEquals(12, game.getIslands().size());

        // Put a tower on island next to the current one
        Island nextIsland =
                game.getIslands().get(game.getMotherNatureIndex().get() == game.islands.size() - 1 ? 0 : game.getMotherNatureIndex().get() + 1);
        nextIsland.addTower(new Tower(TowerColor.BLACK));

        knight.computeInfluence();
        islandIndex = islandIndex == 11 ? 10 : islandIndex;
        // Check the influences
        assertEquals(2, knight.computePlayerInfluence(player1, islandIndex));
        assertEquals(0, knight.computePlayerInfluence(player2, islandIndex));

        assertEquals(11, game.getIslands().size());

        // Add a noEntryTile and the computeInfluence
        game.getIslands().get(islandIndex).addNoEntryTile();
        assertEquals(1, game.getIslands().get(islandIndex).getNoEntryTiles());
        knight.computeInfluence(islandIndex);
        assertEquals(0, game.getIslands().get(islandIndex).getNoEntryTiles());

        // Player1 adds a student and conquers that professor
        player1.getBoard().addStudentToDiningRoom(new Student(SchoolColor.BLUE));
        game.conquerProfessors();

        // Add a student of the same color on an island
        int previousIslandIndex = islandIndex < 2 ? game.getIslands().size() - 2 : islandIndex - 2;
        game.getIslands().get(previousIslandIndex).addStudent(new Student(SchoolColor.BLUE));

        // Remove all the towers form player1, except 1, so that when the influence is computed
        // an EndGameException will be thrown
        for (int i = 0; i < player1.getBoard().getMaxTowers() - 2; i++)
            player1.getBoard().removeTower(TowerColor.BLACK);
        assertThrows(EndGameException.class, () -> knight.computeInfluence(previousIslandIndex));

        // Add a tower to player1 because has finished them
        player1.getBoard().addTower(new Tower(TowerColor.BLACK));

        // Player2 adds a student and conquers that professor
        player2.getBoard().addStudentToDiningRoom(new Student(SchoolColor.GREEN));
        game.conquerProfessors();

        // Put some student of that color on the previous island so that player2 has more influence there
        game.getIslands().get(previousIslandIndex).addStudent(new Student(SchoolColor.GREEN));
        game.getIslands().get(previousIslandIndex).addStudent(new Student(SchoolColor.GREEN));
        game.getIslands().get(previousIslandIndex).addStudent(new Student(SchoolColor.GREEN));
        game.getIslands().get(previousIslandIndex).addStudent(new Student(SchoolColor.GREEN));

        // Remove all the towers form player2, except 1, so that when the influence is computed
        // an EndGameException will be thrown
        for (int i = 0; i < player2.getBoard().getMaxTowers() - 1; i++)
            player2.getBoard().removeTower(TowerColor.WHITE);
        assertThrows(EndGameException.class, () -> knight.computeInfluence(previousIslandIndex));
    }
}
