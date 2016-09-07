package org.vitrivr.cineast.art.modules;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;
import org.vitrivr.cineast.api.WebUtils;
import org.vitrivr.cineast.art.modules.abstracts.AbstractVisualizationModule;
import org.vitrivr.cineast.art.modules.visualization.SegmentDescriptorComparator;
import org.vitrivr.cineast.art.modules.visualization.VisualizationResult;
import org.vitrivr.cineast.art.modules.visualization.VisualizationType;
import org.vitrivr.cineast.core.db.DBSelector;
import org.vitrivr.cineast.core.db.ShotLookup;
import org.vitrivr.cineast.core.util.ArtUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sein on 30.08.16.
 */
public class VisualizationMedianColorGradient extends AbstractVisualizationModule {
  public VisualizationMedianColorGradient() {
    super();
    tableNames.put("MedianColor", "features_MedianColor");
  }

  @Override
  public String getDisplayName() {
    return "VisualizationMedianColorGradient";
  }

  @Override
  public String visualizeMultimediaobject(String multimediaobjectId) {
    DBSelector selector = selectors.get("MedianColor");
    ShotLookup segmentLookup = new ShotLookup();
    List<ShotLookup.ShotDescriptor> segments = segmentLookup.lookUpVideo(multimediaobjectId);
    Collections.sort(segments, new SegmentDescriptorComparator());

    BufferedImage image = new BufferedImage(segments.size(), 1, BufferedImage.TYPE_INT_RGB);
    int count = 0;
    for (ShotLookup.ShotDescriptor segment : segments) {
      int[][] avg = ArtUtil.shotToInt(segment.getShotId(), selector, 1, 1);
      image.setRGB(count, 0, avg[0][0]);
      count++;
    }

    try {
      image = Thumbnails.of(image).scalingMode(ScalingMode.BILINEAR).scale(10, 100).asBufferedImage();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return WebUtils.BufferedImageToDataURL(image, "png");
  }

  @Override
  public List<VisualizationType> getVisualizations() {
    List<VisualizationType> types = new ArrayList();
    types.add(VisualizationType.VISUALIZATION_MULTIMEDIAOBJECT);
    return types;
  }

  @Override
  public VisualizationResult getResultType() {
    return VisualizationResult.IMAGE;
  }
}
