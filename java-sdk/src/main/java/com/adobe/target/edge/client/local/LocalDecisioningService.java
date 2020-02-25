package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.http.JacksonObjectMapper;
import com.adobe.target.edge.client.model.LocalDecisioningRule;
import com.adobe.target.edge.client.model.LocalDecisioningRuleSet;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.adobe.target.edge.client.model.TargetDeliveryResponse;
import com.adobe.target.edge.client.service.TargetClientException;
import com.adobe.target.edge.client.service.TargetExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jamsesso.jsonlogic.JsonLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class LocalDecisioningService {

    private static final Logger logger = LoggerFactory.getLogger(LocalDecisioningService.class);

    private final ClientConfig clientConfig;
    private final JsonLogic jsonLogic;

    public LocalDecisioningService(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.jsonLogic = new JsonLogic();
    }

    public TargetDeliveryResponse executeRequest(TargetDeliveryRequest deliveryRequest,
                                                 LocalDecisioningRuleSet ruleSet) {
        DeliveryRequest devRequest = deliveryRequest.getDeliveryRequest();
        String requestId = devRequest.getRequestId();
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        if (ruleSet == null) {
            DeliveryResponse deliveryResponse = new DeliveryResponse()
                    .client(clientConfig.getClient())
                    .requestId(requestId)
                    .id(devRequest.getId())
                    .status(500);
            return new TargetDeliveryResponse(deliveryRequest, deliveryResponse,
                    500, "Local-decisioning rules not available");
        }
        List<RequestDetails> prefetchRequests = new ArrayList<>();
        List<RequestDetails> executeRequests = new ArrayList<>();
        if (deliveryRequest.getDeliveryRequest().getPrefetch() != null) {
            prefetchRequests.addAll(new ArrayList<>(devRequest.getPrefetch().getMboxes()));
            prefetchRequests.addAll(new ArrayList<>(devRequest.getPrefetch().getViews()));
            if (devRequest.getPrefetch().getPageLoad() != null) {
                prefetchRequests.add(devRequest.getPrefetch().getPageLoad());
            }
        }
        if (deliveryRequest.getDeliveryRequest().getExecute() != null) {
            executeRequests.addAll(new ArrayList<>(devRequest.getExecute().getMboxes()));
            if (devRequest.getExecute().getPageLoad() != null) {
                executeRequests.add(devRequest.getExecute().getPageLoad());
            }
        }
        PrefetchResponse prefetchResponse = new PrefetchResponse();
        ExecuteResponse executeResponse = new ExecuteResponse();
        DeliveryResponse deliveryResponse = new DeliveryResponse()
                .client(clientConfig.getClient())
                .requestId(requestId)
                .id(devRequest.getId())
                .status(200);
        TargetDeliveryResponse targetResponse = new TargetDeliveryResponse(deliveryRequest,
                deliveryResponse, 200, "Local-decisioning response");
        List<LocalDecisioningRule> rules = ruleSet.getRules();
        if (rules != null) {
            String visitorId = getOrCreateVisitorId(deliveryRequest, targetResponse);
            for (RequestDetails details : prefetchRequests) {
                boolean handled = false;
                for (LocalDecisioningRule rule : rules) {
                    Map<String, Object> resultMap = executeRule(deliveryRequest,
                            targetResponse, details, visitorId, rule);
                    handled |= handleResult(resultMap, details, prefetchResponse, null);
                }
                if (!handled) {
                    unhandledResponse(details, prefetchResponse, null);
                }
                deliveryResponse.setPrefetch(prefetchResponse);
            }
            for (RequestDetails details : executeRequests) {
                boolean handled = false;
                for (LocalDecisioningRule rule : rules) {
                    Map<String, Object> resultMap = executeRule(deliveryRequest,
                            targetResponse, details, visitorId, rule);
                    handled |= handleResult(resultMap, details, null, executeResponse);
                }
                if (!handled) {
                    unhandledResponse(details, null, executeResponse);
                }
                deliveryResponse.setExecute(executeResponse);
            }
        }
        return targetResponse;
    }

    private Map<String, Object> executeRule(TargetDeliveryRequest deliveryRequest,
                                            TargetDeliveryResponse targetResponse,
                                            RequestDetails details,
                                            String visitorId,
                                            LocalDecisioningRule rule) {
        Map<String, Object> condition = rule.getCondition();
        Map<String, Object> data = new HashMap<>();
        data.put("allocation", computeAllocation(visitorId, rule));
        addTimeParams(data);
        data.put("user", new UserParamsCollator().collateParams(deliveryRequest, details, rule.getMeta()));
        data.put("page", new PageParamsCollator().collateParams(deliveryRequest, details, rule.getMeta()));
        data.put("referring", new PageParamsCollator(true).collateParams(deliveryRequest, details, rule.getMeta()));
        data.put("mbox", new CustomParamsCollator().collateParams(deliveryRequest, details, rule.getMeta()));
        logger.info("data="+data);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String expression = mapper.writeValueAsString(condition);
            logger.info("expression="+expression);
            return ((Boolean) jsonLogic.apply(expression, data)) ? rule.getConsequence() : null;
        }
        catch (Exception e) {
            String message = "Hit exception while evaluating local-decisioning rule";
            logger.warn(message, e);
            TargetExceptionHandler handler = this.clientConfig.getExceptionHandler();
            if (handler != null) {
                handler.handleException(new TargetClientException(message, e));
            }
            return null;
        }
    }

    private boolean handleResult(Map<String, Object> resultMap,
                                 RequestDetails details,
                                 PrefetchResponse prefetchResponse,
                                 ExecuteResponse executeResponse) {
        logger.info("resultMap=" + resultMap);
        if (resultMap == null || resultMap.isEmpty()) {
            return false;
        }
        ObjectMapper mapper = new JacksonObjectMapper().getMapper();
        if (details instanceof ViewRequest) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> viewArray = (List<Map<String, Object>>) resultMap.get("views");
            if (viewArray != null) {
                List<View> views = mapper.convertValue(viewArray, new TypeReference<List<View>>() {
                });
                if (prefetchResponse != null) {
                    prefetchResponse.setViews(views);
                    return true;
                }
            }
        }
        else if (details instanceof MboxRequest) {
            MboxRequest mboxRequest = (MboxRequest) details;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mboxArray = (List<Map<String, Object>>) resultMap.get("mboxes");
            if (mboxArray != null) {
                for (Map<String, Object> mboxMap : mboxArray) {
                    String mbox = (String) mboxMap.get("name");
                    if (mbox.equals(((MboxRequest) details).getName())) {
                        List<Option> options = mapper.convertValue(mboxMap.get("options"),
                                new TypeReference<List<Option>>() {
                        });
                        List<Metric> metrics = mapper.convertValue(mboxMap.get("metrics"),
                                new TypeReference<List<Metric>>() {
                        });
                        if (prefetchResponse != null) {
                            PrefetchMboxResponse prefetchMboxResponse = new PrefetchMboxResponse();
                            prefetchMboxResponse.setName(mbox);
                            prefetchMboxResponse.setIndex(mboxRequest.getIndex());
                            prefetchMboxResponse.setOptions(options);
                            prefetchMboxResponse.setMetrics(metrics);
                            prefetchResponse.addMboxesItem(prefetchMboxResponse);
                            return true;
                        } else {
                            MboxResponse mboxResponse = new MboxResponse();
                            mboxResponse.setName(mbox);
                            mboxResponse.setIndex(mboxRequest.getIndex());
                            mboxResponse.setOptions(options);
                            mboxResponse.setMetrics(metrics);
                            executeResponse.addMboxesItem(mboxResponse);
                            return true;
                        }
                    }
                }
            }
        }
        else {
            PageLoadResponse pageLoadResponse = new PageLoadResponse();
            @SuppressWarnings("unchecked")
            Map<String, Object> pageLoad = (Map<String, Object>)resultMap.get("pageLoad");
            if (pageLoad != null) {
                List<Option> options = mapper.convertValue(pageLoad.get("options"),
                        new TypeReference<List<Option>>() {
                });
                List<Metric> metrics = mapper.convertValue(pageLoad.get("metrics"),
                        new TypeReference<List<Metric>>() {
                });
                pageLoadResponse.setOptions(options);
                pageLoadResponse.setMetrics(metrics);
            }
            if (prefetchResponse != null) {
                prefetchResponse.setPageLoad(pageLoadResponse);
                return true;
            }
            else {
                executeResponse.setPageLoad(pageLoadResponse);
                return true;
            }
        }
        return false;
    }

    private void unhandledResponse(RequestDetails details,
                                   PrefetchResponse prefetchResponse,
                                   ExecuteResponse executeResponse) {
        if (details instanceof ViewRequest) {
            prefetchResponse.addViewsItem(new View());
        }
        else if (details instanceof MboxRequest) {
            MboxRequest request = (MboxRequest)details;
            if (prefetchResponse != null) {
                PrefetchMboxResponse response = new PrefetchMboxResponse();
                response.setIndex(request.getIndex());
                response.setName(request.getName());
                prefetchResponse.addMboxesItem(response);
            }
            else {
                MboxResponse response = new MboxResponse();
                response.setIndex(request.getIndex());
                response.setName(request.getName());
                executeResponse.addMboxesItem(response);
            }
        }
        else {
            if (prefetchResponse != null) {
                prefetchResponse.setPageLoad(new PageLoadResponse());
            }
            else {
                executeResponse.setPageLoad(new PageLoadResponse());
            }
        }
    }

    private String getOrCreateVisitorId(TargetDeliveryRequest deliveryRequest,
                                        TargetDeliveryResponse targetResponse) {
        String vid = null;
        VisitorId visitorId = deliveryRequest.getDeliveryRequest().getId();
        if (visitorId != null) {
            vid = visitorId.getTntId();
        }
        else {
            visitorId = targetResponse.getResponse().getId();
            if (visitorId != null) {
                vid = visitorId.getTntId();
            }
        }
        if (vid == null) {
            vid = UUID.randomUUID().toString();
            visitorId = new VisitorId().tntId(vid);
            targetResponse.getResponse().setId(visitorId);
        }
        return vid;
    }

    private void addTimeParams(Map<String, Object> data) {
        long now = System.currentTimeMillis();
        data.put("current_timestamp", now);
        SimpleDateFormat dayFormat = new SimpleDateFormat("u");
        dayFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        data.put("current_day", dayFormat.format(now));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        data.put("current_time", timeFormat.format(now));
    }

    private double computeAllocation(String vid, LocalDecisioningRule rule) {
        String client = this.clientConfig.getClient();
        String activityId = rule.getMeta().get("activityId").toString();
        int index = vid.indexOf(".");
        if (index > 0) {
            vid = vid.substring(0, index);
        }
        String input = client + "." + activityId + "." + vid;
        int output = MurmurHash.hash32(input);
        return ((Math.abs(output) % 10000) / 10000D) * 100D;
    }

}
