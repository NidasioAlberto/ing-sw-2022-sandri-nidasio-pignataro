package it.polimi.ingsw.client.gui.objects.types;

public enum CharacterType
{
    MONK("monk.png"), SHAMAN("shaman.png"), HERALD("herald.png"), POSTMAN("postman.png"), GRANDMA_HERBS("grandmaHerbs.png"), JOKER(
            "joker.png"), KNIGHT("knight.png"), MUSHROOM_MAN(
                    "mushroomMan.png"), MINSTREL("minstrel.png"), PRINCESS("princess.png"), THIEF("thief.png"), CENTAUR("centaur.png");

    /**
     * Filename of the texture
     */
    private final String filename;

    /**
     * Private constructor
     * 
     * @param filename Filename of the texture
     */
    private CharacterType(String filename)
    {
        this.filename = filename;
    }

    /**
     * Getters
     */
    public String getFilename()
    {
        return filename;
    }
}
