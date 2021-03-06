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
package com.adobe.target.edge.client.entities;

import com.adobe.experiencecloud.ecid.visitor.CustomerState;
import com.adobe.target.edge.client.model.TargetCookie;
import com.adobe.target.edge.client.utils.CookieUtils;
import com.adobe.target.delivery.v1.model.*;
import kong.unirest.*;
import org.apache.http.HttpStatus;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static com.adobe.target.edge.client.entities.TargetDeliveryRequestTest.*;
import static com.adobe.target.edge.client.utils.TargetConstants.COOKIE_NAME;

class TargetTestDeliveryRequestUtils {

    static final int SESSION_ID_COOKIE_MAX_AGE = 1860;
    static final int DEVICE_ID_COOKIE_MAX_AGE = 63244800;

    static PrefetchRequest getPrefetchViewsRequest() {
        PrefetchRequest prefetchRequest = new PrefetchRequest();
        ViewRequest requestDetails = new ViewRequest();
        prefetchRequest.setViews(Arrays.asList(requestDetails));
        return prefetchRequest;
    }

    static ExecuteRequest getMboxExecuteRequest() {
        List<MboxRequest> mboxRequests = new ArrayList() {{
            add(new MboxRequest().name("server-side-mbox").index(1));
            add(new MboxRequest().name("server-side-mbox").index(2));
            add(new MboxRequest().name("server-side-mbox-prefetch").index(1));
        }};
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setMboxes(mboxRequests);
        return executeRequest;
    }

    static Map<String, CustomerState> getCustomerIds() {
        Map<String, CustomerState> customerIds = new HashMap<>();
        customerIds.put("userid", CustomerState.authenticated("67312378756723456"));
        customerIds.put("puuid", CustomerState.unknown("550e8400-e29b-41d4-a716-446655440000"));
        return customerIds;
    }

    static Context getContext() {
        Context context = new Context();
        context.setChannel(ChannelType.WEB);
        context.setTimeOffsetInMinutes(330.0);
        context.setAddress(getAddress());
        return context;
    }

    static Address getAddress() {
        Address address = new Address();
        address.setUrl("http://localhost:8080");
        return address;
    }

    static List<TargetCookie> getTestCookies() {
        int timeNow = (int) (System.currentTimeMillis() / 1000);
        Optional<TargetCookie> targetCookie = CookieUtils.createTargetCookie(TEST_SESSION_ID, TEST_TNT_ID);
        String visitorValue = "-1330315163%7CMCIDTS%7C18145%7CMCMID%7C" + TargetDeliveryRequestTest.TEST_MCID +
                "%7CMCAAMLH-1567731426%7C9%7CMCAAMB-1568280923%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xz" +
                "PWQmdj0y%7CMCOPTOUT-1567683323s%7CNONE%7CMCAID%7CNONE%7CMCCIDH%7C1806392961";
        TargetCookie visitorCookie = null;
        try {
            visitorCookie = new TargetCookie("AMCV_" + URLEncoder.encode(TEST_ORG_ID, "UTF-8"),
                    visitorValue, timeNow + SESSION_ID_COOKIE_MAX_AGE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<TargetCookie> cookies = new ArrayList<>();
        Collections.addAll(cookies, targetCookie.get(), visitorCookie);
        return cookies;
    }

    static List<TargetCookie> getExpiredSessionCookie() {
        int timeNow = (int) (System.currentTimeMillis() / 1000);
        int sessionExpirationTime = timeNow - 1;
        int tntIdExpirationTime = timeNow + DEVICE_ID_COOKIE_MAX_AGE;
        String cookieValue = "session#430a140336d545daacde53af9636eef5#" +
                sessionExpirationTime + "|PC#" + TEST_TNT_ID + "#" + tntIdExpirationTime;
        TargetCookie targetCookie = new TargetCookie(COOKIE_NAME, cookieValue, timeNow);
        List<TargetCookie> cookies = new ArrayList<>();
        cookies.add(targetCookie);
        return cookies;
    }

    static List<Notification> getMboxNotifications() {
        List<Notification> notifications = new ArrayList<>();
        List<PrefetchMboxResponse> dummyMboxPrefetchResponse = getDummyMboxPrefetchResponse();
        for (PrefetchMboxResponse mbox : dummyMboxPrefetchResponse) {
            NotificationMbox notificationMbox = new NotificationMbox()
                    .name(mbox.getName())
                    .state(mbox.getState());
            Notification notification = new Notification()
                    .id(UUID.randomUUID().toString())
                    .impressionId(UUID.randomUUID().toString())
                    .mbox(notificationMbox);
            setNotificationOptions(notifications, notification, mbox.getOptions());
        }
        return notifications;
    }

    static void setNotificationOptions(List<Notification> notifications, Notification notification,
                                       List<Option> options) {
        notification.type(MetricType.DISPLAY).timestamp(System.currentTimeMillis());
        List<String> tokens = new ArrayList<>();
        for (Option option : options) {
            tokens.add(option.getEventToken());
        }
        notification.tokens(tokens);
        notifications.add(notification);
    }

    static List<PrefetchMboxResponse> getDummyMboxPrefetchResponse() {
        Option option = new Option().type(OptionType.HTML)
                .content("<b>Test option content</b>")
                .eventToken("DQ5I8XE7vs6wVIBc5m8");
        PrefetchMboxResponse prefetchMboxResponse = new PrefetchMboxResponse();
        prefetchMboxResponse.name("server-side-mbox");
        prefetchMboxResponse.state("R3GqCTBoZfbf6JuhJihve+");
        prefetchMboxResponse.options(Arrays.asList(option));
        return Arrays.asList(prefetchMboxResponse);
    }

    static HttpResponse<DeliveryResponse> getTestDeliveryResponse() {
        DeliveryResponse deliveryResponse = new DeliveryResponse() {

            @Override
            public Integer getStatus() {
                return HttpStatus.SC_OK;
            }

            @Override
            public VisitorId getId() {
                return new VisitorId().tntId(TEST_TNT_ID);
            }

        };
        RawResponse rawResponse = new RawResponse() {
            @Override
            public int getStatus() {
                return HttpStatus.SC_OK;
            }

            @Override
            public String getStatusText() {
                return null;
            }

            @Override
            public Headers getHeaders() {
                return new Headers();
            }

            @Override
            public InputStream getContent() {
                return null;
            }

            @Override
            public byte[] getContentAsBytes() {
                return new byte[0];
            }

            @Override
            public String getContentAsString() {
                return null;
            }

            @Override
            public String getContentAsString(String charset) {
                return null;
            }

            @Override
            public InputStreamReader getContentReader() {
                return null;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public String getEncoding() {
                return null;
            }

            @Override
            public Config getConfig() {
                return null;
            }

            @Override
            public HttpResponseSummary toSummary() {
                return null;
            }
        };
        HttpResponse<DeliveryResponse> basicResponse = new BasicResponse(rawResponse, deliveryResponse);
        return basicResponse;
    }
}
