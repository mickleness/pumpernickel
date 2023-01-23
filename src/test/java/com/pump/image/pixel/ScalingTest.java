package com.pump.image.pixel;

import junit.framework.TestCase;

import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ScalingTest extends TestCase {
    static Color[] colors = new Color[] {
            Color.red,
            Color.orange,
            Color.yellow,
            Color.green,
            Color.cyan,
            Color.blue
    };

    public void testWidth() throws Throwable {
        int[] imageTypes = new int[] {
                BufferedImage.TYPE_INT_ARGB,
                BufferedImage.TYPE_INT_RGB,
                BufferedImage.TYPE_3BYTE_BGR,
                BufferedImage.TYPE_4BYTE_ABGR,
                BufferedImage.TYPE_INT_RGB,
                BufferedImage.TYPE_INT_ARGB_PRE
        };

        List<Throwable> errors = new LinkedList<>();

        for (int imageType : imageTypes) {
            System.err.println("Testing image type: " + ImageType.get(imageType));

            try {
                BufferedImage rainbowImage = createRainbowImage(imageType);
                BufferedImage scaled = Scaling.scale(rainbowImage, 80, 70);
                Map<Color, Integer> colorMap = getColorMap(scaled);

                int redCount = colorMap.containsKey(Color.red) ? colorMap.get(Color.red) : 0;
                int orangeCount = colorMap.containsKey(Color.orange) ? colorMap.get(Color.orange) : 0;
                int yellowCount = colorMap.containsKey(Color.yellow) ? colorMap.get(Color.yellow) : 0;
                int greenCount = colorMap.containsKey(Color.green) ? colorMap.get(Color.green) : 0;
                int cyanCount = colorMap.containsKey(Color.cyan) ? colorMap.get(Color.cyan) : 0;
                int blueCount = colorMap.containsKey(Color.blue) ? colorMap.get(Color.blue) : 0;

                int totalPixels = scaled.getWidth() * scaled.getHeight();

                float redPercent = redCount * 100 / totalPixels;
                float orangePercent = orangeCount * 100 / totalPixels;
                float yellowPercent = yellowCount * 100 / totalPixels;
                float greenPercent = greenCount * 100 / totalPixels;
                float cyanPercent = cyanCount * 100 / totalPixels;
                float bluePercent = blueCount * 100 / totalPixels;

                // a little bit of detail may be antialiased away, but we should have 6 really clear stripes:
                assertTrue("red band missing", redPercent > 10);
                assertTrue("orange band missing", orangePercent > 10);
                assertTrue("yellow band missing", yellowPercent > 10);
                assertTrue("green band missing", greenPercent > 10);
                assertTrue("cyan band missing", cyanPercent > 10);
                assertTrue("blue band missing", bluePercent > 10);
            } catch(Throwable e) {
                errors.add(e);
                e.printStackTrace();
            }
        }

        if (!errors.isEmpty())
            throw errors.get(0);
    }

    private Map<Color,Integer> getColorMap(BufferedImage image) {
        Map<Color,Integer> map = new HashMap<>();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                Color c = new Color(rgb);
                Integer f = map.get(c);
                if (f == null) {
                    map.put(c, Integer.valueOf(1));
                } else {
                    map.put(c, Integer.valueOf(1 + f));
                }
            }
        }
        return map;
    }

    private BufferedImage createRainbowImage(int imageType) {
        BufferedImage bi = new BufferedImage(1000, 750, imageType);

        Graphics2D g = bi.createGraphics();
        int x = 0;
        for (int a = 0; a < colors.length; a++) {
            int endX = bi.getWidth() * (a + 1) / colors.length;
            if (a == colors.length - 1) {
                endX = bi.getWidth();
            }
            g.setColor(colors[a]);
            g.fillRect(x, 0, endX - x, bi.getHeight());

            x = endX;
        }
        g.dispose();

        return bi;
    }

}