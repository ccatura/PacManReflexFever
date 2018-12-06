
import java.net.URL;
import java.util.Date;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.io.*;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.scene.control.Label;

/**
 * @author Charles P. Catura
 */
public class PacMaze extends StackPane
{
    Character pacMan = new Character(false, "left", Character.Type.PACMAN); //creates the pacman character
    Character ghost = new Character(true, "right", Character.Type.BLINKY);
    Character ghost1 = new Character(true, "right", Character.Type.BLINKY);
    Character ghost2 = new Character(true, "right", Character.Type.PINKY);
    Character ghost3 = new Character(true, "right", Character.Type.INKY);
    Character ghost4 = new Character(true, "right", Character.Type.CLYDE);
    Character blueGhost = new Character(true, "right", Character.Type.BLUEGHOST);
    Character whiteGhost = new Character(true, "right", Character.Type.WHITEGHOST);

    Character pellet = new Character(true, "left", Character.Type.PELLET); //creates a power pellet character
    Prop prop = new Prop(); //creating the first prop (faux pupper and lower pathways)
    Dots dots = new Dots(420, 5, 20, "right"); 

    ImageView readyText = new ImageView(new Image("images/ready.gif"));
    ImageView titleGraphic = new ImageView(new Image("images/pacman_title.png"));
    ImageView points200 = new ImageView(new Image("images/200.gif"));
    ImageView points400 = new ImageView(new Image("images/400.gif"));
    ImageView points800 = new ImageView(new Image("images/800.gif"));
    ImageView points1600 = new ImageView(new Image("images/1600.gif"));

    /**
     * Startup instructions that are displayed before the came starts.
     */
    final Text STARTUPTEXT = new Text(
            "survive as long as you can" +
            "\ngo left and right to\nstay away from ghosts" +
            "\n\npress 's' to start!");

    //these are the other texts displayed in the game
    Text p1 = new Text("1up");
    Text highScore = new Text("high score");
    Text creditText = new Text("credit 1");
    int score = 0; //the player's score
    int hiscore;
    Text scoreText = new Text("" + score); //the score text version
    Text hiscoreText;
    Text gameOverText = new Text("Game Over");
    File hiscoreFile = new File("txt/hiscore.txt"); 

    /**
     * The coin sound when S is pressed.
     */
    final AudioClip COIN_SOUND = new AudioClip(this.getClass().getResource("sounds/waka.wav").toString());

    /**
     * The death sound when PacMan diest.
     */
    final AudioClip DEATH_SOUND = new AudioClip(this.getClass().getResource("sounds/death.wav").toString());

    /**
     * The energize sound when PacMan eats a power pellet.
     */
    final AudioClip ENERGIZED_SOUND = new AudioClip(this.getClass().getResource("sounds/energized.wav").toString());

    /**
     * The eat ghost sound when eats ghost.
     */
    final AudioClip EAT_GHOST = new AudioClip(this.getClass().getResource("sounds/eat_ghost.wav").toString());

    /**
     * The gulp sound when pellet is eaten.
     */
    final AudioClip GULP_SOUND = new AudioClip(this.getClass().getResource("sounds/gulp.wav").toString());

    /**
     * The "eyes" sound when ghost is eaten.
     */
    final AudioClip EYES_SOUND = new AudioClip(this.getClass().getResource("sounds/eyes.wav").toString());

    /**
     * The intermission song.
     */
    final URL RESOURCE1 = getClass().getResource("sounds/intermission.wav");
    final MediaPlayer INTERMISSION = new MediaPlayer(new Media(RESOURCE1.toString()));

    /**
     * The background music.
     */
    final URL RESOURCE = getClass().getResource("sounds/pacmanfever.mp3");
    final MediaPlayer PACMAN_FEVER = new MediaPlayer(new Media(RESOURCE.toString()));


    /**
     * Is TRUE when S is pressed. We are ready to play. Is FALSE before S is pressed.
     */
    boolean ready = false;

    /**
     * Is TRUE only when game is in motion.
     */
    boolean gameStarted = false;
    boolean pacmanDeadState = false;
    boolean energized = false;
    boolean ghostEaten = false;

