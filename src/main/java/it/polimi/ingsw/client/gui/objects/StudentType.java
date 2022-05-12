package it.polimi.ingsw.client.gui.objects;

/**
 * Enumerates all the students and correlates them with the corresponding texture
 */
public enum StudentType
{
    BLUE("blueStudent.png"),
    GREEN("greenStudent.png"),
    PINK("pinkStudent.png"),
    RED("redStudent.png"),
    YELLOW("yellowStudent.png");

    /**
     * Texture filename
     */
    private String filename;

    /**
     * Constructor
     */
    private StudentType(String filename) { this.filename = filename; }

    /**
     * Filename getter
     */
    public String getFilename() { return filename; }
}
