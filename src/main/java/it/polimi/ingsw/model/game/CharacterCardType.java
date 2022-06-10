package it.polimi.ingsw.model.game;

/**
 * Enumeration of the character cards type
 */
public enum CharacterCardType
{
    MONK("Monk"), SHAMAN("Shaman"), HERALD("Herald"), POSTMAN("Postman"), GRANDMA_HERBS("Grandma"), JOKER("Joker"), KNIGHT("Knight"), MUSHROOM_MAN(
            "Mushroom"), MINSTREL("Minstrel"), PRINCESS("Princess"), THIEF("Thief"), CENTAUR("Centaur");

    private String name;

    private CharacterCardType(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
