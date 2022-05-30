package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.protocol.messages.ActionMessage;
import it.polimi.ingsw.protocol.messages.PlayAssistantCardMessage;

import java.sql.PseudoColumnUsage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This singleton class is basically a map of lambdas that the graphical game objects
 * use to translate the drag and drop movement into a network action.
 * Basically when an object is being dragged, it sets up a string inside
 * this object and, when an object has been dragged on, it calls a function passing
 * as parameter the eventual string of the second object
 */
public class ActionTranslator
{
    private final Map<String, Consumer<String>> lookupMap;

    /**
     * Private constructor for singleton purposes
     */
    private ActionTranslator()
    {
        // Create and initialize the map with all the possible actions
        lookupMap = new HashMap<>();

        lookupMap.put("AssistantCard", (secondObject) -> playAssistantCard(secondObject));
        lookupMap.put("Student", (secondObject) -> moveStudent(secondObject));
        lookupMap.put("Island", (secondObject) -> selectIsland(secondObject));
        lookupMap.put("NoEntry", (secondObject) -> moveNoEntry(secondObject));
        lookupMap.put("MotherNature", (secondObject) -> moveMotherNature(secondObject));
        lookupMap.put("CloutTile", (secondObject) -> selectCloudTile(secondObject));
        lookupMap.put("CharacterCard", (secondObject) -> playCharacterCard(secondObject));

    }

    private void playAssistantCard(String secondObject)
    {

    }

    private void moveStudent(String secondObject)
    {

    }

    public void selectIsland(String secondObject)

    {

    }

    private void moveNoEntry(String secondObject)
    {

    }

    private void moveMotherNature(String secondObject)
    {

    }

    private void selectCloudTile(String secondObject)
    {

    }

    private void playCharacterCard(String secondObject)
    {

    }

    public void endTurn()
    {

    }
}
