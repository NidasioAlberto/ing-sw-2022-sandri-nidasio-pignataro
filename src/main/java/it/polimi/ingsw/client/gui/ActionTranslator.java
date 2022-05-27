package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.protocol.messages.ActionMessage;

import java.util.Map;

/**
 * This singleton class is basically a map of a map that the graphical game objects
 * use to translate the drag and drop movement into a network action.
 * Basically when an object is being dragged, it sets up a string inside
 * this object and, when an object has been dragged on, sets a second string.
 * The combination of both decides what type of message should be sent.
 */
public class ActionTranslator
{
    //private final Map<String, Map<String, ActionMessage>> lookupMap;
}
