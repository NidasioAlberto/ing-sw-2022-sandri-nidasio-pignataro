package it.polimi.ingsw.client.gui.objects;

import it.polimi.ingsw.client.gui.AnimationHandler;
import it.polimi.ingsw.client.gui.objects.types.StudentType;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

public class DrawableStudent extends DrawableObject
{
    /**
     * Student type (color)
     */
    private final StudentType TYPE;

    /**
     * Object mesh
     */
    private final TriangleMesh triangleMesh;
    private final MeshView studentMesh;

    /**
     * Constructor
     */
    public DrawableStudent(StudentType type, AnimationHandler updater)
    {
        super(updater);

        if(type == null)
            throw new NullPointerException("[DrawableStudent] Null student type");

        // Assign the constant parameters
        TYPE = type;

        // Create the mesh referring to the Object file
        triangleMesh = new TriangleMesh();

        // Set all the vertices, textures, faces and normals
        triangleMesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        triangleMesh.getNormals().addAll(type.getParser().getNormals());
        triangleMesh.getFaces().addAll(type.getParser().getFaces());
        triangleMesh.getPoints().addAll(type.getParser().getVertices());
        triangleMesh.getTexCoords().addAll(type.getParser().getTextures());

        // Now i create the actual node with the mesh
        studentMesh = new MeshView(triangleMesh);

        // I create the material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(type.getColor());
        material.setSpecularColor(Color.WHITE);

        // Apply the material
        studentMesh.setMaterial(material);

        // Rotate the student of 180 degrees on the y axis
        studentMesh.getTransforms().add(new Rotate(180, new Point3D(0, 1, 0)));

        // Set the node to mouse transparent
        studentMesh.setMouseTransparent(true);

        // At the end if the updater != null i add the box to it
        if(this.updater != null)
            this.updater.subscribeObject(this);
    }

    @Override
    public void addToGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableStudent] Null group");

        // Add the cylinder to the group
        group.getChildren().add(studentMesh);
    }

    @Override
    public void removeFromGroup(Group group)
    {
        if(group == null)
            throw new NullPointerException("[DrawableStudent] Null group");

        // Remove the cylinder from the group
        group.getChildren().remove(studentMesh);
    }

    @Override
    public void subscribeToPointLight(PointLight light)
    {
        if(light == null)
            throw new NullPointerException("[DrawableStudent] Null point light");

        // The students should be under the light so i subscribe it
        light.getScope().add(studentMesh);
    }

    // This method does nothing because i don't want light from everywhere
    @Override
    public void subscribeToAmbientLight(AmbientLight light){}

    @Override
    public void enableVisibility() {

    }

    @Override
    public void disableVisibility() {

    }

    @Override
    public void translate(Point3D point)
    {
        if(point == null)
            throw new NullPointerException("[DrawableStudent] Null point");

        // Set all the translation
        studentMesh.translateXProperty().set(point.getX());
        studentMesh.translateYProperty().set(point.getY());
        studentMesh.translateZProperty().set(point.getZ());
    }

    @Override
    public void addRotation(Rotate rotation)
    {
        if(rotation == null)
            throw new NullPointerException("[DrawableStudent]");

        // Add the transformation
        studentMesh.getTransforms().add(rotation);
    }

    @Override
    public Point3D getPosition()
    {
        return new Point3D(studentMesh.getTranslateX(), studentMesh.getTranslateY(), studentMesh.getTranslateZ());
    }
}
