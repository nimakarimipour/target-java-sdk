package com.adobe.target.edge.client.model;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.Attributes;

import java.util.*;

public class TargetAttributesResponse implements Attributes {

    private final TargetDeliveryResponse response;
    private final Map<String, Map<String, Object>> content = new HashMap<>();

    public TargetAttributesResponse(TargetDeliveryResponse response) {
        this.response = response;
    }

    @Override
    public boolean getFeatureBoolean(String mbox, String key) {
        Map<String, Object> map = toMap(mbox);
        if (map != null) {
            Object value = map.get(key);
            if (value instanceof Boolean) {
                return ((Boolean) value);
            }
        }
        return false;
    }

    @Override
    public String getFeatureString(String mbox, String key) {
        Map<String, Object> map = toMap(mbox);
        if (map != null) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    @Override
    public int getFeatureInteger(String mbox, String key) {
        Map<String, Object> map = toMap(mbox);
        if (map != null) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            else if (value instanceof String) {
                return Double.valueOf((String)value).intValue();
            }
        }
        return 0;
    }

    @Override
    public double getFeatureDouble(String mbox, String key) {
        Map<String, Object> map = toMap(mbox);
        if (map != null) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            else if (value instanceof String) {
                return Double.parseDouble((String)value);
            }
        }
        return 0d;
    }

    @Override
    public TargetDeliveryResponse getResponse() {
        return response;
    }

    @Override
    public Map<String, Object> toMap(String mbox) {
        Map<String, Object> mboxContent = this.content.get("mbox");
        if (mboxContent != null) {
            return mboxContent;
        }
        if (this.response == null || mbox == null) {
            return null;
        }
        synchronized (this.content) {
            List<MboxResponse> allMboxes = new ArrayList<>();
            DeliveryResponse response = this.response.getResponse();
            PrefetchResponse prefetchResponse = response.getPrefetch();
            if (prefetchResponse != null) {
                List<PrefetchMboxResponse> prefectchMboxes = prefetchResponse.getMboxes();
                allMboxes.addAll(prefectchMboxes);
            }
            ExecuteResponse executeResponse = response.getExecute();
            if (executeResponse != null) {
                List<MboxResponse> executeMboxes = executeResponse.getMboxes();
                allMboxes.addAll(executeMboxes);
            }
            mboxContent = new HashMap<>();
            for (MboxResponse resp : allMboxes) {
                if (resp.getName() == null || !mbox.equals(resp.getName())) {
                    continue;
                }
                List<Option> options = resp.getOptions();
                for (Option option : options) {
                    Object contentMap = option.getContent();
                    if (option.getType() == OptionType.JSON && contentMap instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> contentObj = (Map<String, Object>) contentMap;
                        mboxContent.putAll(contentObj);
                    }
                }
            }
            this.content.put(mbox, mboxContent);
        }
        return mboxContent;
    }
}