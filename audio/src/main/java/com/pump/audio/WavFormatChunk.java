/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.audio;

import java.io.IOException;
import java.io.OutputStream;

public class WavFormatChunk {
	public static final int COMPRESSION_PCM = 0x0001;
	public static final int COMPRESSION_CODE_MS_ADPCM = 0x0002;
	public static final int COMPRESSION_CODE_A_LAW = 0x0006;
	public static final int COMPRESSION_CODE_U_LAW = 0x0007;
	public static final int COMPRESSION_CODE_IMA = 0x0011;
	public static final int COMPRESSION_CODE_ITU_ADPCM = 0x0016;
	public static final int COMPRESSION_CODE_GSM = 0x0031;
	public static final int COMPRESSION_CODE_ITU = 0x0040;
	public static final int COMPRESSION_CODE_MPEG = 0x0050;

	/**
	 * Any value is acceptable, but COMPRESSION_PCM (0x0001) is the most common.
	 */
	public int compressionCode = COMPRESSION_PCM;
	/**
	 * The number of channels specifies how many separate audio signals that are
	 * encoded in the wave data chunk. A value of 1 means a mono signal, a value
	 * of 2 means a stereo signal, etc.
	 */
	public int numChannels = 1;
	/**
	 * The number of sample slices per second. This value is unaffected by the
	 * number of channels.
	 */
	public long sampleRate;
	/**
	 * This value indicates how many bytes of wave data must be streamed to a
	 * D/A converter per second in order to play the wave file. This information
	 * is useful when determining if data can be streamed from the source fast
	 * enough to keep up with playback. This value can be easily calculated with
	 * the formula: AvgBytesPerSec = SampleRate * BlockAlign
	 */
	public long avgBytesPerSecond;
	/**
	 * The number of bytes per sample slice. This value is not affected by the
	 * number of channels and can be calculated with the formula: BlockAlign =
	 * SignificantBitsPerSample / 8 * NumChannels
	 */
	public int blockAlign;
	/**
	 * This value specifies the number of bits used to define each sample. This
	 * value is usually 8, 16, 24 or 32. If the number of bits is not byte
	 * aligned (a multiple of 8) then the number of bytes used per sample is
	 * rounded up to the nearest byte size and the unused bytes are set to 0 and
	 * ignored.
	 */
	public int sigBitsPerSample;

	/**
	 * 
	 * @param compressionCode
	 *            Any value is acceptable, but COMPRESSION_PCM (0x0001) is the
	 *            most common.
	 * @param numChannels
	 *            The number of channels specifies how many separate audio
	 *            signals that are encoded in the wave data chunk. A value of 1
	 *            means a mono signal, a value of 2 means a stereo signal, etc.
	 * @param sampleRate
	 *            The number of sample slices per second. This value is
	 *            unaffected by the number of channels.
	 * @param sigBitsPerSample
	 *            This value specifies the number of bits used to define each
	 *            sample. This value is unaffected by the number of channels.
	 *            This value is usually 8, 16, 24 or 32. If the number of bits
	 *            is not byte aligned (a multiple of 8) then the number of bytes
	 *            used per sample is rounded up to the nearest byte size and the
	 *            unused bytes are set to 0 and ignored.
	 */
	public WavFormatChunk(int compressionCode, int numChannels,
			long sampleRate, int sigBitsPerSample) {
		this.compressionCode = compressionCode;
		this.numChannels = numChannels;
		this.sampleRate = sampleRate;
		this.sigBitsPerSample = sigBitsPerSample;

		blockAlign = sigBitsPerSample / 8 * numChannels;
		avgBytesPerSecond = sampleRate * blockAlign;
	}

	/**
	 * 
	 * @param compressionCode
	 *            Any value is acceptable, but COMPRESSION_PCM (0x0001) is the
	 *            most common.
	 * @param numChannels
	 *            The number of channels specifies how many separate audio
	 *            signals that are encoded in the wave data chunk. A value of 1
	 *            means a mono signal, a value of 2 means a stereo signal, etc.
	 * @param sampleRate
	 *            The number of sample slices per second. This value is
	 *            unaffected by the number of channels.
	 * @param avgBytesPerSecond
	 *            This value indicates how many bytes of wave data must be
	 *            streamed to a D/A converter per second in order to play the
	 *            wave file. This information is useful when determining if data
	 *            can be streamed from the source fast enough to keep up with
	 *            playback. This value can be easily calculated with the
	 *            formula: AvgBytesPerSec = SampleRate * BlockAlign
	 * @param blockAlign
	 *            The number of bytes per sample slice. This value is not
	 *            affected by the number of channels and can be calculated with
	 *            the formula: BlockAlign = SignificantBitsPerSample / 8 *
	 *            NumChannels
	 * @param sigBitsPerSample
	 *            This value specifies the number of bits used to define each
	 *            sample. This value is usually 8, 16, 24 or 32. If the number
	 *            of bits is not byte aligned (a multiple of 8) then the number
	 *            of bytes used per sample is rounded up to the nearest byte
	 *            size and the unused bytes are set to 0 and ignored.
	 */
	public WavFormatChunk(int compressionCode, int numChannels,
			long sampleRate, long avgBytesPerSecond, int blockAlign,
			int sigBitsPerSample) {
		this.compressionCode = compressionCode;
		this.numChannels = numChannels;
		this.sampleRate = sampleRate;
		this.avgBytesPerSecond = avgBytesPerSecond;
		this.blockAlign = blockAlign;
		this.sigBitsPerSample = sigBitsPerSample;
	}

	public WavFormatChunk(byte[] data) throws IOException {
		compressionCode = WavReader.readInt(data, 0, 2);
		numChannels = WavReader.readInt(data, 2, 2);
		sampleRate = WavReader.readLong(data, 4, 4);
		avgBytesPerSecond = WavReader.readLong(data, 8, 4);
		blockAlign = WavReader.readInt(data, 12, 2);
		sigBitsPerSample = WavReader.readInt(data, 14, 2);
	}

	/**
	 * 
	 * @param out
	 *            the stream to write to
	 * @param includeHeader
	 *            whether to include the chunk ID and chunk size or not.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public void write(OutputStream out, boolean includeHeader)
			throws IOException {
		byte[] array = new byte[4];
		if (includeHeader) {
			out.write(new byte[] { 0x66, 0x6D, 0x74, 0x20 });
			WavFileWriter.writeLong(array, 16, 4);
			out.write(array, 0, 4);
		}
		WavFileWriter.writeInt(array, compressionCode, 2);
		out.write(array, 0, 2);
		WavFileWriter.writeInt(array, numChannels, 2);
		out.write(array, 0, 2);
		WavFileWriter.writeLong(array, sampleRate, 4);
		out.write(array, 0, 4);
		WavFileWriter.writeLong(array, avgBytesPerSecond, 4);
		out.write(array, 0, 4);
		WavFileWriter.writeInt(array, blockAlign, 2);
		out.write(array, 0, 2);
		WavFileWriter.writeInt(array, sigBitsPerSample, 2);
		out.write(array, 0, 2);

	}

	@Override
	public String toString() {
		return "WavFormatChunk[ compressionCode = " + compressionCode
				+ " numChannels = " + numChannels + " sampleRate = "
				+ sampleRate + " avgBytesPerSecond = " + avgBytesPerSecond
				+ " blockAlign = " + blockAlign + " sigBitsPerSample = "
				+ sigBitsPerSample + " )";
	}
}