    private Timeline propTimer; //timer that controls props (other than characters)
    private Timeline p1Timer; //1up timer that controls the 1up blinking
    private Timeline scoreTimer; //timer that controls the score accumulation
    private Timeline eventTimer; //timer for random events to excecute
    private Timeline dotTimer; //timer for dots
    private Timeline afterThought; //i didnt plan this very well

    double propPos = -299; //starting point of props
    double propSpeed = .8; //prop speed
    double pelletPos = -299;
    double ghostPos = -200;
    double ghostSpeed = 2.5;
    double leftCollision = 205;
    double rightCollision = 535;
    final double LEFT_BOUND = -250;
    final double RIGHT_BOUND = 1000;
    int ghostPoints;

    long startTime; //time when started
    long currentTime; // = new Date().getTime();

    long ghostStart; //ghost begin countdown
    long ghostClock; //ghost time until shows
    long pelletStart; //pellet begin countdown
    long pelletClock; //pellet time until shows
    long energizedStart;
    long energizedClock = 5500;
    long ghostEatenStart;
    long ghostEatenClock = 500;
    long gameOverStart;

    public PacMaze()
    {
        super();

        //debug();

        readHiscore();

        ghostPoints = 200;

        INTERMISSION.play();
        INTERMISSION.setCycleCount(2);
        INTERMISSION.setVolume(.4);

        //getChildren().add(x); //this is not used right now
        //x.setTranslateX(-200);
        //x.setTranslateY(150);

        setStyle("-fx-background-image: url(images/maze.gif)");
        hiscoreText = new Text("" + hiscore); //the hiscore text version
        setupText();
        addProp();
        p1Blink();
        startScore();

        eventTimer = new Timeline(new KeyFrame(Duration.seconds(.01), e->
                {
                    updateTime();
                    playMusic();
                    addGhost();
                    updateGhost();
                    checkGhostPos();
                    checkPelletPos();
                    addPellet();
                    dots.toBack();

                    if(energized) {
                        updateEnergized();
                    }

                    if(currentTime - startTime > 1000 & ready) {
                        screenB();
                    }

                    if(currentTime - startTime > 3000 && ready) {
                        screenC();
                    }

                    if(currentTime - startTime > 5300 && ready && !gameStarted) {
                        playEverything();

                        dots.start();
                        getChildren().add(dots);
                        dots.setTranslateX(-525);
                        dots.setTranslateY(255);

                    }

                    if(ghostEaten) {
                        if(currentTime - ghostEatenStart > ghostEatenClock) {
                            ghostEaten = false;
                            pacMan.setTranslateX((getWidth() / 2) - (pacMan.canvas.getWidth() / 2));
                            if(points200.getScene() != null) getChildren().remove(points200);
                            if(points400.getScene() != null)getChildren().remove(points400);
                            if(points800.getScene() != null)getChildren().remove(points800);
                            if(points1600.getScene() != null)getChildren().remove(points1600);
                        }
                    }

                    if(debugOn) updateDebug();
                }
            ));
        eventTimer.setCycleCount(Animation.INDEFINITE);
        eventTimer.play();

    }

    public void afterThoughtTimer()
    {
        afterThought = new Timeline(new KeyFrame(Duration.seconds(.01), e->
                {
                    long newTime = new Date().getTime();
                    //System.out.println(gameOverStart);
                    if(newTime - gameOverStart > 2500 && pacmanDeadState) {
                        if(gameOverText.getScene() == null) getChildren().add(gameOverText);
                        afterThought.stop();
                    }
                }
            ));
        afterThought.setCycleCount(Animation.INDEFINITE);
        afterThought.play();
    }

