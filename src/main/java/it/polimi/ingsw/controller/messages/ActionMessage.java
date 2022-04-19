package it.polimi.ingsw.controller.messages;

import it.polimi.ingsw.controller.GameActionHandler;

/**
 * This class represents a message of the Command Pattern. Depending on the arrived message,
 * we pass via parameter the object to be commanded and the message itself calls a specific
 * function of that object to execute the command. It is used to avoid non-oop style switches.
 */
public abstract class ActionMessage
{
    /**
     * Json parsed message to carry along with the actual command to execute
     */
    protected String json;

    /**
     * Constructor
     * @param json The message to be delivered with the command
     * @throws NullPointerException When the json message is null
     */
    protected ActionMessage(String json)
    {
        if(json == null)
            throw new NullPointerException("[ActionMessage] Null json message");

        this.json = json;
    }

    /**
     * The method that has to be called to allow the message to call the specific function
     * of the handler
     * @param handler The controller handler that contains all the functions the player could call
     */
    public void applyAction(GameActionHandler handler) throws NullPointerException
    {
        if (handler == null)
            throw new NullPointerException("[ActionMessage] Handler is null");
    }

    /**
     * Getters and setters
     */
    public String getJson() { return json; }
    public void setJson(String json)
    {
        if(json == null)
            throw new NullPointerException("[ActionMessage] Null json message");

        this.json = json;
    }
}
