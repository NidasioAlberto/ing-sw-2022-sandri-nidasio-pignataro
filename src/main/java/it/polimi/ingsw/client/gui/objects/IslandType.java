package it.polimi.ingsw.client.gui.objects;

public enum IslandType
{
    ISLAND1("island1.png"),
    ISLAND2("island2.png"),
    ISLAND3("island3.png");

    /**
     * Filename where the texture is located
     */
    private String filename;

    /**
     * Constructor
     * @param filename file path to the texture
     */
    private IslandType(String filename)
    {
        this.filename = filename;
    }

    /**
     * Filename getter
     */
    public String getFilename() { return filename; }
}
