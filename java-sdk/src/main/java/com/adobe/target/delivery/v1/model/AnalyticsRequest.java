/*
 * Copyright 2019 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * NOTE: This is an auto generated file. Do not edit directly.
 */
package com.adobe.target.delivery.v1.model;

import javax.annotation.Nullable;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticsRequest {
    @JsonProperty("supplementalDataId")@Nullable
    private String supplementalDataId;

    @JsonProperty("logging")@Nullable
    private LoggingType logging = null;

    @JsonProperty("trackingServer")@Nullable
    private String trackingServer;

    @JsonProperty("trackingServerSecure")@Nullable
    private String trackingServerSecure;

    public AnalyticsRequest supplementalDataId(String supplementalDataId) {
        this.supplementalDataId = supplementalDataId;
        return this;
    }

    /**
     * Supplemental data id, used for **server side** integrations. Format &lt;16 hexadecimal digits&gt;-&lt;16
     * hexadecimal digits&gt;
     *
     * @return supplementalDataId
     **/

    @Nullable
    public String getSupplementalDataId() {
        return supplementalDataId;
    }

    public void setSupplementalDataId(String supplementalDataId) {
        this.supplementalDataId = supplementalDataId;
    }

    public AnalyticsRequest logging(LoggingType logging) {
        this.logging = logging;
        return this;
    }

    /**
     * Get logging
     *
     * @return logging
     **/

    @Nullable
    public LoggingType getLogging() {
        return logging;
    }

    public void setLogging(LoggingType logging) {
        this.logging = logging;
    }

    public AnalyticsRequest trackingServer(@Nullable String trackingServer) {
        this.trackingServer = trackingServer;
        return this;
    }

    /**
     * Get trackingServer
     *
     * @return trackingServer
     **/

    @Nullable
    public String getTrackingServer() {
        return trackingServer;
    }

    public void setTrackingServer(String trackingServer) {
        this.trackingServer = trackingServer;
    }

    public AnalyticsRequest trackingServerSecure(@Nullable String trackingServerSecure) {
        this.trackingServerSecure = trackingServerSecure;
        return this;
    }

    /**
     * Get trackingServerSecure
     *
     * @return trackingServerSecure
     **/

    @Nullable
    public String getTrackingServerSecure() {
        return trackingServerSecure;
    }

    public void setTrackingServerSecure(String trackingServerSecure) {
        this.trackingServerSecure = trackingServerSecure;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalyticsRequest analyticsRequest = (AnalyticsRequest) o;
        return Objects.equals(this.supplementalDataId, analyticsRequest.supplementalDataId) &&
                Objects.equals(this.logging, analyticsRequest.logging) &&
                Objects.equals(this.trackingServer, analyticsRequest.trackingServer) &&
                Objects.equals(this.trackingServerSecure, analyticsRequest.trackingServerSecure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplementalDataId, logging, trackingServer, trackingServerSecure);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AnalyticsRequest {\n");
        sb.append("    supplementalDataId: ").append(toIndentedString(supplementalDataId)).append("\n");
        sb.append("    logging: ").append(toIndentedString(logging)).append("\n");
        sb.append("    trackingServer: ").append(toIndentedString(trackingServer)).append("\n");
        sb.append("    trackingServerSecure: ").append(toIndentedString(trackingServerSecure)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(@Nullable Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

