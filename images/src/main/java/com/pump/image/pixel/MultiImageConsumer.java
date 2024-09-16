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
import java.util.*;

/**
 * This broadcasts the same notifications to several ImageConsumers.
 * <p>
 * If any consumer throws an exception: the exception passes to the
 * {@link java.lang.Thread.UncaughtExceptionHandler}, and that ImageConsumer
 * receives one final {@link ImageConsumer#imageComplete(int)} message
 * with {@link ImageConsumer#IMAGEERROR} as the argument.
 */
class MultiImageConsumer implements ImageConsumer {
    private final List<ImageConsumer> consumers = new LinkedList<>();

    public MultiImageConsumer(ImageConsumer[] consumers) {
        for (ImageConsumer consumer : consumers) {
            if (consumer != null)
                this.consumers.add(consumer);
        }
    }

    @Override
    public synchronized void setDimensions(int width, int height) {
        Iterator<ImageConsumer> iter = consumers.iterator();
        while (iter.hasNext()) {
            ImageConsumer consumer = iter.next();
            try {
                consumer.setDimensions(width, height);
            } catch (Exception e) {
                iter.remove();
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, e);

                try {
                    consumer.imageComplete(ImageConsumer.IMAGEERROR);
                } catch (Exception e2) {
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, e2);
                }
            }
        }
    }

    @Override
    public synchronized void setProperties(Hashtable<?, ?> props) {
        Iterator<ImageConsumer> iter = consumers.iterator();
        while (iter.hasNext()) {
            ImageConsumer consumer = iter.next();
            try {
                consumer.setProperties(props);
            } catch (Exception e) {
                iter.remove();
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, e);

                try {
                    consumer.imageComplete(ImageConsumer.IMAGEERROR);
                } catch (Exception e2) {
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, e2);
                }
            }
        }
    }

    @Override
    public synchronized void setColorModel(ColorModel model) {
        Iterator<ImageConsumer> iter = consumers.iterator();
        while (iter.hasNext()) {
            ImageConsumer consumer = iter.next();
            try {
                consumer.setColorModel(model);
            } catch (Exception e) {
                iter.remove();
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, e);

                try {
                    consumer.imageComplete(ImageConsumer.IMAGEERROR);
                } catch (Exception e2) {
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, e2);
                }
            }
        }
    }

    @Override
    public synchronized void setHints(int hintflags) {
        Iterator<ImageConsumer> iter = consumers.iterator();
        while (iter.hasNext()) {
            ImageConsumer consumer = iter.next();
            try {
                consumer.setHints(hintflags);
            } catch (Exception e) {
                iter.remove();
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, e);

                try {
                    consumer.imageComplete(ImageConsumer.IMAGEERROR);
                } catch (Exception e2) {
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, e2);
                }
            }
        }
    }

    @Override
    public synchronized void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize) {
        Iterator<ImageConsumer> iter = consumers.iterator();
        while (iter.hasNext()) {
            ImageConsumer consumer = iter.next();
            try {
                consumer.setPixels(x, y, w, h, model, pixels, off, scansize);
            } catch (Exception e) {
                iter.remove();
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, e);

                try {
                    consumer.imageComplete(ImageConsumer.IMAGEERROR);
                } catch (Exception e2) {
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, e2);
                }
            }
        }
    }

    @Override
    public synchronized void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
        Iterator<ImageConsumer> iter = consumers.iterator();
        while (iter.hasNext()) {
            ImageConsumer consumer = iter.next();
            try {
                consumer.setPixels(x, y, w, h, model, pixels, off, scansize);
            } catch (Exception e) {
                iter.remove();
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, e);

                try {
                    consumer.imageComplete(ImageConsumer.IMAGEERROR);
                } catch (Exception e2) {
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, e2);
                }
            }
        }
    }

    @Override
    public synchronized void imageComplete(int status) {
        Iterator<ImageConsumer> iter = consumers.iterator();
        while (iter.hasNext()) {
            ImageConsumer consumer = iter.next();
            try {
                consumer.imageComplete(status);
            } catch (Exception e) {
                iter.remove();
                Thread thread = Thread.currentThread();
                thread.getUncaughtExceptionHandler().uncaughtException(thread, e);

                try {
                    consumer.imageComplete(ImageConsumer.IMAGEERROR);
                } catch (Exception e2) {
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, e2);
                }
            }
        }
    }
}