package org.vitrivr.cineast.core.features.exporter;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.config.Config;
import org.vitrivr.cineast.core.data.frames.AudioFrame;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.db.PersistencyWriterSupplier;
import org.vitrivr.cineast.core.features.extractor.Extractor;
import org.vitrivr.cineast.core.setup.EntityCreator;
import org.vitrivr.cineast.core.util.LogHelper;
import java.io.File;
import java.io.FileWriter;


/**
 * Exports deepspeech translation
 *
 * @author huirong
 * @version 1.0
 * @created 06.06.2018
 */

public class ASRImplementation implements Extractor {


    private static final Logger LOGGER = LogManager.getLogger();

    private static final String PROPERTY_NAME_DESTINATION = "destination";

    /** Destination path for the audio-segment. */
    private Path destination;

    private String pathToDeepspeechModels = "/Users/Raphael/Desktop/DeepSpeech/models/";

    /**
     * Default constructor. The AudioSegmentExport can be configured via named properties
     * in the provided HashMap. Supported parameters:
     *
     * <ol>
     *      <li>destination: Path where files should be stored.</li>
     * </ol>
     *
     * @param properties HashMap containing named properties
     */
    public ASRImplementation(HashMap<String, String> properties) {
        if (properties.containsKey(PROPERTY_NAME_DESTINATION)) {
            this.destination = Paths.get(properties.get(PROPERTY_NAME_DESTINATION));
        } else {
            this.destination = Paths.get(Config.sharedConfig().getExtractor().getOutputLocation().toString());
        }
    }

    /**
     * Processes a SegmentContainer: Extract audio-data and writes to a WAVE file.
     *
     * @param shot SegmentContainer to process.
     */

    @Override
    public void processSegment(SegmentContainer shot) {
        try {
            /* Prepare folder and OutputStream. */
            Path directory = this.destination.resolve(shot.getSuperId());
            Files.createDirectories(directory);
            OutputStream stream = Files.newOutputStream(directory.resolve(shot.getId()+".wav"), CREATE, TRUNCATE_EXISTING);




            /* Extract mean samples and prepare byte buffer. */
            short[] data = shot.getMeanSamplesAsShort();
            ByteBuffer buffer = ByteBuffer.allocate(44 + data.length*2).order(ByteOrder.LITTLE_ENDIAN);

            /* Write header of WAV file. */
            this.writeWaveHeader(buffer, shot.getSamplingrate(), (short) 1, data.length); // ralph miscalculated this, should be data.length, not data.length * 2

            /* Write actual data. */
            for (short sample : data) {
                buffer.putShort(sample);


            }

            stream.write(buffer.array());
            stream.close();


            String fileNameWav = shot.getId()+ ".wav";

            String fileNameText = shot.getId() + ".txt";

            String fileFolder = "/Users/Raphael/TestProgram/" + shot.getSuperId() + "/";
            //in mac oxs

            String command = "deepspeech " + pathToDeepspeechModels + "output_graph.pbmm " +
                    fileFolder + fileNameWav + " " +
                    pathToDeepspeechModels + "alphabet.txt " +
                    pathToDeepspeechModels + "lm.binary " +
                    pathToDeepspeechModels + "trie ";


            String output = executeCommand(command);
            System.out.println(command + "\n");
            System.out.println(output);

            try {
                File file = new File(fileFolder + shot.getId() + ".txt");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(output);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



        } catch (IOException | BufferOverflowException e) {
            LOGGER.fatal("Could not export audio segment {} due to a serious IO error ({}).", shot.getId(), LogHelper.getStackTrace(e));
        }
    }

    /**
     * Writes the WAV header to the ByteBuffer (1 channel).
     *
     * @param buffer
     * @param samplingrate Samplingrate of the output file.
     * @param length Length in bytes of the frames data
     */

    private void writeWaveHeader(ByteBuffer buffer, float samplingrate, short channels, int length) {

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
    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }


    @Override
    public void init(PersistencyWriterSupplier phandlerSupply) {

    }

    @Override
    public void finish() {

    }

    @Override
    public void initalizePersistentLayer(Supplier<EntityCreator> supply) {

    }

    @Override
    public void dropPersistentLayer(Supplier<EntityCreator> supply) {

    }
}