    public void pacmanDead()
    {
        pacmanDeadState = true;


        eventTimer.stop();
        propTimer.stop();
        scoreTimer.stop();
        pacMan.stop();
        pacMan.setAnimate(false);
        ghostStart = 0;
        currentTime = 0;
        startTime = 0;
        PACMAN_FEVER.stop();
        gameStarted = false;

        getChildren().remove(ghost);
        getChildren().remove(pacMan);

        Death death = new Death(true, "right", Death.Type.DEATH); //i messed up and need a longer animation so i did this crap
        death.setTranslateX((getWidth() / 2) - (pacMan.canvas.getWidth() / 2)); //center pacman X
        death.setTranslateY(125); //position pacman Y
        getChildren().add(death);

        gameOverStart = new Date().getTime();
        DEATH_SOUND.play();
        afterThoughtTimer();
        writeHiscore(hiscore);
    }

    public void playEverything()
    {
        if(propTimer.getStatus() == Animation.Status.STOPPED) {
            propTimer.play(); //start prop animation
        }
        if(scoreTimer.getStatus() == Animation.Status.STOPPED) scoreTimer.play(); //begins the score accumulation
        if(pacMan.animating == false) pacMan.setAnimate(true);
        if(prop.getScene() == null) getChildren().add(prop);

        setPelletClock();
        setGhostClock();

        gameStarted = true;
    }

    public void updateEnergized()
    {
        if(currentTime - energizedStart > (energizedClock / 2)
        && currentTime - energizedStart < energizedClock) {
            if(ghost.getScene() != null) {
                getChildren().remove(ghost);
                ghost = whiteGhost;
                getChildren().add(ghost);
            }
        }

        if(currentTime - energizedStart > energizedClock) {
            energized = false;
            ghostPoints = 200;
            setPelletClock();
            if(ghost.getScene() != null) {
                String x = ghost.getDirection();
                getChildren().remove(ghost);
                ghost = ghost1;
                ghost.setDirection(x);
                getChildren().add(ghost);
                ENERGIZED_SOUND.stop();

            }
        }
    }    

    public void updateTime()
    {
        currentTime = new Date().getTime(); //update current time
    }

    public void playMusic()
    {
        if(currentTime - startTime > 1000 && ready) {
            PACMAN_FEVER.play(); //plays the background music
        }
    }

    public void screenB()
    {
        if(readyText.getScene() == null) {
            getChildren().remove(STARTUPTEXT); // removes startup text from screen
            getChildren().add(readyText);
            creditText.setText("credit 0"); //turns credit text to 0
        }
    }

    public void screenC()
    {
        getChildren().remove(readyText);
        if(pacMan.getScene() == null) getChildren().add(pacMan);
    }

    public void startScore()
    {
        scoreTimer = new Timeline(new 
            KeyFrame(Duration.seconds(1), e->
                {
                    score += 10;

                    if(hiscore < score)
                    {
                        hiscore = score;    
                    }

                    updateScoreText();

                    propSpeed += .02;
                    propTimer.setRate(propSpeed);
                    ghostSpeed += .025;
                }
            ));
        scoreTimer.setCycleCount(Animation.INDEFINITE);
    }

    public void updateScoreText()
    {
        scoreText.setText("" + score);
        hiscoreText.setText("" + hiscore);

    }

    public void p1Blink()
    {
        //this make p1 blink
        p1Timer = new Timeline( 
            new KeyFrame(Duration.seconds(.3), e->
                {
                    p1.setFill(Color.rgb(255, 255, 255, 1));
                }
            ), new KeyFrame(Duration.seconds(.6), e->
                {
                    p1.setFill(Color.rgb(255, 255, 255, 0));
                }
            ));
        p1Timer.setCycleCount(Animation.INDEFINITE);
        p1Timer.play();
    }

