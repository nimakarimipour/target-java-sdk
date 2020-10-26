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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification extends RequestDetails {
    @JsonProperty("id")@Nullable
    private String id;

    @JsonProperty("impressionId")@Nullable
    private String impressionId;

    @JsonProperty("type")@Nullable
    private MetricType type = null;

    @JsonProperty("timestamp")@Nullable
    private Long timestamp;

    @JsonProperty("tokens")
    private List<String> tokens = new ArrayList<>();

    @JsonProperty("mbox")@Nullable
    private NotificationMbox mbox = null;

    @JsonProperty("view")@Nullable
    private NotificationView view = null;

    @JsonProperty("pageLoad")@Nullable
    private NotificationPageLoad pageLoad = null;

    public Notification id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Notification id will be returned in response and will indicate that the notification was processed successfully.
     *
     * @return id
     **/

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Notification impressionId(String impressionId) {
        this.impressionId = impressionId;
        return this;
    }

    /**
     * Impression id is used to stitch (link) the current notification with a previous notification or execute request
     * . In case they both of them match, the second and other subsequent requests will not generate a new impression
     * to the activity, experience etc.
     *
     * @return impressionId
     **/

    @Nullable
    public String getImpressionId() {
        return impressionId;
    }

    public void setImpressionId(String impressionId) {
        this.impressionId = impressionId;
    }

    public Notification type(MetricType type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     **/

    @Nullable
    public MetricType getType() {
        return type;
    }

    public void setType(MetricType type) {
        this.type = type;
    }

    public Notification timestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Timestamp of the notification, in milliseconds elapsed since UNIX epoch.
     *
     * @return timestamp
     **/

    @Nullable
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Notification tokens(List<String> tokens) {
        this.tokens = tokens;
        return this;
    }

    public Notification addTokensItem(String tokensItem) {
        if (this.tokens == null) {
            this.tokens = new ArrayList<>();
        }
        this.tokens.add(tokensItem);
        return this;
    }

    /**
     * A list of tokens for displayed content or clicked selectors, based on the type of notification.
     *
     * @return tokens
     **/

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public Notification mbox(NotificationMbox mbox) {
        this.mbox = mbox;
        return this;
    }

    /**
     * Get mbox
     *
     * @return mbox
     **/

    @Nullable
    public NotificationMbox getMbox() {
        return mbox;
    }

    public void setMbox(NotificationMbox mbox) {
        this.mbox = mbox;
    }

    public Notification view(NotificationView view) {
        this.view = view;
        return this;
    }

    /**
     * Get view
     *
     * @return view
     **/

    @Nullable
    public NotificationView getView() {
        return view;
    }

    public void setView(NotificationView view) {
        this.view = view;
    }

    public Notification pageLoad(NotificationPageLoad pageLoad) {
        this.pageLoad = pageLoad;
        return this;
    }

    /**
     * Get pageLoad
     *
     * @return pageLoad
     **/

    @Nullable
    public NotificationPageLoad getPageLoad() {
        return pageLoad;
    }

    public void setPageLoad(NotificationPageLoad pageLoad) {
        this.pageLoad = pageLoad;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Notification notification = (Notification) o;
        return Objects.equals(this.id, notification.id) &&
                Objects.equals(this.impressionId, notification.impressionId) &&
                Objects.equals(this.type, notification.type) &&
                Objects.equals(this.timestamp, notification.timestamp) &&
                Objects.equals(this.tokens, notification.tokens) &&
                Objects.equals(this.mbox, notification.mbox) &&
                Objects.equals(this.view, notification.view) &&
                Objects.equals(this.pageLoad, notification.pageLoad) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, impressionId, type, timestamp, tokens, mbox, view, pageLoad, super.hashCode());
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Notification {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    impressionId: ").append(toIndentedString(impressionId)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
        sb.append("    tokens: ").append(toIndentedString(tokens)).append("\n");
        sb.append("    mbox: ").append(toIndentedString(mbox)).append("\n");
        sb.append("    view: ").append(toIndentedString(view)).append("\n");
        sb.append("    pageLoad: ").append(toIndentedString(pageLoad)).append("\n");
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

