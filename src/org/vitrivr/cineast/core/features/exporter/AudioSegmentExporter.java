package org.vitrivr.cineast.core.features.exporter;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
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
import org.vitrivr.cineast.core.util.WavExporter;

/**
 * Exports the audio in a given segment as mono WAV file.
 *
 * @author rgasser
 * @version 1.0
 * @created 31.01.17
 */
public class AudioSegmentExporter implements Extractor {


    private static final Logger LOGGER = LogManager.getLogger();

    private static final String PROPERTY_NAME_DESTINATION = "destination";

    /** Destination path for the audio-segment. */
    private Path destination;

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
    public AudioSegmentExporter(HashMap<String, String> properties) {
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

            /* Extract mean samples and perpare byte buffer. */
            short[] data = shot.getMeanSamplesAsShort();
            ByteBuffer buffer = ByteBuffer.allocate(44 + data.length*2).order(ByteOrder.LITTLE_ENDIAN);

            /* Write header of WAV file. */
            short channels = 1;

            WavExporter myWav = new WavExporter();

            myWav.writeWaveHeader(buffer, shot.getSamplingrate(), channels, data.length * 2);

            /* Write actual data. */
            for (short sample : data) {
                buffer.putShort(sample);
            }

            stream.write(buffer.array());
            stream.close();
        } catch (IOException | BufferOverflowException e) {
            LOGGER.fatal("Could not export audio segment {} due to a serious IO error ({}).", shot.getId(), LogHelper.getStackTrace(e));
        }
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
