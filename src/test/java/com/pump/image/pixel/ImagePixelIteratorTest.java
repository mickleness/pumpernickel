package com.pump.image.pixel;

import com.pump.image.MutableBufferedImage;
import junit.framework.TestCase;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImagePixelIteratorTest extends TestCase {

    enum PixelOrder {
        RANDOM(ImageConsumer.RANDOMPIXELORDER),
        SINGLE_PASS_RANDOM(ImageConsumer.RANDOMPIXELORDER | ImageConsumer.SINGLEPASS),
        SINGLE_PASS_TOP_DOWN_LEFT_RIGHT_COMPLETE_SCANLINES(ImageConsumer.SINGLEPASS | ImageConsumer.COMPLETESCANLINES | ImageConsumer.TOPDOWNLEFTRIGHT),
        SINGLE_PASS_TOP_DOWN_LEFT_RIGHT_PARTIAL_SCANLINES(ImageConsumer.SINGLEPASS | ImageConsumer.TOPDOWNLEFTRIGHT),
        SINGLE_PASS_DOWN_UP_LEFT_RIGHT_COMPLETE_SCANLINES(ImageConsumer.SINGLEPASS | ImageConsumer.COMPLETESCANLINES),
        TWO_PASSES_TOP_DOWN_LEFT_RIGHT_COMPLETE_SCANLINES(ImageConsumer.COMPLETESCANLINES | ImageConsumer.TOPDOWNLEFTRIGHT);

        int hints;
        PixelOrder(int hints) {
            this.hints = hints | ImageConsumer.SINGLEFRAME;
        }

    }

    enum CompletionType {
        SUCCESS(false),
        STALL_MIDWAY_THROUGH(false),
        ERROR_IMMEDIATELY(true),
        ERROR_BEFORE_PIXELS(true),
        ERROR_MIDWAY_THROUGH(true);

        public boolean expectError;

        CompletionType(boolean expectError) {
            this.expectError = expectError;
        }
    }

    public class MyImageProducer implements ImageProducer {
        private final ImageType[] imageTypes;
        private final BufferedImage bi;
        private final PixelOrder pixelOrder;
        private final CompletionType completionType;

        CopyOnWriteArrayList consumers = new CopyOnWriteArrayList();

        public MyImageProducer(BufferedImage bi,ImageType[] imageTypes, PixelOrder pixelOrder,CompletionType completionType) {
            this.bi = bi;
            this.imageTypes = imageTypes;
            this.pixelOrder = pixelOrder;
            this.completionType = completionType;
        }

        @Override
        public void addConsumer(ImageConsumer ic) {
            consumers.add(ic);
            startProduction(ic);
        }

        @Override
        public boolean isConsumer(ImageConsumer ic) {
            return consumers.contains(ic);
        }

        @Override
        public void removeConsumer(ImageConsumer ic) {
            consumers.remove(ic);
        }

        @Override
        public void startProduction(ImageConsumer ic) {
            if (completionType == CompletionType.ERROR_IMMEDIATELY) {
                ic.imageComplete(ImageConsumer.IMAGEERROR);
                return;
            }
            ic.setDimensions(bi.getWidth(), bi.getHeight());
            ic.setHints(pixelOrder.hints);

            if (completionType == CompletionType.ERROR_BEFORE_PIXELS) {
                ic.imageComplete(ImageConsumer.IMAGEERROR);
                return;
            }

            List<Rectangle> queuedRectangles = new LinkedList<>();

            if (pixelOrder == PixelOrder.RANDOM || pixelOrder == PixelOrder.SINGLE_PASS_RANDOM) {
                List<Point> points = new ArrayList<>();
                for (int x = 0; x < bi.getWidth(); x++) {
                    for (int y = 0; y < bi.getHeight(); y++) {
                        points.add(new Point(x,y));
                    }
                }
                Random r = new Random(0);
                Collections.shuffle(points, r);
                for (Point point : points) {
                    queuedRectangles.add(new Rectangle(point.x, point.y, 1, 1));
                }
            } else if (pixelOrder == PixelOrder.TWO_PASSES_TOP_DOWN_LEFT_RIGHT_COMPLETE_SCANLINES) {
                for (int y = 0; y < bi.getHeight(); y += 2) {
                    queuedRectangles.add(new Rectangle(0, y, bi.getWidth(), 1));
                }
                for (int y = 1; y < bi.getHeight(); y += 2) {
                    queuedRectangles.add(new Rectangle(0, y, bi.getWidth(), 1));
                }
            } else if (pixelOrder == PixelOrder.SINGLE_PASS_TOP_DOWN_LEFT_RIGHT_COMPLETE_SCANLINES) {
                for (int y = 0; y < bi.getHeight(); y++) {
                    queuedRectangles.add(new Rectangle(0, y, bi.getWidth(), 1));
                }
            } else if (pixelOrder == PixelOrder.SINGLE_PASS_TOP_DOWN_LEFT_RIGHT_PARTIAL_SCANLINES) {
                for (int y = 0; y < bi.getHeight(); y++) {
                    for (int x = 0; x < bi.getWidth(); x += 10) {
                        queuedRectangles.add(new Rectangle(x, y, Math.min(bi.getWidth() - x, 10), 1));
                    }
                }
            } else if (pixelOrder == PixelOrder.SINGLE_PASS_DOWN_UP_LEFT_RIGHT_COMPLETE_SCANLINES) {
                for (int y = bi.getHeight() - 1; y >= 0; y--) {
                    queuedRectangles.add(new Rectangle(0, y, bi.getWidth(), 1));
                }
            }

            int midway = queuedRectangles.size() / 2;
            int ctr = 0;
            while (!queuedRectangles.isEmpty()) {
                if (ctr == midway) {
                    if (completionType == CompletionType.STALL_MIDWAY_THROUGH) {
                        return;
                    } else if (completionType == CompletionType.ERROR_MIDWAY_THROUGH) {
                        ic.imageComplete(ImageConsumer.IMAGEERROR);
                        return;
                    }
                }

                ImageType t = imageTypes[ (ctr++) % imageTypes.length ];
                Rectangle r = queuedRectangles.remove(0);
                BufferedImage subimage = bi.getSubimage(r.x, r.y, r.width, r.height);
                PixelIterator pixelIterator = BufferedImageIterator.create(subimage);
                pixelIterator = t.createPixelIterator(pixelIterator);

                if (t.isInt()) {
                    int[] array = new int[r.width * r.height * t.getSampleCount()];
                    int offset = 0;
                    while (!pixelIterator.isDone()) {
                        pixelIterator.next(array, offset);
                        offset += r.width * t.getSampleCount();
                    }

                    ic.setPixels(r.x, r.y, r.width, r.height, t.getColorModel(), array, 0, r.width* t.getSampleCount());
                } else if(t.isByte()) {
                    byte[] array = new byte[r.width * r.height * t.getSampleCount()];
                    int offset = 0;
                    while (!pixelIterator.isDone()) {
                        pixelIterator.next(array, offset);
                        offset += r.width * t.getSampleCount();
                    }

                    ic.setPixels(r.x, r.y, r.width, r.height, t.getColorModel(), array, 0, r.width * t.getSampleCount());
                }
            }

            if (completionType == CompletionType.SUCCESS) {
                ic.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
            }
        }

        @Override
        public void requestTopDownLeftRightResend(ImageConsumer ic) {
            // intentionally empty
        }
    }

    /**
     * Test several combinations of an ImageProducer offering pixel data / errors / etc in varying contexts.
     */
    @Test
    public void testImagePixelIterator() {
        // we're reading a BufferedImage that's already in memory; we can afford a smaller timeout interval:
        ImagePixelIterator.TIMEOUT_MILLIS = 100;

        BufferedImage bi = new MutableBufferedImage(ScalingTest.createRainbowImage(60, 60, BufferedImage.TYPE_INT_RGB, true));
        ImageType[] outputTypes = new ImageType[] {
                null,
                ImageType.INT_ARGB_PRE,
                ImageType.INT_ARGB,
                ImageType.INT_RGB,
                ImageType.INT_BGR,
                ImageType.BYTE_ABGR_PRE,
                ImageType.BYTE_ABGR,
                ImageType.BYTE_BGR,
        };


        // TODO: add alternating types
        ImageType[][] imageTypeArrays = new ImageType[][] {
                new ImageType[] {ImageType.INT_ARGB_PRE},
                new ImageType[] {ImageType.INT_ARGB},
                new ImageType[] {ImageType.INT_RGB},
                new ImageType[] {ImageType.INT_BGR},
                new ImageType[] {ImageType.BYTE_ABGR_PRE},
                new ImageType[] {ImageType.BYTE_ABGR},
                new ImageType[] {ImageType.BYTE_BGR},
        };

        int ctr = 0;
        for (ImageType[] imageTypes : imageTypeArrays) {
            for (PixelOrder pixelOrder : PixelOrder.values()) {
                for (CompletionType completionType : CompletionType.values()) {
                    MyImageProducer p = new MyImageProducer(bi, imageTypes, pixelOrder, completionType);
                    for (ImageType outputType : outputTypes) {
                        ctr++;
                        System.out.println("ctr = "+ ctr + " " + Arrays.asList(imageTypes) + " " + pixelOrder.name()+" "+ completionType.name() + " "+ outputType);
//                        if (ctr < 101)
//                            continue;

                        try {
                            ImagePixelIterator source = new ImagePixelIterator(p, outputType);
                            BufferedImage copy = BufferedImageIterator.writeToImage(source, null);
                            PixelSourceImageProducerTest.assertImageEquals(bi, copy, false);

                            if (completionType == CompletionType.STALL_MIDWAY_THROUGH || completionType.expectError) {
                                fail("completionType = " + completionType);
                            }

                        } catch(ImagePixelIterator.UnresponsiveImageProducerException e) {
                            if (completionType == CompletionType.STALL_MIDWAY_THROUGH) {
                                // this is a success
                            } else {
                                throw e;
                            }
                        } catch(ImagePixelIterator.ImageProducerException e) {
                            if (completionType.expectError) {
                                // this is a success
                            } else {
                                throw e;
                            }
                        }
                    }
                }
            }
        }
    }
}
