
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/*
 * @author Charles P. Catura
 */
public class PacManReflexFever extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        BorderPane borderPane = new BorderPane();
        PacMaze pacMaze = new PacMaze();
        borderPane.setCenter(pacMaze);
        //borderPane.setCenter(startScreen);
        
        Scene scene = new Scene(borderPane, 1000, 500);
        stage.setTitle("Pac-Man Reflex Fever");

        stage.setScene(scene);

        scene.setOnKeyPressed(ke->pacMaze.handleKey(ke));
        stage.setResizable(false);
        stage.show();
        
        //pacMaze.play(); //animates the background props
    }



    
    public static void main(String[] args) {
        launch(args);
    }

}
