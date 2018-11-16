import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class DisplayWindow extends Application
{

    private final static ArrayList<String> images = loadImages();

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {

        final int IMAGE_WIDTH = 400;

        // 2, 5, 9
        Random r = new Random();
        MyImage image = new MyImage(images.get(r.nextInt(10)));

        ImageView topLeftImg = new ImageView();
        topLeftImg.setImage(image.getImage());
        topLeftImg.setPreserveRatio(true);
        topLeftImg.setFitWidth(IMAGE_WIDTH);

        ImageView topRightImg = new ImageView();
        topRightImg.setImage(image.getEdgedVersion(130).getImage());
        topRightImg.setPreserveRatio(true);
        topRightImg.setFitWidth(IMAGE_WIDTH);

        ImageView bottomLeftImg = new ImageView();
        bottomLeftImg.setPreserveRatio(true);
        bottomLeftImg.setFitWidth(IMAGE_WIDTH);
        //bottomLeftImg.setImage(image.getEdgedVersion(130).getImage());

        ImageView bottomRightImg = new ImageView();
        bottomRightImg.setPreserveRatio(true);
        bottomRightImg.setFitWidth(IMAGE_WIDTH);
        //bottomRightImg.setImage(image.getCircledVersion(50, 0).getImage());


        GridPane imageGrid = new GridPane();
        imageGrid.add(topLeftImg, 0, 0);
        imageGrid.add(topRightImg, 1, 0);
        imageGrid.add(bottomLeftImg, 0, 1);
        imageGrid.add(bottomRightImg, 1, 1);

        Button btnRun = new Button();
        btnRun.setText("Run");
        btnRun.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                bottomRightImg.setImage(image.getCircledVersion(50, 0, bottomLeftImg).getImage());
            }
        });

        VBox rightColumn = new VBox();
        rightColumn.setSpacing(15);
        rightColumn.getChildren().addAll(btnRun);

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(imageGrid,rightColumn);

        Scene scene = new Scene(hbox);
        primaryStage.setScene(scene);
        double imageHeight = image.getHeight() * ((double)IMAGE_WIDTH / image.getWidth());
        primaryStage.setHeight(imageHeight * 2.0 + 35);
        primaryStage.show();
    }

    private static ArrayList<String> loadImages()
    {
        ArrayList<String> list = new ArrayList<>();
        list.add("https://upload.wikimedia.org/wikipedia/commons/3/3f/Bikesgray.jpg");
        list.add("https://upload.wikimedia.org/wikipedia/commons/thumb/f/f0/Valve_original_%281%29.PNG/193px-Valve_original_%281%29.PNG");
        list.add("https://upload.wikimedia.org/wikipedia/commons/f/f0/Valve_original_%281%29.PNG");
        list.add("http://www.goodies.ma/wp-content/uploads/2017/11/material-555x382.jpg");
        list.add("https://www.busybeecandles.co.uk/wp/wp-content/uploads/2013/08/apple-benefits37.jpg");
        list.add("http://images.streetstylestore.com/8/1/8/2/4/81824-home_default.jpg");
        list.add(new File("src/107.jpg").toURI().toString());
        list.add(new File("src/happy-cartoon-whale-sun-cloud-23624490.jpg").toURI().toString());
        list.add("http://www.cs.utah.edu/~sshankar/cs6640/project5/images/circle/pattern.png");
        list.add("https://5.imimg.com/data5/XX/SM/MY-32940434/dvd-branding-250x250.jpg");
        list.add(new File("src/WIN_20181116_13_15_07_Pro.jpg").toURI().toString());
        return list;
    }

}
