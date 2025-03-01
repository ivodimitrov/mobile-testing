package utils.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Image utils.
 */
public class Image {

    /**
     * Compare two images.
     *
     * @param actual   Actual image.
     * @param expected Expected image.
     * @return ImageComparisonResult.
     */
    public static ImageComparisonResult compare(BufferedImage actual,
                                                BufferedImage expected) {
        return compare(actual, expected, 30, 0);
    }

    /**
     * Compare two images.
     *
     * @param actual          Actual image.
     * @param expected        Expected image.
     * @param pixelSimilarity If sum of RGB difference in less then pixelSimilarity then two pixels will be same.
     * @return ImageComparisonResult.
     */
    public static ImageComparisonResult compare(BufferedImage actual,
                                                BufferedImage expected,
                                                int pixelSimilarity) {
        return compare(actual, expected, pixelSimilarity, 0);
    }

    /**
     * Compare two images.
     *
     * @param actual             Actual image.
     * @param expected           Expected image.
     * @param pixelSimilarity    If sum of RGB difference in less then pixelSimilarity then two pixels will be same.
     * @param ignoreHeaderPixels Ignore pixels from top of the images.
     * @return ImageComparisonResult.
     */
    public static ImageComparisonResult compare(BufferedImage actual,
                                                BufferedImage expected,
                                                int pixelSimilarity,
                                                int ignoreHeaderPixels) {
        long diffPixels = 0;
        double diffPercent;

        BufferedImage diffImage = copy(actual);

        int width = actual.getWidth();
        int height = actual.getHeight();
        int expectedWidth = expected.getWidth();
        int expectedHeight = expected.getHeight();

        if (width != expectedWidth || height != expectedHeight) {
            diffPixels = width * height;
            diffPercent = 100.0;
        } else {
            Color red = new Color(255, 0, 0);
            int redRgb = red.getRGB();

            for (int y = ignoreHeaderPixels; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int result = pixelDiff(actual.getRGB(x, y), expected.getRGB(x, y));
                    if (result > pixelSimilarity) {
                        // Increase count in diffPixels
                        diffPixels++;
                        // Write different pixels in diffImage with red color
                        diffImage.setRGB(x, y, redRgb);

                    }
                }
            }
            diffPercent = (100 * diffPixels) / (double) (expectedWidth * expectedHeight);
        }

        return new ImageComparisonResult(diffPixels, diffPercent, actual, diffImage, expected);
    }

    /**
     * Save image to file.
     *
     * @param image    BufferedImage object.
     * @param filePath File path as String.
     * @throws IOException When save operation fails.
     */
    public static void save(BufferedImage image, String filePath) throws IOException {
        File parentFolder = new File(filePath).getParentFile();
        if (!parentFolder.exists()) {
            System.out.println(parentFolder.getAbsolutePath() + " does NOT exist. Creating it right now.");
            parentFolder.mkdirs();
        }
        ImageIO.write(image, "png", new File(filePath));
    }

    private static int pixelDiff(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = image.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return image;
    }
}
