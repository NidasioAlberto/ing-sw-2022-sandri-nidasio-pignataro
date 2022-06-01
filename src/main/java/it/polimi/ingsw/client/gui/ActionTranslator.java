package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.model.SchoolColor;
import java.util.*;

/**
 * This singleton class is basically a map of lambdas that the graphical game objects use to translate the drag and drop movement into a network
 * action. Basically when an object is being dragged, it sets up a string inside this object and, when an object has been dragged on, it calls a
 * function passing as parameter the eventual string of the second object
 */
public class ActionTranslator
{
    // Singleton instance
    private static ActionTranslator instance;

    /**
     * Map of all the possible actions mapped with their corresponding functions
     */
    private final Map<String, Map<String, Runnable>> lookupMap;

    /**
     * The two actors that determine the behaviour
     */
    private String draggedItem;
    private String droppedOnItem;

    /**
     * All the possible registered data
     */
    private List<SchoolColor> selectedColors;
    private int selectedIsland;
    private int selectedCard;
    private int selectedCloudTile;

    /**
     * Private constructor for singleton purposes
     */
    private ActionTranslator()
    {
        // Create and initialize all the selected stuff
        selectedColors = new ArrayList<>();
        selectedIsland = -1;
        selectedCard = -1;
        selectedCloudTile = -1;

        draggedItem = "";
        droppedOnItem = "";

        // Create and initialize the map with all the possible actions
        lookupMap = new HashMap<>();

        // Create all the situations
        lookupMap.put("Student", new HashMap<>());
        lookupMap.get("Student").put("Island", () -> moveStudentToIsland());
        lookupMap.get("Student").put("Dining", () -> moveStudentToDining());
        lookupMap.get("Student").put("Student", () -> swapStudentFromEntranceToDining());
        lookupMap.get("Student").put("", () -> selectColor());

        lookupMap.put("CharacterStudent", new HashMap<>());
        lookupMap.get("CharacterStudent").put("Island", () -> moveStudentFromCharacterCardToIsland());
        lookupMap.get("CharacterStudent").put("Student", () -> swapStudentFromCharacterCardToEntrance());
        lookupMap.get("CharacterStudent").put("Dining", () -> moveStudentFromCharacterCardToDining());

        lookupMap.put("AssistantCard", new HashMap<>());
        lookupMap.get("AssistantCard").put("", () -> playAssistantCard());

        lookupMap.put("MotherNature", new HashMap<>());
        lookupMap.get("MotherNature").put("Island", () -> moveMotherNature());

        lookupMap.put("CloudTile", new HashMap<>());
        lookupMap.get("CloudTile").put("", () -> selectCloudTile());

        lookupMap.put("CharacterCard", new HashMap<>());
        lookupMap.get("CharacterCard").put("", () -> playCharacterCard());

        lookupMap.put("NoEntryTile", new HashMap<>());
        lookupMap.get("NoEntryTile").put("Island", () -> moveNoEntryFromCharacterCardToIsland());

        lookupMap.put("Island", new HashMap<>());
        lookupMap.get("Island").put("", () -> selectIsland());
    }

    public void setDraggedItem(String draggedItem)
    {
        if (draggedItem == null)
            throw new NullPointerException("[ActionTranslator] Null dragged item");
        this.draggedItem = draggedItem;
    }

    public void setDroppedOnItem(String droppedOnItem)
    {
        if (droppedOnItem == null)
            throw new NullPointerException("[ActionTranslator] Null dropped on item");
        this.droppedOnItem = droppedOnItem;
    }

    /**
     * Method to act the corresponding function
     */
    public void execute()
    {
        // Check the strings
        if (draggedItem != null && droppedOnItem != null)
            Objects.requireNonNull(lookupMap.get(draggedItem).get(droppedOnItem)).run();

        // At the end i clear all the selections
        clear();
    }

    /**
     * Method to clear all the selections
     */
    public void clear()
    {
        // Clear all the selections
        selectedColors.clear();
        selectedCloudTile = -1;
        selectedCard = -1;
        selectedIsland = -1;

        // Clear also the selection strings
        draggedItem = "";
        droppedOnItem = "";
    }

    /**
     * PARAMETER SET METHODS
     */
    public void selectColor(SchoolColor color)
    {
        if (color == null)
            throw new NullPointerException("[ActionTranslator] Null school color");

        selectedColors.add(color);
    }

    public void selectIsland(int island)
    {
        if (island < 0)
            throw new IllegalArgumentException("[ActionTranslator] Illegal island selection");

        selectedIsland = island;
    }

    public void selectCard(int card)
    {
        if (card < 0)
            throw new IllegalArgumentException("[ActionTranslator] Illegal card selection");

        selectedCard = card;
    }

    public void selectCloudTile(int cloud)
    {
        if (cloud < 0)
            throw new IllegalArgumentException("[ActionTranslator] Illegal cloud tile selection");

        selectedCloudTile = cloud;
    }

    /**
     * ACTION METHODS
     */
    private void moveStudentToIsland()
    {
        System.out.println("Move student to island");
    }

    private void moveStudentToDining()
    {
        System.out.println("Move student to dining");
    }

    private void swapStudentFromEntranceToDining()
    {
        System.out.println("Swap student from entrance to dining");
    }

    private void selectColor()
    {
        System.out.println("Select color");
    }

    private void moveStudentFromCharacterCardToIsland()
    {
        System.out.println("Move student from character card to island");
    }

    private void swapStudentFromCharacterCardToEntrance()
    {
        System.out.println("Swap student from character card to entrance");
    }

    private void moveStudentFromCharacterCardToDining()
    {
        System.out.println("Move student from character card to dining");
    }

    private void playAssistantCard()
    {
        System.out.println("Play assistant card");
    }

    private void moveMotherNature()
    {
        System.out.println("Move mother nature");
    }

    private void selectCloudTile()
    {
        System.out.println("Select cloud tile");
    }

    private void playCharacterCard()
    {
        System.out.println("Play character card");
    }

    private void moveNoEntryFromCharacterCardToIsland()
    {
        System.out.println("Move no entry tile from character card to island");
    }

    private void selectIsland()
    {
        System.out.println("Select island");
    }

    /**
     * The only public function. It is called by the end Turn button
     */
    public void endTurn()
    {

    }

    /**
     * Static method to get the singleton instance
     */
    public static ActionTranslator getInstance()
    {
        if (instance == null)
            instance = new ActionTranslator();
        return instance;
    }
}
