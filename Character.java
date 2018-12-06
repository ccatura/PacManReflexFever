
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color; 
import javafx.scene.shape.Circle; 
import javafx.util.Duration;

/**
 * @author Charles P. Catura
 */
public class Character extends Pane {
    /**
     * Characters that can be used
     */
    enum Type //the types of characters we will have
    {
        /**
         * Pacman.
         */
        PACMAN,

        /**
         * Blinky the ghost.
         */
        BLINKY,

        /**
         * Pinky the ghost.
         */
        PINKY,

        /**
         * Inky the ghost.
         */
        INKY,

        /**
         * Clyde the ghost.
         */
        CLYDE,

        /**
         * Blue scared ghost.
         */
        BLUEGHOST,

        /**
         * Blinking white and blue ghost.
         */
        WHITEGHOST,

        /**
         * Power pellet.
         */
        PELLET; 
    }
    /**
     * Image properties and settings. The image that contains all the characters of the game.j
     */
    Image pacmanSprites = new Image("images/pacman_sprites.gif"); //the sprite sheet of all the graphics
    double spriteCols = 8; //how many sprite columns
    double spriteRows = 8;//how many sprite rows
    double spriteSize = pacmanSprites.getWidth() / spriteCols; //width (and also height) of all graphics in this world
    double spriteRow; //which row of sprites to use (this needs to be cleaned up) pacman right facing = 0. pacman left facing = 256.

    double animSpeed = 45; //pacman chomp speed, lower is faster
    String direction; //which direction pacman faces
    static AnimationTimer timer; //the animation timer for the frames
    boolean animating = false; //false = no animation, true = animating
    int frameCount = 4; //frame count for any animation
    int frameStart;

    Canvas canvas = new Canvas(spriteSize, spriteSize); //size of the pacman character in pixels
    GraphicsContext gc = canvas.getGraphicsContext2D();

    /*
     * @param animate Whether or not Pac-Man is animating: True or False.
     * @param direction Which direction Pac-Man faces: Left or Right.
     */
    Character(boolean animate, String direction,Type type) {
        this.setDirection(direction); //the direction pacman "travels"
        this.setAnimate(animate); //start the animation offrames
        this.getChildren().add(canvas); //add the character to the main pane (which is then added to the maze)

        switch(type) {
            case PACMAN:
            spriteRow = 0;
            break;
            case BLINKY:
            spriteRow = 1;
            break;
            case PINKY:
            spriteRow = 2;
            break;
            case INKY:
            spriteRow = 3;
            break;
            case CLYDE:
            spriteRow = 4;
            break;
            case BLUEGHOST:
            spriteRow = 5;
            break;
            case WHITEGHOST:
            spriteRow = 6;
            break;
            case PELLET:
            spriteRow = 7;
            break;
        }

    }

    /*
    @param direction Which direction Pac-Man faces: Left or Right.
     */
    public void setDirection(String direction)
    {
        this.direction = direction;

        //this needs to be cleaned up/fixed  for the different characters in the game
        if(direction == "right") {
            frameStart = 0; //1st row is right facing pacman
            //System.out.println("right");
        } else {
            frameStart = frameCount; //2nd row is left facing pacman
            //System.out.println("left");
        }
    }

    /*
     * @param animate Whether or not Pac-Man is animating: True or False.
     */
    public void setAnimate(boolean animate)
    {
        //starts of with the pacman "ball" shape
        gc.drawImage(pacmanSprites, 0, 0, spriteSize, spriteSize, 0, 0, spriteSize, spriteSize);

        timer = new AnimationTimer() //use AnimationTimer to continue the game loop
        {

            int frameNumber = 0; //virtually keeping track of the frame
            private long lastUpdate = 0; //stores the last time that the frame changed. used for animation speed.

            public void handle(long now)
            {
                if(animate) { //animation will only start if boolean animate is set to true
                    animating = true; //telling whoever that we are animating
                    if (now - lastUpdate >= (1000000 * animSpeed)) { //controls the animation speed
                        if(frameNumber > frameCount - 1) { //resets frame to 0 if > the limit to repeat the animation
                            frameNumber = 0;
                        }

                        gc.clearRect(0, 0, spriteSize, spriteSize); //clears canvas so we can draw the next frame

                        //draws the current animation frame
                        //pacmanSprites = the image file
                        //frameNumber = the column
                        //spriteRow = the row
                        //spriteSize = the size of the image in pixels
                        gc.drawImage((pacmanSprites),
                            ((frameNumber + frameStart) * spriteSize),
                            (spriteRow * spriteSize),
                            (spriteSize),
                            (spriteSize),
                            0,
                            0,
                            (spriteSize),
                            (spriteSize));

                        frameNumber++; //advance the frame number (does not affect the image. this is just for us)
                        lastUpdate = now;
                        //System.out.println(frameNumber);
                    }
                }
            }
        };

        timer.start(); //starts the animation

    }

    public void stop()
    {
        setAnimate(false);
        animating = false;
        timer.stop();
    }

    /*
    public void startAnimation(boolean startStop) {
    if(startStop) {
    timer.start();
    } else {
    timer.stop();   
    } 
    }*/

    public String getDirection() {
        return direction;
    }
}
