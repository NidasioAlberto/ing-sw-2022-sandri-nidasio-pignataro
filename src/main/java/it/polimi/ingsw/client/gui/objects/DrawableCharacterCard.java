package it.polimi.ingsw.client.gui.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import it.polimi.ingsw.client.gui.ActionTranslator;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.CharacterType;
import it.polimi.ingsw.client.gui.objects.types.StudentType;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.game.CharacterCardType;
import it.polimi.ingsw.protocol.updates.CharacterCardPayloadUpdate;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
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
     * Payload positionings
     */
    public static final double FIRST_X_PAYLOAD = -0.30;
    public static final double FIRST_Y_PAYLOAD = -0.25;
    public static final double STEP_X_PAYLOAD = 0.30;
    public static final double STEP_Y_PAYLOAD = 0.30;

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
     * Two types of image (greyed and not)
     */
    private Image normalImage;
    private WritableImage grayedImage;

    /**
     * Card payloads
     */
    private List<DrawableNoEntryTile> tiles;
    private List<DrawableStudent> students;

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

        // Create the payload collections
        tiles = new ArrayList<>();
        students = new ArrayList<>();

        // Create the texture
        PhongMaterial material = new PhongMaterial();

        // Extract the image texture from the character type
        normalImage = new Image(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(CharacterType.valueOf(TYPE.name()).getFilename())));
        grayedImage = new WritableImage(normalImage.getPixelReader(), (int) normalImage.getWidth(), (int) normalImage.getHeight());

        /**
         * GRAY the normal image
         */
        for (int i = 0; i < normalImage.getWidth(); i++)
        {
            for (int j = 0; j < normalImage.getHeight(); j++)
            {
                Color color = normalImage.getPixelReader().getColor(i, j);
                double mean = (color.getRed() + color.getBlue() + color.getGreen()) / 3;
                Color gray = new Color(mean, mean, mean, 1);

                // Apply the gray color
                grayedImage.getPixelWriter().setColor(i, j, gray);
            }
        }

        material.setDiffuseMap(grayedImage);

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

        // Set also the dragged over event because of reset purposes in case of a wrong
        // action
        box.setOnMouseDragReleased((event) -> {
            // Set the droppedOn target
            ActionTranslator.getInstance().setDroppedOnItem("CharacterCard");

            // Execute the reset
            ActionTranslator.getInstance().execute();
        });
    }

    /**
     * Method to reset all the movable things on the character card to the correct position
     */
    public void updatePosition()
    {
        if (tiles.size() != 0)
        {
            // Reset the no entry tiles
            for (int i = 0; i < tiles.size(); i++)
            {
                // Coordinates without rotations
                Point3D coordinates = new Point3D(FIRST_X_PAYLOAD * X_DIMENSION + (i % 3) * STEP_X_PAYLOAD * X_DIMENSION, 0,
                        FIRST_Y_PAYLOAD * X_DIMENSION - (int) (i / 3) * STEP_Y_PAYLOAD * X_DIMENSION);

                // Translate the tile to the correct position
                tiles.get(i).translate(coordinates.add(getPosition()));
            }
        } else if (students.size() != 0)
        {
            // Reset the students
            for (int i = 0; i < students.size(); i++)
            {
                // Coordinates without rotations
                Point3D coordinates = new Point3D(FIRST_X_PAYLOAD * X_DIMENSION + (i % 3) * STEP_X_PAYLOAD * X_DIMENSION, 0,
                        FIRST_Y_PAYLOAD * X_DIMENSION - (int) (i / 3) * STEP_Y_PAYLOAD * X_DIMENSION);

                // Translate the student in the correct position
                students.get(i).translate(coordinates.add(getPosition()));
            }
        }
    }

    /**
     * Method to update the character card payload
     * 
     * @param update The update message
     * @param group The group to which add or remove eventual new stuff
     * @param light The light to which add or remove eventual stuff
     */
    public void update(CharacterCardPayloadUpdate update, Group group, PointLight light)
    {
        if (update == null)
            throw new NullPointerException("[DrawableCharacterCard] Null payload update");

        // Determine if the payload is no entry tiles or not
        if (update.getNoEntryTiles() != null && update.getNoEntryTiles() != 0)
        {
            // Memorize these before, DON'T USE THEM INSIDE THE FOR LOOPS
            int numOfUpdate = update.getNoEntryTiles();
            int actualNum = tiles.size();

            // Check if the no entry are less or more
            if (numOfUpdate > actualNum)
                for (int i = numOfUpdate; i > actualNum; i--)
                    addNoEntry(group, light);
            else if (numOfUpdate < actualNum)
                for (int i = numOfUpdate; i < actualNum; i++)
                    removeNoEntry(group, light);

        } else if (update.getStudents() != null && update.getStudents().size() != 0)
        {
            // Parse all the colors and first remove the eccess and the add the new
            for (SchoolColor color : SchoolColor.values())
            {
                // Calculate the number of students
                long cardNum = update.getStudents().stream().filter((s) -> s.getColor() == color).count();
                long num = students.stream().filter((s) -> s.getType().name().equals(color.name())).count();

                if (num > cardNum)
                {
                    for (long i = cardNum; i < num; i++)
                        this.removeStudent(color, group, light);
                }
            }

            // Add the missing students TO BE DONE LATER
            for (SchoolColor color : SchoolColor.values())
            {
                // Calculate the number of students
                long cardNum = update.getStudents().stream().filter((s) -> s.getColor() == color).count();
                long num = students.stream().filter((s) -> s.getType().name().equals(color.name())).count();

                if (num < cardNum)
                {
                    for (long i = num; i < cardNum; i++)
                        this.addStudent(color, group, light);
                }
            }
        }
        // Update the general positionings
        updatePosition();
    }

    /**
     * Method to set the card active (color of gray)
     */
    public void setActive(boolean status)
    {
        if (status)
        {
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseMap(normalImage);
            box.setMaterial(material);
        } else
        {
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseMap(grayedImage);
            box.setMaterial(material);
        }
    }

    /**
     * Method to add a student to the card
     * 
     * @param color The color of the student to be added
     * @param group The group to which subscribe the student
     * @param light The point light to which subscribe the student
     */
    public void addStudent(SchoolColor color, Group group, PointLight light)
    {
        // Check if it is possible
        if (students.size() >= 6)
            return;

        // Create the new student of the same color
        DrawableStudent student = new DrawableStudent(StudentType.valueOf(color.name()), updater);

        // Coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_PAYLOAD * X_DIMENSION + (students.size() % 3) * STEP_X_PAYLOAD * X_DIMENSION, 0,
                FIRST_Y_PAYLOAD * X_DIMENSION - (int) (students.size() / 3) * STEP_Y_PAYLOAD * X_DIMENSION);

        // Translate the student in the correct position
        student.translate(coordinates.add(getPosition()));

        // Set the student to character one
        student.setCharacter(true);

        // Add the student to the list
        students.add(student);

        // Add the student to the group and light
        student.addToGroup(group);
        student.subscribeToPointLight(light);
    }

    /**
     * Method to remove a student of a certain color
     * 
     * @param color Color of the student to be removed
     * @param group Group from which unsubscribe the student
     * @param light Point light from which unsubscribe the student
     */
    public void removeStudent(SchoolColor color, Group group, PointLight light)
    {
        // Check if it is actually possible
        if (students.stream().filter((s) -> s.getType().name().equals(color.name())).count() == 0)
            return;

        // Extract the student to remove
        DrawableStudent student = students.stream().filter((s) -> s.getType().name().equals(color.name())).findFirst().get();

        // Remove the student from the group
        student.removeFromGroup(group);

        // Unsubscribe the student from the light
        student.unsubscribeFromPointLight(light);

        // Remove the student from the list
        students.remove(student);
    }

    /**
     * Method to add No entry tile
     */
    public void addNoEntry(Group group, PointLight light)
    {
        // Check if it is actually possible
        if (tiles.size() >= 6)
            return;

        // Create the tile
        DrawableNoEntryTile tile = new DrawableNoEntryTile(updater);

        // Coordinates without rotations
        Point3D coordinates = new Point3D(FIRST_X_PAYLOAD * X_DIMENSION + (tiles.size() % 3) * STEP_X_PAYLOAD * X_DIMENSION, 0,
                FIRST_Y_PAYLOAD * X_DIMENSION - (int) (tiles.size() / 3) * STEP_Y_PAYLOAD * X_DIMENSION);

        // Translate the tile to the correct position
        tile.translate(coordinates.add(getPosition()));

        // Add the tile to the list
        tiles.add(tile);

        // Add the tile to group and light
        tile.addToGroup(group);
        tile.subscribeToPointLight(light);
    }

    /**
     * Method to remove no entry tiles
     */
    public void removeNoEntry(Group group, PointLight light)
    {
        // Make sure that it could be done
        if (tiles.size() == 0)
            return;

        // Take the tile to remove
        DrawableNoEntryTile tile = tiles.get(tiles.size() - 1);

        // Remove the tile from the group and lightings
        tile.removeFromGroup(group);
        tile.unsubscribeFromPointLight(light);

        // Remove the tile from the collection
        tiles.remove(tile);
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
