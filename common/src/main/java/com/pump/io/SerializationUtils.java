package com.pump.io;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Static helper methods related to serialization.
 */
public class SerializationUtils {

    /**
     * Deserialize a serialized, compressed object at the file path provided.
     * <p>
     * See {@link #serialize(Serializable, File)}
     *
     * @param file
     *            the file to read.
     * @return the deserialized data.
     */
    public static Serializable deserialize(File file)
            throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(file)) {
            return deserialize(fileIn, true);
        }
    }

    /**
     * Deserialize a serialized object from a given InputStream.
     * <p>
     * This can read data created via {@link #serialize(Serializable, OutputStream, boolean, boolean)}.
     *
     * @param in the InputStream to read a serialized object from.
     * @param useGZIPdecompression if true then the InputStream needs to be passed through
     *                             an additional GZIPInputStream.
     * @return one Serializable object from the InputStream.
     */
    public static Serializable deserialize(InputStream in, boolean useGZIPdecompression) throws IOException, ClassNotFoundException {
        if (useGZIPdecompression) {
            try (GZIPInputStream zipIn = new GZIPInputStream(in)) {
                try (ObjectInputStream objIn = new ObjectInputStream(zipIn)) {
                    return (Serializable) objIn.readObject();
                }
            }
        } else {
            try (ObjectInputStream objIn = new ObjectInputStream(in)) {
                return (Serializable) objIn.readObject();
            }
        }
    }

    /**
     * Serialize and compress an object to the file path provided.
     * <p>
     * See {@link #deserialize(File)}.
     *
     * @param object
     *            the object to serialize.
     * @param dest
     *            the file to write.
     */
    public static void serialize(Serializable object, File dest)
            throws IOException {
        if (!dest.exists() && !dest.createNewFile()) {
            throw new IOException("File.createNewFile failed for " + dest.getPath());
        }

        try (FileOutputStream fileOut = new FileOutputStream(dest)) {
            serialize(object, fileOut, true, true);
        }
    }

    /**
     * Serialize and compress the output byte array using GZIP compression.
     * <p>
     * This can be deserialized via {@link #deserialize(byte[])}.
     *
     * @param serializable
     *            the object to serialize.
     * @return
     * 			  the byte array of the serialized data.
     */
    public static byte[] serialize(Serializable serializable) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            serialize(serializable, byteOut, true, true);
            return byteOut.toByteArray();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserialize a compressed byte array created via {@link #serialize(Serializable)}.
     *
     * @param bytes an array of bytes that contains a compressed Serializable object.
     * @return the deserialized object.
     */
    public static Serializable deserialize(byte[] bytes) {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes)) {
            return deserialize(byteIn, true);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize an object to the OutputStream provided.
     * <p>
     * The {@link #deserialize(InputStream, boolean)} can read what this writes.
     *
     * @param object the object to serialize
     * @param outputStream the stream to write to
     * @param useGZIPcompression if true then the OutputStream passes through an additional GZIPOutputStream
     * @param closeOutput if true then `outputStream` is closed when this method completes, if false then
     *                    it is not closed.
     */
    public static void serialize(Serializable object, OutputStream outputStream, boolean useGZIPcompression, boolean closeOutput) throws IOException {
        try (MeasuredOutputStream outputStreamWrapper = new MeasuredOutputStream(outputStream)) {
            if (!closeOutput)
               outputStreamWrapper.setCloseable(false);
            if (useGZIPcompression) {
                try (GZIPOutputStream zipOut = new GZIPOutputStream(outputStreamWrapper)) {
                    try (ObjectOutputStream objOut = new ObjectOutputStream(zipOut)) {
                        objOut.writeObject(object);
                    }
                }
            } else {
                try (ObjectOutputStream objOut = new ObjectOutputStream(
                        outputStreamWrapper)) {
                    objOut.writeObject(object);
                }
            }
        }
    }
}
