package org.vitrivr.cineast.core.config;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.vitrivr.cineast.core.data.CorrespondenceFunction;

public class QueryConfig extends ReadableQueryConfig {

  public QueryConfig(ReadableQueryConfig qc) {
    super(qc);
  }

  private QueryConfig(ReadableQueryConfig qc, UUID uuid) {
    super(qc, uuid);
  }

  public QueryConfig setDistanceWeights(float[] weights) {
    this.distanceWeights = weights;
    return this;
  }

  public QueryConfig setDistanceIfEmpty(Distance distance) {
    if (this.distance == null) {
      return setDistance(distance);
    }
    return this;
  }

  public QueryConfig setDistanceWeightsIfEmpty(float[] weights) {
    if (this.distanceWeights == null) {
      return setDistanceWeights(weights);
    }
    return this;
  }

  public QueryConfig setNormIfEmty(float norm) {
    if (Float.isNaN(this.norm)) {
      return setNorm(norm);
    }
    return this;
  }

  public QueryConfig setCorrespondenceFunctionIfEmpty(CorrespondenceFunction f) {
    if (this.correspondence == null) {
      return setCorrespondenceFunction(f);
    }
    return this;
  }

  /**
   * Adds the provided hint to the list of hints.
   *
   * @param hint Hint to be added.
   * @return this
   */
  public QueryConfig addHint(Hints hint) {
    this.hints.add(hint);
    return this;
  }

  /**
   * Adds the provided hints and thereby replaces all hints that may have been
   * set previously.
   *
   * @param hint Collection of hints to be added.
   * @return this
   */
  public QueryConfig setHints(Collection<Hints> hint) {
    this.hints.clear();
    this.hints.addAll(hint);
    return this;
  }

  @Override
  public QueryConfig clone() {
    return new QueryConfig(this);
  }

  public QueryConfig cloneWithNewQueryId() {
    return new QueryConfig(this, UUID.randomUUID());
  }

  public static QueryConfig clone(ReadableQueryConfig qc) {
    return new QueryConfig(qc);
  }

  public static QueryConfig notNull(QueryConfig qc) {
    if (qc == null) {
      return new QueryConfig(null);
    }
    return qc;
  }

  /**
   * creates a new {@link QueryConfig} which is identical to the provided one except for the query
   * id
   */
  public static QueryConfig newQueryConfigFromOther(QueryConfig qc) {
    return new QueryConfig(qc, null);
  }

//  protected void setNet(NeuralNet net) {
//    this.net = net;
//  }

  protected QueryConfig setDistance(Distance distance) {
    this.distance = distance;
    if (distance == Distance.euclidean) {
      this.norm = 2f;
    } else if (distance == Distance.manhattan) {
      this.norm = 1f;
    } else if (distance == Distance.chebyshev) {
      this.norm = Float.POSITIVE_INFINITY;
    }
    return this;
  }

  protected QueryConfig setNorm(float norm) {
    this.norm = norm;
    if (Math.abs(norm - 2f) < 1e-6f) {
      this.distance = Distance.euclidean;
    } else if (Math.abs(norm - 1f) < 1e-6f) {
      this.distance = Distance.manhattan;
    } else if (Float.isInfinite(norm) && norm > 0) {
      this.distance = Distance.chebyshev;
    }
    return this;
  }

  protected QueryConfig setCorrespondenceFunction(CorrespondenceFunction f) {
    Objects.requireNonNull(f, "Correspondence function cannot be null");
    this.correspondence = f;
    return this;
  }

//  protected void setClassificationCutoff(float classificationCutoff) {
//    this.classificationCutoff = classificationCutoff;
//  }
}
