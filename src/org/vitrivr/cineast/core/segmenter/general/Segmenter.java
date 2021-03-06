package org.vitrivr.cineast.core.segmenter.general;

import org.vitrivr.cineast.core.data.entities.MultimediaObjectDescriptor;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.decode.general.Decoder;

/**
 * Segmenter's split a media-file into chunks (segments). The nature of that chunk is specific to
 * the media-type and the segmenter's implementation. It could be a frame or shot from a video,
 * a sequence from a song or a single image.
 *
 * @author rgasser
 * @version 1.0
 * @created 16.01.17
 */
public interface Segmenter<A> extends Runnable, AutoCloseable {
    /**
     * Method used to initialize the Segmenter. A class implementing the Decoder interface with
     * the same type must be provided.
     *
     * @param decoder Decoder used for media-decoding.
     * @param object Media object that is about to be segmented.
     */
    void init(Decoder<A> decoder, MultimediaObjectDescriptor object);

    /**
     * Returns the next SegmentContainer from the source OR null if there are no more segments in the queue. As
     * generation of SegmentContainers can take some time (depending on the media-type), a null return does not
     * necessarily mean that the Segmenter is done segmenting. Use the complete() method to check this.
     *
     * <strong>Important:</strong> This method should be designed to block and wait for an appropriate amount of time if the
     * Segmenter is not yet ready to deliver another segment! It's up to the Segmenter how long that timeout should last.
     *
     * @return
     */
    SegmentContainer getNext() throws InterruptedException;

    /**
     * Indicates whether the Segmenter is complete i.e. no new segments are to be expected.
     *
     * @return true if work is complete, false otherwise.
     */
    boolean complete();

    /**
     * Closes the Segmenter. This method should cleanup and relinquish all resources. Especially,
     * calling this method should also close the Decoder associated with this Segmenter instance.
     *
     * <strong>Note:</strong> It is unsafe to re-use a Segmenter after it has been closed.
     */
    @Override
    void close();
}