    public void setupText()
    {
        //load custom font
        final Font F = Font.loadFont(getClass().getResourceAsStream("fonts/emulogic.ttf"), 28);

        // these next lines assign the font to these labels
        STARTUPTEXT.setFont(F);
        creditText.setFont(F);
        highScore.setFont(F);
        p1.setFont(F);
        scoreText.setFont(F);
        hiscoreText.setFont(F);
        gameOverText.setFont(F);

        getChildren().add(STARTUPTEXT);
        STARTUPTEXT.setFill(Color.rgb(0, 255, 222));
        STARTUPTEXT.setTextAlignment(TextAlignment.CENTER);
        setAlignment(Pos.CENTER);

        getChildren().add(creditText);
        creditText.setFill(Color.rgb(255, 255, 255));
        creditText.setTextAlignment(TextAlignment.LEFT);
        creditText.setTranslateX(-350);
        creditText.setTranslateY(237);

        getChildren().add(highScore);
        highScore.setFill(Color.rgb(255, 255, 255));
        highScore.setTextAlignment(TextAlignment.CENTER);
        highScore.setTranslateX(0);
        highScore.setTranslateY(-242);

        getChildren().add(p1);
        p1.setFill(Color.rgb(255, 255, 255, 1));
        p1.setTextAlignment(TextAlignment.LEFT);
        p1.setTranslateX(-420);
        p1.setTranslateY(-242);

        getChildren().add(scoreText);
        scoreText.setFill(Color.rgb(255, 255, 255, 1));
        scoreText.setTextAlignment(TextAlignment.RIGHT);
        scoreText.setTranslateX(-378);
        scoreText.setTranslateY(-210);

        getChildren().add(hiscoreText);
        hiscoreText.setFill(Color.rgb(255, 255, 255, 1));
        hiscoreText.setTextAlignment(TextAlignment.RIGHT);
        hiscoreText.setTranslateX(0);
        hiscoreText.setTranslateY(-210);

        gameOverText.setFill(Color.rgb(255, 0, 0, 1));
        gameOverText.setTranslateX(0);
        gameOverText.setTranslateY(0);

    }

    public void addProp()
    {
        //prop timeline controls animation of props.
        propTimer = new Timeline(new KeyFrame(Duration.millis(5), e->
                {
                    updateProp();
                    updatePellet();
                }
            ));
        propTimer.setCycleCount(Animation.INDEFINITE);
        propTimer.setRate(propSpeed);
    }

    public void updateProp()
    {
        prop.toBack();
        prop.setTranslateX(propPos);

        if(pacMan.direction == "right") {
            propPos--;
        } else {
            propPos++;
        }

        if(propPos > 1200) {
            propPos = -350;
            prop.changeProp();
        } else if(propPos < -350) {
            propPos = 1200; 
            prop.changeProp();
        }
    }

    public void addPellet()
    {
        if(currentTime - pelletStart > pelletClock && gameStarted && !energized) {
            //pelletStart = new Date().getTime();
            if(pellet.getScene() == null) {
                if(pacMan.getDirection() == "left") {
                    pelletPos = -250;
                    pellet.setDirection("right");
                } else if(pacMan.getDirection() == "right") {
                    pelletPos = 1000;
                    pellet.setDirection("left");
                }

                //ghost.setTranslateX(ghostPos); //starting position is just oustide the sceen (L -250) or (R 1000)
                pellet.setTranslateY(148);
                if(pellet.getScene() == null && !energized) getChildren().add(pellet);
                pellet.toBack();

            }
        }
    }

    public void updatePellet()
    {
        pellet.toBack();
        pellet.setTranslateX(pelletPos);

        if(pellet.getScene() != null) {
            if(pacMan.direction == "right") {
                pelletPos--;
            } else {
                pelletPos++;
            }
        }
    }

    public void checkPelletPos()
    {
        if(pellet.getScene() != null) {
            if((pellet.getTranslateX() >= leftCollision && pellet.getTranslateX() <= (leftCollision + 30) && pellet.getDirection() == "right"
                || pellet.getTranslateX() <= rightCollision && pellet.getTranslateX() >= (rightCollision - 30) && pellet.getDirection() == "left")
            && pellet.getScene() != null) {
                energized = true;
                getChildren().remove(pellet);
                GULP_SOUND.play();
                energizedStart = new Date().getTime();
                ENERGIZED_SOUND.play();

                pelletPos = -250;

                if(ghost.getScene() != null){
                    getChildren().remove(ghost);
                    ghost = blueGhost;
                    getChildren().add(ghost);
                }

                updateGhost();
                score += 50;
                updateScoreText();
            }

            if(pellet.getTranslateX() < LEFT_BOUND && pacMan.getDirection() == "left"
            || pellet.getTranslateX() > RIGHT_BOUND && pacMan.getDirection() == "right") {
                getChildren().remove(pellet);
            }
        }
    }

