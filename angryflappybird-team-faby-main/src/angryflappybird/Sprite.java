package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * A class for all moving object in a game. Contain getters and setters 
 * for velocity, position, and more.
 * 
 * @author Nikki Cheng, Kareena Joshipura, Qianna Pierre
 */
public class Sprite {  
	
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    //private String IMAGE_DIR = "../resources/images/";

    /**
     * Constructor for sprite class
     */
    public Sprite() {
        this.positionX = 0;
        this.positionY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    /**
     * Sprite object
     * @param pX
     * @param pY
     * @param image
     */
    public Sprite(double pX, double pY, Image image) {
    	setPositionXY(pX, pY);
        setImage(image);
        this.velocityX = 0;
        this.velocityY = 0;
    }

    /**
     * set an image and its width & height
     * @param image
     */
    public void setImage(Image image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * @param positionX
     * @param positionY
     */
    public void setPositionXY(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    /**
     * getter for position X
     * @return positionX
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * getter for position Y
     * @return positionY
     */
    public double getPositionY() {
        return positionY;
    }

    /**
     * setter for velocity
     * @param velocityX
     * @param velocityY
     */
    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * add velocity to X and Y position of an object
     * @param x
     * @param y
     */
    public void addVelocity(double x, double y) {
        this.velocityX += x;
        this.velocityY += y;
    }

    /**
     * getter for velocity X
     * @return velocityX
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * getter for velocity Y
     * @return velocityY
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * getter for width
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * render in graphics to the game scene
     * @param gc
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    /**
     * get the boundary for a rectangle
     * @return 2D rectangle with positionX, positionY, width, and height
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    /**
     * check to see if a sprite object intersect with another sprite object
     * @param s
     * @return true or false 
     */
    public boolean intersectsSprite(Sprite s) {
        return s.getBoundary(/**
         * @param time
         */
        ).intersects(this.getBoundary());
    }

    /**
     * update the X and Y position of the object
     * @param time
     */
    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }
}
