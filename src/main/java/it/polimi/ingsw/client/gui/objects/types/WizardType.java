package it.polimi.ingsw.client.gui.objects.types;

public enum WizardType
{
    WIZARD_1("back1.png"),
    WIZARD_2("back2.png"),
    WIZARD_3("back3.png"),
    WIZARD_4("back4.png");

    /**
     * Filename of the back texture
     */
    private final String filename;

    /**
     * Private Constructor
     * @param filename Filename of the back texture
     */
    private WizardType(String filename) { this.filename = filename; }

    /**
     * Getters
     */
    public String getFilename() { return filename; }
}
