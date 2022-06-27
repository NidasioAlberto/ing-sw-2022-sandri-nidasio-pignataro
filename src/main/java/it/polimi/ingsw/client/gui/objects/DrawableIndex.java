package it.polimi.ingsw.client.gui.objects;

import java.util.HashMap;
import java.util.Map;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.model.SchoolColor;
import it.polimi.ingsw.model.TowerColor;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;

public class DrawableIndex extends DrawableObject
{

    /**
     * The camera angle
     */
    private final double CAMERA_ANGLE;

    /**
     * Texts and images
     */
    private Map<SchoolColor, Pair<Text, ImageView>> students;
    private Map<TowerColor, Pair<Text, ImageView>> towers;
    private Pair<Text, ImageView> noEntry;

    /**
     * Layout
     */
    private GridPane grid;
    private BorderPane pane;

    public DrawableIndex(double cameraAngle, AnimationHandler updater)
    {
        super(updater);

        // Set the parameters
        CAMERA_ANGLE = cameraAngle;

        // Create the maps
        students = new HashMap<>();
        towers = new HashMap<>();

        // Create the texts, images
        for (SchoolColor color : SchoolColor.values())
        {
            students.put(color, new Pair<Text, ImageView>(new Text("--"), new ImageView(
                    new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream(color.name().toLowerCase() + "Student.png")))));

            // Set the images and text dimensions
            students.get(color).getKey().setFont(Font.font("Verdana", 30));
            students.get(color).getValue().setFitWidth(50);
            students.get(color).getValue().setFitHeight(50);

        }

        for (TowerColor color : TowerColor.values())
        {
            towers.put(color, new Pair<Text, ImageView>(new Text("--"), new ImageView(
                    new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream(color.name().toLowerCase() + "Tower.png")))));

            // Set the images and text dimensions
            towers.get(color).getKey().setFont(Font.font("Verdana", 30));
            towers.get(color).getValue().setFitWidth(50);
            towers.get(color).getValue().setFitHeight(50);
        }

        noEntry = new Pair<Text, ImageView>(new Text("--"),
                new ImageView(new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("noEntryTile.png"))));
        noEntry.getKey().setFont(Font.font("Verdana", 30));
        noEntry.getValue().setFitWidth(50);
        noEntry.getValue().setFitHeight(50);

        // Create the pane
        grid = new GridPane();
        pane = new BorderPane();

        // Add the objects to the pane
        for (int i = 0; i < SchoolColor.values().length; i++)
        {
            // Add the text in the left position
            grid.add(students.get(SchoolColor.values()[i]).getKey(), 0, i);

            // Add the image in the right position
            grid.add(students.get(SchoolColor.values()[i]).getValue(), 1, i);
        }

        for (int i = 0; i < TowerColor.values().length; i++)
        {
            // Add the text in the left position
            grid.add(towers.get(TowerColor.values()[i]).getKey(), 2, i);

            // Add the image in the right position
            grid.add(towers.get(TowerColor.values()[i]).getValue(), 3, i);
        }

        grid.add(noEntry.getKey(), 2, TowerColor.values().length);
        grid.add(noEntry.getValue(), 3, TowerColor.values().length);

        // Set vertical and orizontal gaps
        grid.setHgap(50);
        grid.setVgap(50);

        // Sets the background color
        grid.setStyle("-fx-background-color: #81CAF1; -fx-background-radius: 18 18 18 18; -fx-border-radius: 18 18 18 18;");
        grid.setPadding(new Insets(30, 30, 30, 30));

        // Incapsulate the grid inside the pane
        pane.setCenter(grid);

        // Rotate the pane
        pane.getTransforms().add(new Rotate(-CAMERA_ANGLE, new Point3D(1, 0, 0)));

        // Set the border around
        pane.setPadding(new Insets(4, 4, 4, 4));
        pane.setStyle("-fx-background-color: white; -fx-background-radius: 18 18 18 18; -fx-border-radius: 18 18 18 18;");

        // Set the pane scaling
        pane.setScaleX(0.3);
        pane.setScaleY(0.3);
        pane.setScaleZ(0.3);
    }

    /**
     * Method to reset all the numbers
     */
    public void resetNumbers()
    {
        for (SchoolColor color : SchoolColor.values())
            students.get(color).getKey().setText("--");

        for (TowerColor color : TowerColor.values())
            towers.get(color).getKey().setText("--");

        noEntry.getKey().setText("--");
    }

    /**
     * Method to set the student number
     */
    public void setStudentNumber(SchoolColor color, int number)
    {
        if (color == null)
            throw new NullPointerException("[DrawableIndex] Null color");
        if (number < 0)
            throw new IllegalArgumentException("[DrawableIndex] Less than 0 number");

        students.get(color).getKey().setText(number == 0 ? "--" : Integer.toString(number));
    }

    /**
     * Method to set the tower number
     * 
     * @param color The tower color
     * @param number The number
     */
    public void setTowerNumber(TowerColor color, int number)
    {
        if (color == null)
            throw new NullPointerException("[DrawableIndex] Null color");
        if (number < 0)
            throw new IllegalArgumentException("[DrawableIndex] Less than 0 number");

        towers.get(color).getKey().setText(number == 0 ? "--" : Integer.toString(number));
    }

    /**
     * Sets the noEntry number
     * 
     * @param number
     */
    public void setNoEntryNumber(int number)
    {
        if (number < 0)
            throw new IllegalArgumentException("[DrawableIndex] Less than 0 number");

        noEntry.getKey().setText(number == 0 ? "--" : Integer.toString(number));
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableIndex] Null group");

        group.getChildren().add(pane);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableIndex] Null group");

        group.getChildren().remove(pane);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {}

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableIndex] Null ambient light");

        light.getScope().add(pane);
    }

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {}

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableIndex] Null ambient light");

        light.getScope().remove(pane);
    }

    @Override
    public void enableVisibility()
    {}

    @Override
    public void disableVisibility()
    {}

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableIndex] Null point");

        pane.translateXProperty().set(point.getX());
        pane.translateYProperty().set(point.getY());
        pane.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableIndex] Null rotation");

        pane.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(pane.getTranslateX(), pane.getTranslateY(), pane.getTranslateZ());
    }

}
