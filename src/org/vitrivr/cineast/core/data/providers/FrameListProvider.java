package org.vitrivr.cineast.core.data.providers;

import java.util.ArrayList;
import java.util.List;

import org.vitrivr.cineast.core.data.frames.VideoFrame;

/**
 * @author rgasser
 * @version 1.0
 * @created 11.01.17
 */
public interface FrameListProvider {
    default List<VideoFrame> getVideoFrames() {
        ArrayList<VideoFrame> list = new ArrayList<>(1);
        list.add(VideoFrame.EMPTY_VIDEO_FRAME);
        return list;
    }
}
