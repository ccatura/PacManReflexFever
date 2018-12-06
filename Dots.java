
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.animation.KeyValue;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.animation.Animation;

public class Dots extends Pane
{
    private double width;
    private double speed;
    private int size;
    String direction;
    private Timeline dotTimer;
    private Timeline allDotTimer;

    Dots(double width, double speed, int size, String direction)
    {
        this.setDotsWidth(width);
        this.setSpeed(speed);
        this.setSize(size);
        this.setDirection(direction);


        
        allDotTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), e->{
                    makeDot();
                }),
            new KeyFrame(Duration.seconds(1.5), e->{
                    makeDot();
                }),
            new KeyFrame(Duration.seconds(2), e->{
                    makeDot();
                }),
            new KeyFrame(Duration.seconds(2.5), e->{
                    makeDot();
                })
        );
        allDotTimer.play();
    }

    public void start()
    {
        allDotTimer.play();
        //System.out.println("Started");
    }

        public void stop()
    {
        allDotTimer.stop();
    }
    
    public void makeDot()
    {
        Rectangle dot = new Rectangle();

        dot = new Rectangle(0, 0, size, size);
        dot.setFill(Color.rgb(250, 185, 178));
        getChildren().add(dot);

        dotTimer = new Timeline();
        dotTimer.setCycleCount(Timeline.INDEFINITE);
        dotTimer.setAutoReverse(false);
        
        KeyValue kv1 = new KeyValue(dot.xProperty(), 0);
        KeyValue kv2 = new KeyValue(dot.xProperty(), getDotsWidth());
        KeyFrame kf1 = new KeyFrame(Duration.millis(0), kv1);
        KeyFrame kf2 = new KeyFrame(Duration.millis(2000), kv2);
        dotTimer.getKeyFrames().addAll(kf1, kf2);

        dotTimer.play();
        //dotTimer.setRate(speed);
    }    

    public void setDotsWidth(double width)
    {
        this.width = width;
    }

    public double getDotsWidth()
    {
        return width;
    }

    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }

    public double getSpeed()
    {
        return speed;
    }

    public int getSize()
    {
        return size;
    }

}
