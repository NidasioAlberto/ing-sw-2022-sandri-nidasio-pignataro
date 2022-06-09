package it.polimi.ingsw.client.gui.objects;

import java.util.Objects;
import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.ObjectModelParser;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;

public class DrawableNoEntryTile extends DrawableObject
{
    // The object parser
    private static final ObjectModelParser parser = new ObjectModelParser("Models/noEntry.obj", 5);

    /**
     * Object mesh and texture
     */
    private final TriangleMesh triangleMesh;
    private final MeshView noEntryMesh;

    /**
     * Constructor
     * 
     * @param updater The animation updater
     */
    public DrawableNoEntryTile(AnimationHandler updater)
    {
        super(updater);

        // Create the mesh referring to the object file
        triangleMesh = new TriangleMesh();

        // Set all the vertices, textures, faces and normals
        triangleMesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        triangleMesh.getNormals().addAll(parser.getNormals());
        triangleMesh.getFaces().addAll(parser.getFaces());
        triangleMesh.getPoints().addAll(parser.getVertices());
        triangleMesh.getTexCoords().addAll(parser.getTextures());

        // Create the actual node with the mesh
        noEntryMesh = new MeshView(triangleMesh);

        // Create the texture material
        PhongMaterial material = new PhongMaterial();

        // Pick the texture
        material.setDiffuseMap(new Image(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("noEntry.png"))));

        // Set the material
        noEntryMesh.setMaterial(material);

        // Add the initial rotation
        addRotation(new Rotate(180, new Point3D(0, 1, 0)));

        // TODO mouse drag and drop
    }

    @Override
    public void addToGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null group");

        group.getChildren().add(noEntryMesh);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if (group == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null group");

        group.getChildren().remove(noEntryMesh);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null point light");

        light.getScope().add(noEntryMesh);
    }

    @Override
    public void subscribeToAmbientLight(AmbientLight light)
    {}

    @Override
    public void unsubscribeFromPointLight(PointLight light)
    {
        if (light == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null point light");

        light.getScope().remove(noEntryMesh);
    }

    @Override
    public void unsubscribeFromAmbientLight(AmbientLight light)
    {}

    @Override
    public void enableVisibility()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void disableVisibility()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void translate(Point3D point)
    {
        if (point == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null 3D point");

        noEntryMesh.translateXProperty().set(point.getX());
        noEntryMesh.translateYProperty().set(point.getY());
        noEntryMesh.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if (rotation == null)
            throw new NullPointerException("[DrawableNoEntryTile] Null rotation");

        noEntryMesh.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(noEntryMesh.getTranslateX(), noEntryMesh.getTranslateY(), noEntryMesh.getTranslateZ());
    }
}
