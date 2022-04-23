package it.polimi.ingsw.controller.messages;

import org.json.JSONObject;
import it.polimi.ingsw.controller.GameActionHandler;
import it.polimi.ingsw.model.BaseGameAction;

/**
 * This class represents a message of the Command Pattern. Depending on the arrived message, we pass
 * via parameter the object to be commanded and the message itself calls a specific function of that
 * object to execute the command. It is used to avoid non-oop style switches.
 */
public abstract class ActionMessage
{
    /**
     * Factory method to generate an action message from a given JSON object.
     * 
     * @param actionJson JSON action object
     * @return Appropriately created action base on the JSON content.
     * @throws IllegalArgumentException Thrown if the command was not recognized.
     */
    static public ActionMessage buildActionMessage(JSONObject actionJson)
            throws IllegalArgumentException
    {
        String actionId = actionJson.getString("actionId");

        if (actionId == CharacterCardActionMessage.class.getName())
            return new CharacterCardActionMessage(actionJson);
        else if (actionId == EndTurnMessage.class.getName())
            return new EndTurnMessage();
        else if (actionId == MoveMotherNatureMessage.class.getName())
            return new MoveMotherNatureMessage(actionJson);
        else if (actionId == MoveStudentFromEntranceToDiningMessage.class.getName())
            return new MoveStudentFromEntranceToDiningMessage(actionJson);
        else if (actionId == MoveStudentFromEntranceToIslandMessage.class.getName())
            return new MoveStudentFromEntranceToIslandMessage(actionJson);
        else if (actionId == PlayAssistantCardMessage.class.getName())
            return new PlayAssistantCardMessage(actionJson);
        else if (actionId == PlayCharacterCardMessage.class.getName())
            return new PlayCharacterCardMessage(actionJson);
        else if (actionId == SelectCloudTileMessage.class.getName())
            return new SelectCloudTileMessage(actionJson);

        throw new IllegalArgumentException(
                "[ActionMessage] Unrecognized action message " + actionId);
    }

    /**
     * Checks if the given game action handler is valid.
     * 
     * @throws NullPointerException Thrown if the handler is null.
     */
    public void checkHandler(GameActionHandler handler) throws NullPointerException
    {
        if (handler == null)
            throw new NullPointerException("[ActionMessage] Handler is null");
    }

    /**
     * Method that has to be called to handle the message. Uses the given game action handler.
     * 
     * @param handler The controller handler that contains all the functions the player could call.
     * @throws NullPointerException Thrown if handler is null.
     */
    abstract public void applyAction(GameActionHandler handler) throws NullPointerException;

    abstract public BaseGameAction getBaseGameAction();
}
