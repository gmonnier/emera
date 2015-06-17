package com.maxmind.geoip2.record;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * City-level data associated with an IP address.
 * </p>
 * <p>
 * This record is returned by all the end points except the Country end point.
 * </p>
 */
final public class City extends AbstractNamedRecord {
    @JsonProperty
    private Integer confidence;

    public City() {
        super();
    }

    /**
     * @return A value from 0-100 indicating MaxMind's confidence that the city
     * is correct. This attribute is only available from the Insights
     * end point.
     */
    public Integer getConfidence() {
        return this.confidence;
    }

}
