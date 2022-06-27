package it.polimi.ingsw.client.gui.lobby;

import it.polimi.ingsw.client.gui.SceneController;

/**
 * Interface for all scene controllers.
 */
public interface Controllable
{
    /**
     * Method called when the controller becomes the current one in order to set properly the scene.
     */
    void initialize(SceneController controller);
}
