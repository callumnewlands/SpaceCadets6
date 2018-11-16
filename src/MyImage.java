import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;

public class MyImage
{

    public enum GreyScaleMethod
    {
        PIXEL_BY_PIXEL,
        GRAPHICS
    }

    private BufferedImage img;

    public MyImage(String url)
    {
        this.img = SwingFXUtils.fromFXImage(new Image(url), null);
    }

    public MyImage(Image img)
    {
        this.img = SwingFXUtils.fromFXImage(img, null);
    }

    public MyImage(BufferedImage img)
    {
        this.img = img;
    }

    public MyImage getGreyscaleVersion(GreyScaleMethod method)
    {
        switch (method) {
            case GRAPHICS:
                return greyscaleByGraphics();
            case PIXEL_BY_PIXEL:
                return greyscaleByPixels();
            default:
                return greyscaleByGraphics();
        }
    }

    private MyImage greyscaleByGraphics()
    {
        BufferedImage newImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = newImage.getGraphics();
        g.drawImage(this.img, 0, 0, null);
        g.dispose();
        return new MyImage(newImage);
    }

    private MyImage greyscaleByPixels()
    {
        BufferedImage newImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                newImage.setRGB(x, y, this.img.getRGB(x, y));
            }
        }
        return new MyImage(newImage);
    }

    public int getHeight()
    {
        return this.img.getHeight();
    }

    public int getWidth()
    {
        return this.img.getWidth();
    }

    public Image getImage()
    {
        return SwingFXUtils.toFXImage(this.img, null);
    }

    public BufferedImage getBufferedImage()
    {
        return this.img;
    }

    public MyImage getCircledVersion(int minimumRadius, int threshold, ImageView accumulatorImageView)
    {
        BufferedImage edged = this.getEdgedVersion(0).getBufferedImage();
        MyImage circled = new MyImage(this.getBufferedImage());
        BufferedImage accumulatorImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);

        final int magnitudeThreshold = 150;

        final int maxRadius = this.getWidth() / 2;
        int[][][] accumulator = new int[this.getWidth()][this.getHeight()][maxRadius];
        int[] currentHighestAccumulator = {0, 0, 0, 0};

        for (int x = 0; x < this.getWidth(); x++) {
            System.out.printf("%d / %d %n", x, this.getWidth());
            //System.out.printf("%f%% %n", Math.round(((float)x / this.getWidth()) * 100.0) / 100.0);
            for (int y = 0; y < this.getHeight(); y++) {
                if (new Color(edged.getRGB(x, y)).getRed() > magnitudeThreshold) {
                    for (int radius = minimumRadius; radius < maxRadius; radius++) {
                        for (int theta = 0; theta < 360; theta++) {

                            int a = x + (int)(radius * Math.cos(Math.toRadians(theta)));
                            int b = y + (int)(radius * Math.sin(Math.toRadians(theta)));

                            try {
                                accumulator[a][b][radius] += 1;
                                int currentVote = accumulator[a][b][radius];
                                if (currentVote > currentHighestAccumulator[3]) {
                                    currentHighestAccumulator[0] = a;
                                    currentHighestAccumulator[1] = b;
                                    currentHighestAccumulator[2] = radius;
                                    currentHighestAccumulator[3] = currentVote;
                                }
                            }
                            catch (IndexOutOfBoundsException e) {}
                        }
                    }
                }
            }
        }

        int xCentre = currentHighestAccumulator[0];
        int yCentre = currentHighestAccumulator[1];
        int radius = currentHighestAccumulator[2];


        //TODO remove this:
        for (int x = 0; x < this.getWidth(); x++)
        {
            //System.out.printf("%d / %d %n", x, this.getWidth());
            for (int y = 0; y < this.getHeight(); y++)
            {
                //System.out.println(accumulator[x][y][radius])
                if (!(new Color(edged.getRGB(x, y)).getRed() > magnitudeThreshold)) {
                    accumulatorImage.setRGB(x, y, Color.HSBtoRGB(accumulator[x][y][radius] / 100.0f, 1.0f, 1.0f));
                } else {
                    accumulatorImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        final int CIRCLE_WIDTH = 2;

        for (int theta = 0; theta < 360; theta++) {
            try {
                for (int i = -CIRCLE_WIDTH; i < CIRCLE_WIDTH; i++) {
                    int a = xCentre + (int) ((radius + i) * Math.cos(Math.toRadians(theta)));
                    int b = yCentre + (int) ((radius + i) * Math.sin(Math.toRadians(theta)));

                    circled.getBufferedImage().setRGB(a, b, Color.GREEN.getRGB());
                }
            }
            catch (IndexOutOfBoundsException e) {}
        }

//        final int POINT_RADIUS = 3;
//
//        for (int i = -POINT_RADIUS; i < POINT_RADIUS; i++) {
//            for (int j = -POINT_RADIUS; j < POINT_RADIUS; j++) {
//                circled.getBufferedImage().setRGB(xCentre + i, yCentre + j, Color.BLACK.getRGB());
//            }
//        }

        accumulatorImageView.setImage(SwingFXUtils.toFXImage(accumulatorImage, null));
        return circled;
    }


    // if threshold = 0, do not threshold the image
    public MyImage getEdgedVersion(int threshold)
    {

        if (threshold < 0 || threshold > 255)
        {
            throw new IllegalArgumentException("Threshold must be between 0 and 255");
        }

        BufferedImage greyscale = this.getGreyscaleVersion(GreyScaleMethod.GRAPHICS).getBufferedImage();

        float[] xKernelData = {1, 0, -1, 2, 0, -2, 1, 0, -1};
        ConvolveOp xOperation = new ConvolveOp(new Kernel(3, 3, xKernelData));
        BufferedImage xGradient = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        xOperation.filter(greyscale, xGradient);

        float[] yKernelData = {1, 2, 1, 0, 0, 0, -1, -2, -1};
        ConvolveOp yOperation = new ConvolveOp(new Kernel(3, 3, yKernelData));
        BufferedImage yGradient = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        yOperation.filter(greyscale, yGradient);

        BufferedImage newImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                Color xPixel = new Color(xGradient.getRGB(x, y));
                Color yPixel = new Color(yGradient.getRGB(x, y));
                int newMagnitude = (int)Math.round(Math.sqrt(Math.pow(xPixel.getRed(), 2) + Math.pow(yPixel.getRed(), 2)));

                if (threshold > 0)
                {
                    newMagnitude = newMagnitude < threshold ? 0 : 255;
                }
                else
                {
                    newMagnitude = Math.min(newMagnitude, 255);
                }

                Color newColour = new Color(newMagnitude, newMagnitude, newMagnitude);
                newImage.setRGB(x, y, newColour.getRGB());
            }
        }
        return new MyImage(newImage);

    }

}
