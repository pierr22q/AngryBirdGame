package angryflappybird;

import java.io.File;
import java.util.HashMap;

import javax.swing.InputMap;
import javax.swing.KeyStroke;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Define class for all the object coefficients and initializing
 * images.
 * 
 * @author Nikki Cheng, Kareena Joshipura, Qianna Pierre
 */
public class Defines {
    
    // dimension of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;

    // coefficients related to the blob
    final int BLOB_WIDTH = 70;
    final int BLOB_HEIGHT = 70;
    final int BLOB_POS_X = 70;
    final int BLOB_POS_Y = 200;
    final int BLOB_DROP_TIME = 300000000;   // the elapsed time threshold before the blob starts dropping
    final int BLOB_DROP_VEL = 300;          // the blob drop velocity
    final int BLOB_FLY_VEL = -40;
    final int BLOB_IMG_LEN = 4;
    final int BLOB_IMG_PERIOD = 5;
    
    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 100;
    final int FLOOR_COUNT = 2;
    
    // coefficients related to time
    final int SCENE_SHIFT_TIME = 3;
    double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    
    // coefficients related to pipes
    final int PIPE_HEIGHT = 245;
    final int PIPE_WIDTH = 80;
    
    // coefficients related to meteor
    final int METEOR_WIDTH = 60;
    final int METEOR_HEIGHT = 80;
    final int METEOR_POS_X = 245;
    final int METEOR_POS_Y = 200; 
    final int METEOR_DROP_VEL = 300;
    final int METEOR_COUNT = 5;

    // coefficients related to pipe
    final int PIPE_COUNT = 2;
    final int PIPE_POS_X = 0;
    final int PIPE_POS_Y = 0;
    
    // coefficients related to fuel
    final int FUEL_WIDTH = 70;
    final int FUEL_HEIGHT = 70;
    final int FUEL_POS_X = 170;
    final int FUEL_POS_Y = 220;
    final int FUEL_COUNT = 100;
    final int FUEL_VEL = 120;
    
    // coefficients related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
    private final String IMAGE_DIR = "../resources/images/";
    // code meant for sound, but doesn't work on our computer
    // private final String SOUND_DIR = "../resources/sounds/";
    
    // code meant for sound, but doesn't work on our computer
    // File laserResource;
    // AudioClip laserPlayer;
    // String path_collision_sound;
    // AudioClip collisionSound;

    final String[] IMAGE_FILES = {"background","rocket0", "rocket1", "rocket2", "rocket3", "bottomPipe","floor","topPipe", "meteor", "fuel", "gfuel"};
    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    
    //nodes on the scene graph
    Button startButton;
    
    
    // constructor
    Defines() {

        // code meant for sound, but doesn't work on our computer
        // path_collision_sound = pathAudio("collide");
        // collisionSound = new AudioClip(path_collision_sound);

         for(int i=0; i<IMAGE_FILES.length; i++) {
                Image img;
                if (i == 6) {
                    img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT, false, false);
                }
                else if (i == 5 || i == 7 ) {
                    img = new Image(pathImage(IMAGE_FILES[i]), PIPE_WIDTH, PIPE_HEIGHT, false, false);
                }
                else if (i == 1 || i == 2 || i == 3 || i == 4){
                    img = new Image(pathImage(IMAGE_FILES[i]), BLOB_WIDTH, BLOB_HEIGHT, false, false);
                }
                else if (i == 8 ) {
                    img = new Image(pathImage(IMAGE_FILES[i]), METEOR_WIDTH, METEOR_HEIGHT, false, false);
                }
                else if (i == 9 ) {
                    img = new Image(pathImage(IMAGE_FILES[i]), FUEL_WIDTH, FUEL_HEIGHT, false, false);
                }
                else if (i == 10 ) {
                    img = new Image(pathImage(IMAGE_FILES[i]), FUEL_WIDTH, FUEL_HEIGHT, false, false);
                }
                else {
                    img = new Image(pathImage(IMAGE_FILES[i]), SCENE_WIDTH, SCENE_HEIGHT, false, false);
                }
                IMAGE.put(IMAGE_FILES[i],img);
            }
            
        
        // initialize image views
        for(int i=0; i<IMAGE_FILES.length; i++) {
            ImageView imgView = new ImageView(IMAGE.get(IMAGE_FILES[i]));
            IMVIEW.put(IMAGE_FILES[i],imgView);
        }
        
        // initialize scene nodes
        startButton = new Button("Go!");

    }
    
    /**
     * file path for image
     * @param filepath
     * @return fullpath
     */
    public String pathImage(String filepath) {
        String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
        return fullpath;     
    }
    
//    code meant for sound, wasn't working on our computer
//    public String pathAudio(String filepath) {
//        String fullpath = getClass().getResource(SOUND_DIR+filepath+".mp3").toExternalForm();
//        return fullpath;
//    }
    
    
    /**
     * method for resizing image 
     * @param filepath
     * @param width
     * @param height
     * @return resized image
     */
    public Image resizeImage(String filepath, int width, int height) {
        IMAGE.put(filepath, new Image(pathImage(filepath), width, height, false, false));
        return IMAGE.get(filepath);
    }
}
