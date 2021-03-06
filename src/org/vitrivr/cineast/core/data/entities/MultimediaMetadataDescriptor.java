package org.vitrivr.cineast.core.data.entities;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.vitrivr.cineast.core.data.ExistenceCheck;
import org.vitrivr.cineast.core.data.providers.primitive.NothingProvider;
import org.vitrivr.cineast.core.data.providers.primitive.PrimitiveTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.ProviderDataType;
import org.vitrivr.cineast.core.data.providers.primitive.StringTypeProvider;
import org.vitrivr.cineast.core.db.dao.reader.DatabaseLookupException;
import org.vitrivr.cineast.core.db.dao.reader.MultimediaMetadataReader;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;


/**
 * @author rgasser
 * @version 1.0
 * @created 10.01.17
 */
public class MultimediaMetadataDescriptor implements ExistenceCheck {
  /** Name of the entity in the persistence layer. */
  public static final String ENTITY = "cineast_metadata";

  /** Field names in the persistence layer. */
  public static final String[] FIELDNAMES = {"objectid", "domain", "key", "value"};

  /** List of supported types. */
  private static final List<Class<?>> SUPPORTED_TYPES = ImmutableList.of(
      Integer.class, Long.class, Float.class, Double.class, String.class);

  /** ID of the MultimediaObject this MultimediaMetadataDescriptor belongs to. */
  private final String objectId;

  /** String value that identifies the metadata domain (e.g. EXIF, IPTC, DC...) */
  private final String domain;

  /** Key (name) of the metadata entry. Must NOT be unique for a given object. */
  private final String key;

  /** Value of the MetadataDescriptor. */
  private final PrimitiveTypeProvider value;

  /**
   * Flag that indicates whether or not the MultimediaMetadataDescriptor has been stored
   * persistently in the underlying.
   */
  private final boolean exists;

  /**
   * Convenience method to create a MultimediaMetadataDescriptor marked as new. The method will
   * assign a new ID to this MultimediaObjectDescriptor.
   *
   * @param objectId ID of the MultimediaObject this MultimediaMetadataDescriptor belongs to.
   * @param domain domain that the metadata entry belongs to
   * @param key Key (name) of the metadata entry.
   * @param value Value of the metadata entry. Can be any type of object, but only Double, Float,
   *              Int, Long and String are supported officially.
   * @return A new MultimediaMetadataDescriptor
   */
  public static MultimediaMetadataDescriptor of(String objectId, String domain, String key,
      @Nullable Object value) {
    return new MultimediaMetadataDescriptor(objectId, domain, key, value, false);
  }

  /**
   * Constructor for MultimediaMetadataDescriptor. Tries to infer the type of the provided value by
   * means of instance of. If the value is not compatible with the default primitive types, the
   * object's toString() method is used to get a String representation.
   *
   * @param objectId ID of the MultimediaObject this MultimediaMetadataDescriptor belongs to.
   * @param domain domain that the metadata entry belongs to
   * @param key Key (name) of the metadata entry.
   * @param value Value of the metadata entry. Can be any type of object, but only Double, Float,
   *              Int, Long and String are supported officially.
   */
  public MultimediaMetadataDescriptor(String objectId, String domain, String key,
      @Nullable Object value, boolean exists) {
    this.objectId = objectId;
    this.key = key;
    this.domain = domain;
    this.exists = exists;

    if (isSupportedValue(value)) {
      this.value = PrimitiveTypeProvider.fromObject(value);
    } else if (value != null) {
      this.value = new StringTypeProvider(value.toString());
    } else {
      this.value = new NothingProvider();
    }
  }

  private static boolean isSupportedValue(Object value) {
    return SUPPORTED_TYPES.stream().anyMatch(clazz -> clazz.isInstance(value));
  }

  /**
   * Constructor for MultimediaMetadataDescriptor which can be used to create a
   * MultimediaMetadataDescriptor from a Map containing the fieldnames as keys and the
   * PrimitiveTypeProviders as value. Maps like this are usually returned by DB lookup classes.
   *
   * @param data Map that maps the fieldnames to PrimitiveTypeProvider's.
   * @throws DatabaseLookupException If a required field could not be mapped.
   * @see PrimitiveTypeProvider
   * @see MultimediaMetadataReader
   */
  public MultimediaMetadataDescriptor(Map<String, PrimitiveTypeProvider> data)
      throws DatabaseLookupException {
    if (data.get(FIELDNAMES[0]) != null
        && data.get(FIELDNAMES[0]).getType() == ProviderDataType.STRING) {
      this.objectId = data.get(FIELDNAMES[0]).getString();
    } else {
      throw new DatabaseLookupException(
          "Could not read column '" + FIELDNAMES[0] + "' for MultimediaObjectDescriptor.");
    }

    if (data.get(FIELDNAMES[1]) != null
        && data.get(FIELDNAMES[1]).getType() == ProviderDataType.STRING) {
      this.domain = data.get(FIELDNAMES[1]).getString();
    } else {
      throw new DatabaseLookupException(
          "Could not read column '" + FIELDNAMES[1] + "' for MultimediaObjectDescriptor.");
    }

    if (data.get(FIELDNAMES[2]) != null
        && data.get(FIELDNAMES[2]).getType() == ProviderDataType.STRING) {
      this.key = data.get(FIELDNAMES[2]).getString();
    } else {
      throw new DatabaseLookupException(
          "Could not read column '" + FIELDNAMES[2] + "' for MultimediaObjectDescriptor.");
    }

    this.value = data.get(FIELDNAMES[3]);
    this.exists = true;
  }

  @JsonProperty
  public String getObjectId() {
    return objectId;
  }

  @JsonProperty
  public String getDomain() {
    return domain;
  }

  @JsonProperty
  public String getKey() {
    return key;
  }

  @JsonProperty
  public Object getValue() {
    return PrimitiveTypeProvider.getObject(this.value);
  }

  @Override
  public boolean exists() {
    return this.exists;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("objectId", objectId)
        .add("domain", domain)
        .add("key", key)
        .add("value", value)
        .toString();
  }
}

