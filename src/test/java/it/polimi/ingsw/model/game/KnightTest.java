package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.ExpertGameAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.SchoolBoard;
import it.polimi.ingsw.model.TowerColor;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        board1 = new SchoolBoard(TowerColor.BLACK);
        board2 = new SchoolBoard(TowerColor.WHITE);
        player1 = new Player("pippo", board1);
        player2 = new Player("peppo", board2);
        game = new Game();

        // Add the player to the game
        try {
            game.addPlayer(player1);
            game.addPlayer(player2);
        } catch (Exception e) {
        }

        // Setup the game
        game.setupGame();

        // Now i can instantiate the character card
        knight = CharacterCard.createCharacterCard(CharacterCardType.KNIGHT, game);
    }

    @Test
    public void constructorTest()
    {
        //Null pointers inside the factory method
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(null, game));
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(CharacterCardType.KNIGHT, null));

        //Type confirmation
        assertEquals(CharacterCardType.KNIGHT, knight.getCardType());

        //Cost
        assertEquals(2, knight.cost);

        //Not already used
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
        player1.addCoins(10);

        try
        {
            knight.activate();
        } catch (Exception e)
        {
        }

        // After activation the player should have 2 coin less
        assertEquals(8, player1.getCoins());

        // The card should have the activation flag to true and also the first used flag
        assertEquals(true, knight.firstUsed);
        assertEquals(true, knight.isActivated());
        // The card should cost now 2 coins
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
        assertEquals(8, player1.getCoins());

        // Finally if I deactivate the card the cost will be 2 coins
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
        assertEquals(5, player1.getCoins());
    }


    @Test
    public void isPlayableTest()
    {
        //The card should be playable because mother nature has not been moved yet
        assertEquals(true, knight.isPlayable());

        //If i move mother nature, the card should not be playable anymore
        game.motherNatureMoved = true;

        assertEquals(false, knight.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        //I verify that all the expert actions are rejected except the base action one
        for(ExpertGameAction action : ExpertGameAction.values())
        {
            if(action == ExpertGameAction.ACTION_BASE)
                assertEquals(true, knight.isValidAction(action));
            else
                assertEquals(false, knight.isValidAction(action));
        }
    }

    @Test
    public void applyActionTest()
    {
        //This card doesn't actually have an action, so it should deactivate once
        //mother nature has been moved

        //I active the card
        game.selectPlayer(0);
        player1.addCoins(10);
        try { knight.activate();}
        catch(Exception e){}

        //Now the card should be active and i can call applyAction.
        knight.applyAction();

        //Because mother nature has not been moved yet, i expect the card to be still active
        assertEquals(true, knight.isActivated());

        //Now i move mother nature
        game.motherNatureMoved = true;

        knight.applyAction();

        //I expect the card to be deactivated
        assertEquals(false, knight.isActivated());
    }

    @Test
    public void computePlayerInfluenceTest()
    {
        //Call the method with invalid parameters
        assertThrows(IndexOutOfBoundsException.class, () -> knight.computePlayerInfluence(player1, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> knight.computePlayerInfluence(player1, 20));
        assertThrows(NullPointerException.class, () -> knight.computePlayerInfluence(null, 0));

        //For each island the player influence should be +2 the actual influence when the card is inactive

        //Check the deactivated status
        for(int i = 0; i < game.islands.size(); i++)
        {
            assertEquals(knight.computePlayerInfluence(player1, i), game.computePlayerInfluence(player1, i));
        }

        //Now i activate the card and check again
        game.selectPlayer(0);
        player1.addCoins(10);
        try { knight.activate();}
        catch(Exception e){}

        // If i deselect the player the card should give me an error
        game.currentPlayerIndex = Optional.empty();

        assertThrows(NoSuchElementException.class, () -> knight.computePlayerInfluence(player1, 0));

        //Now i reselect the player
        game.selectPlayer(0);

        //Should all be +2
        for(int i = 0; i < game.islands.size(); i++)
        {
            assertEquals(knight.computePlayerInfluence(player1, i), game.computePlayerInfluence(player1, i) + 2);
        }
    }
}
