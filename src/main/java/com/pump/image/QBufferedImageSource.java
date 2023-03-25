package com.pump.image;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.PixelIterator;

import java.awt.image.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is loosely modeled after the sun.awt.image.OffScreenImageSource, but it includes a few changes. Note these
 * changes are only activated if the BufferedImage is one of the image types supported by {@link com.pump.image.pixel.BufferedImageIterator}.
 * If the image is unsupported we fall back to the BufferedImage's default ImageProducer. The improvements/changes include:
 * <ul><li>This class calls {@link ImageConsumer#setHints(int)} before sending pixels.</li>
 * <li>This resolves <a href="https://bugs.openjdk.org/browse/JDK-4200096?jql=status%20%3D%20Open%20AND%20text%20~%20%22OffScreenImageSource%22%20ORDER%20BY%20priority%20DESC">JDK-4200096</a></li>
 * <li>This supports multithreaded production. The <code>OffScreenImageSource</code> is thread-safe because it
 * is synchronized, but it is synchronized in a way that only lets one thread receive pixels at a time.</li>
 * <li>This will produce pixel data in formats other than ARGB (to save time converting).</li>
 * <li></li></ul>
 */
public class QBufferedImageSource implements ImageProducer {
    private BufferedImage image;
    private Map<?, ?> properties;
    private List<ImageConsumer> consumers = new CopyOnWriteArrayList<>();
    private ImageProducer backupImageProducer;

    QBufferedImageSource(BufferedImage image,
                                Map<?, ?> properties, ImageProducer backupImageProducer) {
        this.image = Objects.requireNonNull(image);
        this.backupImageProducer = Objects.requireNonNull(backupImageProducer);
        if (properties != null) {
            this.properties = properties;
        } else {
            this.properties = new Hashtable<String, Object>();
        }
    }

    @Override
    public void addConsumer(ImageConsumer consumer) {
        consumers.add(consumer);

        PixelIterator<?> pixelIter = null;
        ImageType<?> iterType = null;
        try {
            pixelIter = BufferedImageIterator.create(image);
            iterType = ImageType.get(pixelIter.getType());
        } catch(RuntimeException e) {
            // this means the BufferedImage isn't supported (yet) by BufferedImageIterator,
            // so we'll use our backup ImageProducer
        }
        if (pixelIter != null && iterType != null) {
            produce(consumer, pixelIter, iterType);
        } else {
            backupImageProducer.addConsumer(consumer);
            try {
                backupImageProducer.startProduction(consumer);
            } finally {
                backupImageProducer.removeConsumer(consumer);
            }
        }
    }

    @Override
    public boolean isConsumer(ImageConsumer consumer) {
        return consumers.contains(consumer);
    }

    @Override
    public void removeConsumer(ImageConsumer consumer) {
        consumers.remove(consumer);
    }

    @Override
    public void startProduction(ImageConsumer consumer) {
        addConsumer(consumer);
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer consumer) {
        // intentionally empty
    }

    private void produce(ImageConsumer consumer, PixelIterator pixelIter, ImageType<?> imageType) {
        // at any time the consumer could be unregistered, so we constantly
        // check to see if a consumer is still relevant (JDK-4200096)

        try {
            consumer.setDimensions(image.getWidth(), image.getHeight());

            if (consumers.contains(consumer))
                consumer.setProperties(new Hashtable<>(properties));

            if (consumers.contains(consumer))
                consumer.setHints(ImageConsumer.SINGLEPASS |
                        ImageConsumer.COMPLETESCANLINES |
                        ImageConsumer.TOPDOWNLEFTRIGHT |
                        ImageConsumer.SINGLEFRAME);

            if (consumers.contains(consumer))
                consumer.setColorModel(imageType.getColorModel());

            int width  = image.getWidth();

            if (imageType.isInt()) {
                int[] row = new int[width * pixelIter.getPixelSize()];
                int y = 0;
                while ( !pixelIter.isDone() && consumers.contains(consumer)) {
                    pixelIter.next(row, 0);
                    consumer.setPixels(0, y++, width, 1, imageType.getColorModel(), row, 0, row.length);
                }
            } else if (imageType.isByte()) {
                byte[] row = new byte[width * pixelIter.getPixelSize()];
                int y = 0;
                while ( !pixelIter.isDone() && consumers.contains(consumer)) {
                    pixelIter.next(row, 0);
                    consumer.setPixels(0, y++, width, 1, imageType.getColorModel(), row, 0, row.length);
                }
            }

            if (consumers.contains(consumer))
                consumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);

            if (consumers.contains(consumer))
                consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
        } catch (Exception e) {
            if (consumers.contains(consumer))
                consumer.imageComplete(ImageConsumer.IMAGEERROR);
        }
    }
}
