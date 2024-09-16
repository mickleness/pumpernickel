/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.pixel;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * This lets a PixelIterator.Source act as a <code>java.awt.image.ImageProducer</code>.
 */
public class PixelSourceImageProducer implements ImageProducer {
    static int threadCtr;

    static final ExecutorService IMAGE_PRODUCTION_QUEUE = new ThreadPoolExecutor(0, 4,
            1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "PixelIteratorImageProducer-thread-" + (threadCtr++));
                }
            }, new ThreadPoolExecutor.AbortPolicy());

    static class ImageProductionRunnable implements Runnable {
        PixelSourceImageProducer imageProducer;

        public ImageProductionRunnable(PixelSourceImageProducer imageProducer) {
            this.imageProducer = Objects.requireNonNull(imageProducer);
        }

        @Override
        public void run() {
            ImageConsumer[] consumers = imageProducer.popPendingImageConsumers();
            if (consumers.length == 0)
                return;
            ImageConsumer multiConsumer = new MultiImageConsumer(consumers);

            int exitCode = ImageConsumer.IMAGEERROR;
            try {
                int width = imageProducer.pixelSource.getWidth();
                int height = imageProducer.pixelSource.getHeight();
                multiConsumer.setDimensions(width, height);

                try (PixelIterator iterator = imageProducer.pixelSource.createPixelIterator()) {
                    int hints = ImageConsumer.COMPLETESCANLINES + ImageConsumer.SINGLEPASS + ImageConsumer.SINGLEFRAME;
                    if (iterator.isTopDown())
                        hints += ImageConsumer.TOPDOWNLEFTRIGHT;

                    multiConsumer.setHints(hints);

                    ImageType type = ImageType.get(iterator.getType());
                    ColorModel colorModel = type.getColorModel();

                    multiConsumer.setColorModel(colorModel);

                    int pixelSize = iterator.getPixelSize();

                    int y = iterator.isTopDown() ? 0 : height - 1;
                    int yIncr = iterator.isTopDown() ? 1 : -1;
                    if (iterator.isInt()) {
                        int[] row = new int[iterator.getWidth() * pixelSize];
                        for (int k = 0; k < height; k++) {
                            iterator.next(row, 0);
                            multiConsumer.setPixels(0, y, width, 1, colorModel, row, 0, width * pixelSize);
                            y += yIncr;
                        }
                    } else {
                        byte[] row = new byte[iterator.getWidth() * iterator.getPixelSize()];
                        for (int k = 0; k < height; k++) {
                            iterator.next(row, 0);
                            multiConsumer.setPixels(0, y, width, 1, colorModel, row, 0, width * pixelSize);
                            y += yIncr;
                        }
                    }
                }
                exitCode = ImageConsumer.STATICIMAGEDONE;
            } catch(Throwable t) {
                t.printStackTrace();
            } finally {
                multiConsumer.imageComplete(exitCode);
            }
        }
    }

    /**
     * All ImageConsumers that have been registered with this ImageProducer.
     */
    private final List<ImageConsumer> allConsumers = new LinkedList<>();

    /**
     * The ImageConsumers that are currently waiting for a call to {@link #startProduction(ImageConsumer)}.
     */
    private final List<ImageConsumer> waitingConsumers = new LinkedList<>();

    private final PixelIterator.Source pixelSource;

    public PixelSourceImageProducer(PixelIterator.Source pixelSource) {
        this.pixelSource = Objects.requireNonNull(pixelSource);
    }

    @Override
    public void addConsumer(ImageConsumer consumer) {
        synchronized (allConsumers) {
            if (!allConsumers.contains(consumer))
                allConsumers.add(consumer);
            waitingConsumers.add(consumer);
        }
    }

    @Override
    public boolean isConsumer(ImageConsumer consumer) {
        synchronized (allConsumers) {
            return allConsumers.contains(consumer);
        }
    }

    @Override
    public void removeConsumer(ImageConsumer consumer) {
        synchronized (allConsumers) {
            allConsumers.remove(consumer);
            waitingConsumers.remove(consumer);
        }
    }

    @Override
    public void startProduction(ImageConsumer consumer) {
        synchronized (allConsumers) {
            addConsumer(consumer);
        }
        IMAGE_PRODUCTION_QUEUE.submit(new ImageProductionRunnable(this));
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer consumer) {
        // this does nothing. Either our underlying pixelSource produced data in a top-to-bottom
        // order or it didn't; we can't usually do much to change that here.
    }

    private ImageConsumer[] popPendingImageConsumers() {
        synchronized (allConsumers) {
            ImageConsumer[] returnValue = waitingConsumers.toArray(new ImageConsumer[0]);
            waitingConsumers.clear();
            return returnValue;
        }
    }
}