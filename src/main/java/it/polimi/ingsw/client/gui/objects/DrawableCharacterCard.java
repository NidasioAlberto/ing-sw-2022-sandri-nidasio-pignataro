package it.polimi.ingsw.client.gui.objects;

import java.util.Objects;
import it.polimi.ingsw.client.gui.ActionTranslator;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.CharacterType;
import it.polimi.ingsw.model.game.CharacterCardType;
import it.polimi.ingsw.protocol.updates.CharacterCardPayloadUpdate;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

public class DrawableCharacterCard extends DrawableObject
{
    /**
     * Scale factor between X and Y dimensions
     */
    public static final double SCALE_FACTOR = 1.516;

    /**
     * X Dimension constant
     */
    private final double X_DIMENSION;

    /**
     * Y Dimension constant
     */
    private final double Y_DIMENSION;

    /**
     * Character card number inside the array
     */
    private final int CHARACTER_NUMBER;

    /**
     * The card type
     */
    private final CharacterCardType TYPE;

    /**
     * The actual graphical box
     */
    private final Box box;

    /**
     * Constructor
     * 
     * @param updater
     */
    public DrawableCharacterCard(double x_dimension, int characterNumber, CharacterCardType type, AnimationHandler updater)
    {
        super(updater);

        if (x_dimension <= 0)
            throw new IllegalArgumentException("[DrawableCharacterCard] Zero or less X dimension");
        if (characterNumber < 0)
            throw new IllegalArgumentException("[DrawableCharacterCard] Less than zero character number");
        if (type == null)
            throw new NullPointerException("[DrawableCharacterCard] Null card type");

        // Set the constants
        X_DIMENSION = x_dimension;
        Y_DIMENSION = x_dimension * SCALE_FACTOR;
        CHARACTER_NUMBER = characterNumber;
        TYPE = type;

        // Create the box
        box = new Box(X_DIMENSION, Y_DIMENSION, 0);

        // Create the texture
        PhongMaterial material = new PhongMaterial();

        // Extract the image texture from the character type
        material.setDiffuseMap(new Image(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(CharacterType.valueOf(TYPE.name()).getFilename()))));

        // Set the material
        box.setMaterial(material);

        // Add the rotations
        box.getTransforms().add(new Rotate(90, new Point3D(1, 0, 0)));
        box.getTransforms().add(new Rotate(180, new Point3D(0, 0, 1)));

        // Add the mouse click feature
        box.setOnMouseClicked((event) -> {
            // Ensure that the click happened only one time
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1)
            {
                // Set the selected card and call the action
                ActionTranslator.getInstance().setDraggedItem("CharacterCard");

                // Set the card number
                ActionTranslator.getInstance().selectCard(CHARACTER_NUMBER);

                // Execute the action
                ActionTranslator.getInstance().execute();
            }
        });

        // Set also the dragged over event because of reset purposes in case of a wrong action
        box.setOnMouseDragReleased((event) -> {
            // Set the droppedOn target
            ActionTranslator.getInstance().setDroppedOnItem("CharacterCard");

            // Execute the reset
            ActionTranslator.getInstance().execute();
        });
    }

    public void update(CharacterCardPayloadUpdate update, Group group, PointLight light)
    {
        if (update == null)
            throw new NullPointerException("[DrawableCharacterCard] Null payload update");

        // Determine if the payload is no entry tiles or not
        if (update.getNoEntryTiles() != 0)
        {
            // No entry

        } else
        {
            // Students
        }
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableCharacterCard] Null group");

        group.getChildren().add(box);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableCharacterCard] Null group");

        group.getChildren().remove(box);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCharacterCard] Null ambient light");

        light.getScope().add(box);
    }

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {}

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableCharacterCard] Null ambient light");

        light.getScope().remove(box);
    }

    @Override
    public void enableVisibility()
    {
        box.setMouseTransparent(false);
    }

    @Override
    public void disableVisibility()
    {
        box.setMouseTransparent(true);
    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableCharacterCard] Null point");

        box.translateXProperty().set(point.getX());
        box.translateYProperty().set(point.getY());
        box.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableCharacterCard] Null rotation");

        box.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ());
    }
}
