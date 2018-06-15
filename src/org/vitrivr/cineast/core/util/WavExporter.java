package org.vitrivr.cineast.core.util;

import org.vitrivr.cineast.core.data.frames.AudioFrame;

import java.nio.ByteBuffer;

public class WavExporter {
    /**
     * Writes the WAV header to the ByteBuffer (1 channel).
     *
     * @param buffer
     * @param samplingrate Samplingrate of the output file.
     * @param length Length in bytes of the frames data
     */

    public static double writeWaveHeader(ByteBuffer buffer, float samplingrate, short channels, int length) {

        /* Length of the subChunk2. */
        final int subChunk2Length = length * channels * (AudioFrame.BITS_PER_SAMPLE/8); /* Number of bytes for audio data: NumSamples * NumChannels * BitsPerSample/8. */


        /* RIFF Chunk. */
        buffer.put("RIFF".getBytes());
        buffer.putInt(36 + subChunk2Length);
        buffer.put("WAVE".getBytes()); /* WAV format. */

        /* Format chunk. */
        buffer.put("fmt ".getBytes()); /* Begin of the format chunk. */
        buffer.putInt(16); /* Length of the Format chunk. */
        buffer.putShort((short)1); /* Format: 1 = Raw PCM (linear quantization). */
        buffer.putShort((short)1); /* Number of channels. */
        buffer.putInt((int)samplingrate); /* Samplingrate. */
        buffer.putInt((int)(samplingrate * channels * (AudioFrame.BITS_PER_SAMPLE/8))); /* Byte rate: SampleRate * NumChannels * BitsPerSample/8 */
        buffer.putShort((short)(channels * (AudioFrame.BITS_PER_SAMPLE/8))); /* Block align: NumChannels * BitsPerSample/8. */
        buffer.putShort((short)(AudioFrame.BITS_PER_SAMPLE)) /* Bits per sample. */;

        /* Data chunk */
        buffer.put("data".getBytes()); /* Begin of the data chunk. */
        /* Length of the data chunk. */
        buffer.putInt(subChunk2Length);

        // calculate duration of the clip
        // file.length() / format.getSampleRate() / (format.getSampleSizeInBits() / 8.0) / format.getChannels();


        double durationInSeconds = subChunk2Length / channels / (AudioFrame.BITS_PER_SAMPLE / 8.0) / samplingrate ;

        return durationInSeconds;
    }
}