package com.example.editorgrafov;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Vertex extends StackPane {

    private final Circle circle;
    private final Text text;
    private int radius;
    private Color colorFill;
    private static final Color defaultColorFill = Color.YELLOW;
    private static final int defaultRadius = 30;

    /**
     * Constructor of Vertex object
     * @param text value stored inside of vertex
     * @param x coordinate, where vertex is placed
     * @param y coordinate, where vertex is placed
     */
    public Vertex(Text text, double x, double y) {
        this.text = text;
        this.circle = new Circle(x, y, defaultRadius);
        //default
        circle.setFill(defaultColorFill);
        circle.setStroke(Color.BLACK);
        text.setStyle("-fx-font-weight: bold");
        //text.setFont(new Font("Arial", 50));
        getChildren().add(circle);
        getChildren().add(text);
    }

    public void resetFill() {
        colorFill = defaultColorFill;
        circle.setFill(defaultColorFill);
    }

    public void resetRadius() {
        radius = defaultRadius;
        resizeNode(radius);
    }

    /**
     * Method that returns x coordinate of vertex center
     * @return x coordinate of vertex
     */
    public double getX() {
        return circle.getCenterX();
    }

    /**
     * Method that returns y coordinate of vertex center
     * @return y coordinate of vertex
     */
    public double getY() {
        return circle.getCenterY();
    }

    /**
     * Method that sets x coordinate of vertex center
     * @param x coordinate of center
     */
    public void setX(double x) {
        circle.setCenterX(x);
    }

    /**
     * Method that sets y coordinate of vertex center
     * @param y coordinate of center
     */
    public void setY(double y) {
        circle.setCenterY(y);
    }

    /**
     * Method which returns value of vertex
     * @return Text value of vertex
     */
    public Text getText() {
        return text;
    }

    /**
     * Method which returns value of vertex
     * @return String value of vertex
     */
    public String getString() {
        return text.getText();
    }

    /**
     * Method which returns the Shape of vertex
     * @return Circle representation of vertex
     */
    public Circle getCircle() {
        return circle;
    }

    /**
     * Method which return radius of vertex
     * @return integer value of radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Method which returns color fill of Shape which represents vertex
     * @return Color of object representing vertex
     */
    public Color getFill() {
        return colorFill;
    }

    /*
     * possible customization options
     */

    /**
     * Method that sets colorfill of Vertex
     * @param color
     */
    public void setVertexFill(Color color) {
        colorFill = color;
        circle.setFill(color);
    }

    /**
     * Method that sets color of value inside vertex
     * @param color
     */
    public void setValueColor(Color color) {
        text.setFill(color);
    }

    /**
     * Method that resizes vertex to one with new radius
     * @param radius
     */
    public void resizeNode(int radius) {
        this.radius = radius;
        circle.setRadius(radius);
    }

    /**
     * Method that changes font of value inside the vertex
     * @param fontName of value
     * @param height of text
     */
    public void changeFontAndSize(String fontName, double height) {
        text.setFont(new Font(fontName, height));
    }


    //and more

}
