package org.vitrivr.cineast.core.run;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.vitrivr.cineast.core.config.IdConfig;
import org.vitrivr.cineast.core.data.MediaType;
import org.vitrivr.cineast.core.db.DBSelectorSupplier;
import org.vitrivr.cineast.core.db.PersistencyWriterSupplier;
import org.vitrivr.cineast.core.features.extractor.Extractor;
import org.vitrivr.cineast.core.idgenerator.ObjectIdGenerator;
import org.vitrivr.cineast.core.metadata.MetadataExtractor;

/**
 * Provides a configuration context for an extraction run.
 *
 * @author rgasser
 * @version 1.0
 * @created 13.01.17
 */
public interface ExtractionContextProvider {
    /**
     * Determines the MediaType of the source material. Only one media-type
     * can be specified per ExtractionContextProvider.
     *
     * @return Media-type of the source material.
     */
    MediaType sourceType();

    /**
     * Determines the path of the input-file or folder.
     *
     * @return Path to the input-file or folder.
     */
    Path inputPath();

    /**
     * Limits the number of files that should be extracted. This a predicate is applied
     * before extraction starts. If extraction fails for some fails the effective number
     * of extracted files may be lower.
     *
     * @return A number greater than zero.
     */
    @Deprecated
    int limit();

    /**
     * Offset into the list of files that are being distracted.
     *
     * @return A positive number or zero
     */
    @Deprecated
    int skip();

    /**
     *  Limits the depth of recursion when extraction folders of files. Has no
     *  effect if the inputPath points to a file.
     *
     * @return A number greater than zero.
     */
    int depth();

    /**
     * Returns a list of extractor classes that should be used for
     * the extraction run!
     *
     * @return List of named extractors.
     */
    public List<Extractor> extractors();

    /**
     * Returns a list of exporter classes that should be invoked during extraction. Exporters
     * usually generate some representation and persistently store that information somewhere.
     *
     * @return List of named exporters.
     */
    public List<Extractor> exporters();

    /**
     * Returns a list of metadata extractor classes that should be invoked during extraction. MetadataExtractor's
     * usually read some metadata from a file.
     *
     * @return List of named exporters.
     */
    public List<MetadataExtractor> metadataExtractors();

    /**
     * Returns an instance of ObjectIdGenerator that should be used to generated MultimediaObject ID's
     * during an extraction run.
     *
     * @return ObjectIdGenerator
     */
    ObjectIdGenerator objectIdGenerator();

    /**
     * Returns the ExistenceCheck mode that should be applied during the extraction run. Can either be:
     *
     * NOCHECK          - No checks performed (may cause DB error if collision occurs).
     * CHECK_SKIP       - Check existence and skip object on collision.
     * CHECK_PROCEED    - Check existence and proceed with object on collision.
     *
     * @return ExistenceCheck mode for current run.
     */
    IdConfig.ExistenceCheck existenceCheck();

    /**
     * Returns the PersistencyWriterSupplier that can be used during the extraction run to
     * obtain PersistencyWriter instance.
     *
     * @return PersistencyWriterSupplier instance used obtain a PersistencyWriter.
     */
    PersistencyWriterSupplier persistencyWriter();

    /**
     * Returns the DBSelectorSupplier that can be used during the extraction run to obtain
     * a DBSelector instance.
     *
     * @return DBSelectorSupplier instance used obtain a DBSelector.
     */
    DBSelectorSupplier persistencyReader();

    /**
     * Returns the default output-location for files generated during extraction
     * (e.g. thumbnails, PROTO files etc.). Unless explicitly stated otherwise
     * in the configuration of one of the exporters, this path will be used.
     *
     * @return Output location for files generated during extraction.
     */
    File outputLocation();

    /**
     * Returns the number of threads to be used to power the extraction pipeline.
     *
     * @return Number of threads. Must be > 0.
     */
    int threadPoolSize();

    /**
     * Returns the size of the task queue. That queue is used to store extraction
     * tasks right before they are being processed.
     *
     * @return Size of the task queue. Must be > 0.
     */
    Integer taskQueueSize();

    /**
     * Returns the size of the segment queue. That queue is used to store segments when they are
     * handed to the extraction pipeline but the pipeline is currently fully occupied.
     *
     * @return Size of the segment queue. Must be > 0.
     */
    Integer segmentQueueSize();


    /**
     * Returns the size of a batch. A batch is used when persisting data. Entities will be kept in
     * memory until the batchsize limit is hit at which point they will be persisted.
     *
     * @return Batch size.
     */
    Integer getBatchsize();
}