    public void setPelletClock()
    {
        pelletStart = new Date().getTime();
        pelletClock = (int)(Math.random() * 15000) + 10000; //was 3000, 2000
    }

    public void addGhost()
    {
        if(currentTime - ghostStart > ghostClock && gameStarted) {
            ghostStart = new Date().getTime();
            if(ghost.getScene() == null) {
                int randGhost = (int)(Math.random() * 4) + 1;
                if(energized) {
                    ghost = blueGhost;
                } else if(randGhost == 1) {
                    ghost = ghost1;
                } else if(randGhost == 2) {
                    ghost = ghost2;
                } else if(randGhost == 3) {
                    ghost = ghost3;
                } else {
                    ghost = ghost4;
                }

                getChildren().add(ghost);

                if(pacMan.getDirection() == "left") {
                    ghostPos = -240;
                    ghost.setDirection("right");
                } else {
                    ghostPos = 990;
                    ghost.setDirection("left");
                }

                //ghost.setTranslateX(ghostPos); //starting position is just oustide the sceen (L -250) or (R 1000)
                ghost.setTranslateY(148);
                ghost.toBack();

                //System.out.println("There should be a ghost");
            }
        }
    }

    public void checkGhostPos()
    {
        if((ghost.getTranslateX() >= leftCollision && ghost.getTranslateX() <= (leftCollision + 30) && ghost.getDirection() == "right"
            || ghost.getTranslateX() <= rightCollision && ghost.getTranslateX() >= (rightCollision - 30)  && ghost.getDirection() == "left")
        && (!energized && ghost.getScene() != null)) {
            pacmanDead();
        } else if((ghost.getTranslateX() >= leftCollision && ghost.getTranslateX() <= (leftCollision + 30) && ghost.getDirection() == "right"
            || ghost.getTranslateX() <= rightCollision && ghost.getTranslateX() >= (rightCollision - 30) && ghost.getDirection() == "left")
        && (energized && ghost.getScene() != null)) {
            EAT_GHOST.play();
            EYES_SOUND.play();
            EYES_SOUND.setVolume(2);
            EYES_SOUND.setCycleCount(2);
            getChildren().remove(ghost);
            setGhostEatenClock();
            ghostEaten = true;
            pacMan.setTranslateX(-600);

            if(ghostPoints == 200) {
                getChildren().add(points200);
            } else if(ghostPoints == 400) {
                getChildren().add(points400);
            } else if(ghostPoints == 800) {
                getChildren().add(points800);
            } else if(ghostPoints == 1600) {
                getChildren().add(points1600);
            }

            //System.out.println(ghostPoints);

            score += ghostPoints;
            if(ghostPoints < 1600) ghostPoints += ghostPoints;
            updateScoreText();

        }

        if(ghost.getScene() != null) {
            if(ghost.getTranslateX() < LEFT_BOUND && ghost.getDirection() == "right"
            || ghost.getTranslateX() > RIGHT_BOUND && ghost.getDirection() == "left") {
                getChildren().remove(ghost);
                setGhostClock();
            }
        }

        if(ghost.getTranslateX() >= rightCollision && ghost.getDirection() == "right") {
            ghost.setDirection("left");
        } else if(ghost.getTranslateX() <= leftCollision && ghost.getDirection() == "left") {
            ghost.setDirection("right");
        }
    }

    public void setGhostClock()
    {
        ghostStart = new Date().getTime();
        ghostClock = (int)(Math.random() * 3000) + 500;
    }

    public void updateGhost()
    {
        if(ghost.getScene() != null) {
            if(ghost.getDirection() == "right" && pacMan.getDirection() == "left") {
                ghostPos += ghostSpeed;
            } else if(ghost.getDirection() == "left" && pacMan.getDirection() == "right"){
                ghostPos -= ghostSpeed;
            } else if(ghost.getDirection() == "right" && pacMan.getDirection() == "right") {
                ghostPos -= (ghostSpeed /3);
            } else if(ghost.getDirection() == "left" && pacMan.getDirection() == "left") {
                ghostPos += (ghostSpeed /3);
            }
            ghost.setTranslateX(ghostPos);
            ghost.setTranslateY(148);
        }
    }

