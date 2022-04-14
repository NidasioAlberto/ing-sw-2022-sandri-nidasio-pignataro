package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.exceptions.NotEnoughCoinsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MushroomManTest
{
    Game game;
    CharacterCard mushroomMan;
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
        mushroomMan = CharacterCard.createCharacterCard(CharacterCardType.MUSHROOM_MAN, game);
    }

    @Test
    public void constructorTest()
    {
        //Null pointers inside the factory method
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(null, game));
        assertThrows(NullPointerException.class, () -> CharacterCard.createCharacterCard(CharacterCardType.MUSHROOM_MAN, null));

        //Type confirmation
        assertEquals(CharacterCardType.MUSHROOM_MAN, mushroomMan.getCardType());

        //Cost
        assertEquals(3, mushroomMan.cost);

        //Not already used
        assertEquals(false, mushroomMan.firstUsed);
        assertEquals(false, mushroomMan.isActivated());
    }

    @Test
    public void generalCardTest()
    {
        // If we don't select a player we expect noSuchElementException
        assertThrows(NoSuchElementException.class, () -> mushroomMan.activate());

        // Player selection and card activation
        game.selectPlayer(0);

        // If i activate the card with no coins i should get an error
        assertThrows(NotEnoughCoinsException.class, () -> mushroomMan.activate());

        // Add the coins to activate the card
        player1.addCoins(10);

        try
        {
            mushroomMan.activate();
        } catch (Exception e)
        {
        }

        // After activation the player should have 3 coin less
        assertEquals(7, player1.getCoins());

        // The card should have the activation flag to true and also the first used flag
        assertEquals(true, mushroomMan.firstUsed);
        assertEquals(true, mushroomMan.isActivated());
        // The card should cost now 4 coins
        assertEquals(4, mushroomMan.cost);

        // If i try to activate the card while already activated nothing should happen
        try
        {
            mushroomMan.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, mushroomMan.firstUsed);
        assertEquals(true, mushroomMan.isActivated());
        assertEquals(4, mushroomMan.cost);
        assertEquals(7, player1.getCoins());

        // Finally if I deactivate the card the cost will be 4 coins
        mushroomMan.deactivate();

        try
        {
            mushroomMan.activate();
        } catch (Exception e)
        {
        }

        assertEquals(true, mushroomMan.firstUsed);
        assertEquals(true, mushroomMan.isActivated());
        assertEquals(4, mushroomMan.cost);
        assertEquals(3, player1.getCoins());
    }

    @Test
    public void isPlayableTest()
    {
        //The card should be playable because mother nature has not been moved yet
        assertEquals(true, mushroomMan.isPlayable());

        //If i move mother nature, the card should not be playable anymore
        game.motherNatureMoved = true;

        assertEquals(false, mushroomMan.isPlayable());
    }

    @Test
    public void isValidActionTest()
    {
        //I need to verify that only the color selection action is valid with no selected color
        for(ExpertGameAction action : ExpertGameAction.values())
        {
            if(action == ExpertGameAction.SELECT_COLOR)
                assertEquals(true, mushroomMan.isValidAction(action));
            else
                assertEquals(false, mushroomMan.isValidAction(action));
        }

        //Select color
        mushroomMan.activated = true;
        player1.selectColor(SchoolColor.RED);
        game.selectPlayer(0);
        mushroomMan.applyAction();

        //Now i verify that it only accepts the basic actions
        for(ExpertGameAction action : ExpertGameAction.values())
        {
            if(action == ExpertGameAction.ACTION_BASE)
                assertEquals(true, mushroomMan.isValidAction(action));
            else
                assertEquals(false, mushroomMan.isValidAction(action));
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
        try { mushroomMan.activate();}
        catch(Exception e){}

        //I expect to receive a IndexOutOfBoundsException because of no selected color
        assertThrows(IndexOutOfBoundsException.class, () -> mushroomMan.applyAction());

        //So i select the color and apply the action
        player1.selectColor(SchoolColor.RED);
        mushroomMan.applyAction();

        //Because mother nature has not been moved yet, i expect the card to be still active
        assertEquals(true, mushroomMan.isActivated());

        //Now i move mother nature
        game.motherNatureMoved = true;

        mushroomMan.applyAction();

        //I expect the card to be deactivated
        assertEquals(false, mushroomMan.isActivated());
    }

    @Test
    public void computePlayerInfluenceTest()
    {
        //Call the method with invalid parameters
        mushroomMan.activated = true;
        assertThrows(IndexOutOfBoundsException.class, () -> mushroomMan.computePlayerInfluence(player1, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> mushroomMan.computePlayerInfluence(player1, 20));
        assertThrows(NullPointerException.class, () -> mushroomMan.computePlayerInfluence(null, 0));
        assertThrows(NoSuchElementException.class, () -> mushroomMan.computePlayerInfluence(player1, 0));
        mushroomMan.activated = false;

        //I setup the bench
        Professor removedProfessor = game.removeProfessor(0);
        player1.getBoard().addProfessor(removedProfessor);
        player1.getBoard().addProfessor(game.removeProfessor(0));

        //Verify that the not activated card calculates the influence as game
        for(int i = 0; i < game.islands.size(); i++)
        {
            assertEquals(game.computePlayerInfluence(player1, i), mushroomMan.computePlayerInfluence(player1, i));
            //Add another student with different color to the islands to demonstrate that only the selected color matters
            if(game.islands.get(i).getStudentsByColor(removedProfessor.getColor()) > 0)
            {
                int colorIndex;
                //Take a different color
                for(colorIndex = 0; colorIndex < SchoolColor.values().length && SchoolColor.values()[colorIndex] == removedProfessor.getColor(); colorIndex++);

                //Add that color to the island
                game.islands.get(i).addStudent(new Student(SchoolColor.values()[colorIndex]));
            }
        }

        //Now i activate the card and select the color
        mushroomMan.activated = true;
        player1.selectColor(removedProfessor.getColor());
        game.selectPlayer(0);
        mushroomMan.applyAction();

        //At this point there should be 2 islands where the influence computation should be different
        int differentInfluence = 0;
        for(int i = 0; i < game.islands.size(); i++)
        {
            if(game.computePlayerInfluence(player1, i) != mushroomMan.computePlayerInfluence(player1, i))
            {
                assertEquals(game.islands.get(i).getStudents().get(0).getColor(), removedProfessor.getColor());
                assertEquals(game.computePlayerInfluence(player1, i) - 1, mushroomMan.computePlayerInfluence(player1, i));
                differentInfluence++;
            }
        }

        //Verify that the difference is 2
        assertEquals(2, differentInfluence);
    }
}
