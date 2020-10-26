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
 */
package com.adobe.target.edge.client.model;

import javax.annotation.Nullable;

import com.adobe.experiencecloud.ecid.visitor.Visitor;
import com.adobe.target.delivery.v1.model.DeliveryRequest;

public class TargetDeliveryRequest {

    @Nullable
    private String sessionId;

    @Nullable
    private String locationHint;

    @Nullable
    private Visitor visitor;
    private DeliveryRequest deliveryRequest = new DeliveryRequest();

    private TargetDeliveryRequest() {}

    public static TargetDeliveryRequest fromRequest(DeliveryRequest deliveryRequest) {
        TargetDeliveryRequest targetDeliveryRequest = new TargetDeliveryRequest();
        targetDeliveryRequest.deliveryRequest = deliveryRequest;
        return targetDeliveryRequest;
    }

    TargetDeliveryRequest setVisitor(@Nullable Visitor visitor) {
        this.visitor = visitor;
        return this;
    }

    TargetDeliveryRequest setSessionId(@Nullable String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    TargetDeliveryRequest setLocationHint(@Nullable String locationHint) {
        this.locationHint = locationHint;
        return this;
    }

    @Nullable
    public String getSessionId() {
        return sessionId;
    }

    public DeliveryRequest getDeliveryRequest() {
        return deliveryRequest;
    }

    @Nullable
    public Visitor getVisitor() {
        return visitor;
    }

    @Nullable
    public String getLocationHint() {
        return locationHint;
    }

    public static TargetDeliveryRequestBuilder builder() {
        return new TargetDeliveryRequestBuilder();
    }

    @Override
    public String toString() {
        return "TargetDeliveryRequest{" +
                "sessionId='" + sessionId + '\'' +
                ", locationHint='" + locationHint + '\'' +
                ", visitor=" + visitor +
                ", deliveryRequest=" + deliveryRequest +
                '}';
    }
}