    public void readHiscore()
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader(hiscoreFile)); 
            String st; 
            while ((st = br.readLine()) != null) 
                hiscore = Integer.parseInt(st); 
        } catch(IOException e) {
            //System.out.println(e);
            hiscore = 0;
        }
    }

    public void writeHiscore(int h)
    {
        try {
            //open file for writing
            PrintWriter fileOut = new PrintWriter(hiscoreFile); //makes new file every time
            fileOut.println(hiscore);
            fileOut.close();
        } catch(IOException ex) {
            System.out.println("Error occured while writing to " + hiscoreFile.toString());   
        }

    }

    public void setGhostEatenClock()
    {
        ghostEatenStart = new Date().getTime();
    }

    public void handleKey(KeyEvent ke)
    {
        //excecutes if S is pressed and READY = true.
        if(ke.getCode() == KeyCode.S && !ready) {
            INTERMISSION.stop();
            COIN_SOUND.play(); //plays coin sound
            ready = true; //we are reqady to play

            startTime = new Date().getTime(); //reset the event clock when S is pressed
            //getChildren().add(readyText); //adds pacman character to pane
            //getChildren().add(ghost); //adds ghost character to pane

            pacMan.setTranslateX((getWidth() / 2) - (pacMan.canvas.getWidth() / 2)); //center pacman X
            pacMan.setTranslateY(148); //position pacman Y

            //pellet.setTranslateX(600);
            pellet.setTranslateY(148);

        }

        if(ke.getCode() == KeyCode.Q) {
            if(!debugOn) debug();
        }

        //excecutes only if we are ready
        if(ready && gameStarted == true) {
            if(ke.getCode() == KeyCode.RIGHT && !(pacMan.getDirection() == "right")
            && ghost.getDirection() == "right") {
                pacMan.setDirection("right");
            } else if(ke.getCode() == KeyCode.LEFT && !(pacMan.getDirection() == "left")
            && ghost.getDirection() == "right") {
                pacMan.setDirection("left");
            } else if(ke.getCode() == KeyCode.RIGHT && !(pacMan.getDirection() == "right")
            && ghost.getDirection() == "left") {
                pacMan.setDirection("right");
            } else if(ke.getCode() == KeyCode.LEFT && !(pacMan.getDirection() == "left")
            && ghost.getDirection() == "left") {
                pacMan.setDirection("left");
            }
        }
    }

    public void debug()
    {
        debugOn = true;

        propPosATT.setTextFill(Color.rgb(255, 255, 255));
        ghostPosATT.setTextFill(Color.rgb(255, 255, 255));
        startTimeATT.setTextFill(Color.rgb(255, 255, 255));
        currentTimeATT.setTextFill(Color.rgb(255, 255, 255));
        ghostStartATT.setTextFill(Color.rgb(255, 255, 255));
        ghostClockATT.setTextFill(Color.rgb(255, 255, 255));
        ghostClockCountDownATT.setTextFill(Color.rgb(255, 255, 255));
        pacDir.setTextFill(Color.rgb(255, 255, 255));
        ghostDir.setTextFill(Color.rgb(255, 255, 255));
        getGhostX.setTextFill(Color.rgb(255, 255, 255));
        ghostExist.setTextFill(Color.rgb(255, 255, 255));
        ghostSpeedATT.setTextFill(Color.rgb(255, 255, 255));
        pelletPosATT.setTextFill(Color.rgb(255, 255, 255));
        pacmanDeadATT.setTextFill(Color.rgb(255, 255, 255));
        energizedATT.setTextFill(Color.rgb(255, 255, 255));

        propPosATT.setTranslateX(350);
        ghostPosATT.setTranslateX(350);
        startTimeATT.setTranslateX(350);
        currentTimeATT.setTranslateX(350);
        ghostStartATT.setTranslateX(350);
        ghostClockATT.setTranslateX(350);
        ghostClockCountDownATT.setTranslateX(350);
        pacDir.setTranslateX(350);
        ghostDir.setTranslateX(350);
        getGhostX.setTranslateX(350);
        ghostExist.setTranslateX(350);
        ghostSpeedATT.setTranslateX(350);
        pelletPosATT.setTranslateX(350);
        pacmanDeadATT.setTranslateX(350);
        energizedATT.setTranslateX(350);

        propPosATT.setTranslateY(-200);
        ghostPosATT.setTranslateY(-180);
        startTimeATT.setTranslateY(-160);
        currentTimeATT.setTranslateY(-140);
        ghostStartATT.setTranslateY(-120);
        ghostClockATT.setTranslateY(-100);
        ghostClockCountDownATT.setTranslateY(-80);
        pacDir.setTranslateY(-60);
        ghostDir.setTranslateY(-40);
        getGhostX.setTranslateY(-20);
        ghostExist.setTranslateY(0);
        ghostSpeedATT.setTranslateY(20);
        pelletPosATT.setTranslateY(40);
        pacmanDeadATT.setTranslateY(60);
        energizedATT.setTranslateY(80);

        getChildren().addAll(propPosATT, ghostPosATT, startTimeATT, currentTimeATT,
            ghostStartATT, ghostClockATT, ghostClockCountDownATT, pacDir, ghostDir,
            getGhostX, ghostExist, ghostSpeedATT, pelletPosATT, pacmanDeadATT, energizedATT);
    }

    public void updateDebug()
    {
        propPosATT.setText("propPos: " + propPos);
        ghostPosATT.setText("ghostPos: " + ghost.getTranslateX());
        startTimeATT.setText("startTime: " + startTime);
        currentTimeATT.setText("currentTime: " + currentTime);
        ghostStartATT.setText("ghostStart: " + ghostStart);
        ghostClockATT.setText("ghostClock: " + ghostClock);
        ghostClockCountDownATT.setText("ghostClockCountDown: " + (currentTime - ghostStart));
        pacDir.setText("pacDir: " + pacMan.getDirection());
        ghostDir.setText("ghostDir: " + ghost.getDirection());
        getGhostX.setText("getGhostX: " + ghost.getTranslateX());
        ghostExist.setText("ghostExist: " + (ghost.getScene()));
        ghostSpeedATT.setText("ghostSpeed: " + ghostSpeed);
        pelletPosATT.setText("pelletPos: " + pellet.getTranslateX());
        pacmanDeadATT.setText("pacmanDead: " + pacmanDeadState);
        energizedATT.setText("energized: " + energized);
    }

    /**
     * The following are attribute labels for debugging.
     * They will be removed when everything is peachy.
     */
    boolean debugOn = false;
    Label propPosATT = new Label("propPos: " + propPos);
    Label ghostPosATT = new Label("ghostPos: " + ghost.getTranslateX());
    Label startTimeATT = new Label("startTime: " + startTime);
    Label currentTimeATT = new Label("currentTime: " + currentTime);
    Label ghostStartATT = new Label("ghostStart: " + ghostStart);
    Label ghostClockATT = new Label("ghostClock: " + ghostClock);
    Label ghostClockCountDownATT = new Label("ghostClockCountDown: " + (currentTime - ghostStart));
    Label pacDir = new Label("pacDir: " + pacMan.getDirection());
    Label ghostDir = new Label("ghostDir: " + ghost.getDirection());
    Label getGhostX = new Label ("getGhostX: " + ghost.getTranslateX());
    Label ghostExist = new Label ("ghostExist: " + (ghost.getScene()));
    Label ghostSpeedATT = new Label ("ghostSpeed: " + ghostSpeed);
    Label pelletPosATT = new Label ("pelletPos: " + pellet.getTranslateX());
    Label pacmanDeadATT = new Label ("pacmanDead: " + pacmanDeadState);
    Label energizedATT = new Label ("energized: " + energized);
}
