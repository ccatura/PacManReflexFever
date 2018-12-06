
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

public class Prop extends Pane {
    ImageView path1 = new ImageView(new Image("images/maze-path-1.gif"));
    ImageView path2 = new ImageView(new Image("images/maze-path-2.gif"));
    ImageView path3 = new ImageView(new Image("images/maze-path-3.gif"));

    Prop() {
        changeProp();
    }

    public void changeProp()
    {
        int a = (int)((Math.random()) * 3);

        //System.out.println(a);

        getChildren().clear();

        if(a == 0) {
            getChildren().add(path1);
        } else if(a ==1) {
            getChildren().add(path2);
        } else {
            getChildren().add(path3);
        }        
    }
}
