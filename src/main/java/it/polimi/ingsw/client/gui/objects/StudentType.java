package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.ObjectModelParser;

/**
 * Enumerates all the students and correlates them with the corresponding texture
 */
public enum StudentType
{
    RED("Models/redStudent.obj");

    /**
     * Object file parser
     */
    private ObjectModelParser parser;

    /**
     * Constructor
     */
    private StudentType(String filename) { this.parser = new ObjectModelParser(filename, 15); }

    /**
     * Parser getter
     */
    public ObjectModelParser getParser() { return parser; }
}
