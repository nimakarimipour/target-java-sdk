package com.adobe.target.edge.client.local;

import com.adobe.target.delivery.v1.model.*;
import com.adobe.target.edge.client.ClientConfig;
import com.adobe.target.edge.client.model.LocalDecisioningRule;
import com.adobe.target.edge.client.model.LocalDecisioningRuleSet;
import com.adobe.target.edge.client.model.TargetDeliveryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public final class TraceHandler {

    private static final String MATCHED_IDS_KEY = "matchedSegmentIds";
    private static final String UNMATCHED_IDS_KEY = "unmatchedSegmentIds";
    private static final String MATCHED_RULES_KEY = "matchedRuleConditions";
    private static final String UNMATCHED_RULES_KEY = "unmatchedRuleConditions";

    private final TimeZone utc = TimeZone.getTimeZone("UTC");

    private final ObjectMapper mapper;
    private final LocalDecisioningRuleSet ruleSet;

    private final Map<String, Object> trace;
    private final Map<Number, Map<String, Object>> campaigns;
    private final Map<Number, Map<String, Object>> evaluatedTargets;

    public TraceHandler(ClientConfig clientConfig,
            RuleLoader ruleLoader,
            ObjectMapper mapper,
            LocalDecisioningRuleSet ruleSet,
            TargetDeliveryRequest request) {
        this.mapper = mapper;
        this.ruleSet = ruleSet;
        this.trace = new HashMap<>();
        this.trace.put("clientCode", clientConfig.getClient());
        this.trace.put("artifact", this.artifactTrace(ruleLoader, ruleSet));
        this.trace.put("profile", this.profileTrace(request.getDeliveryRequest().getId()));
        this.campaigns = new HashMap<>();
        this.evaluatedTargets = new HashMap<>();
    }

    public void updateRequest(TargetDeliveryRequest request, RequestDetails details) {
        this.trace.put("request", this.requestTrace(request, details));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addCampaign(LocalDecisioningRule rule, Map<String, Object> context,
            boolean matched) {
        Map<String, Object> meta = rule.getMeta();
        Number activityId = (Number)meta.get("activityId");
        if (activityId != null) {
            if (!this.campaigns.containsKey(activityId)) {
                Map<String, Object> campaign = this.campaignTrace(rule);
                campaign.put("branchId", meta.get("experienceId"));
                campaign.put("offers", meta.get("offerIds"));
                campaign.put("environmentId", meta.get("environmentId"));
                Map<String, Object> consequence = rule.getConsequence();
                campaign.put("metrics", consequence.get("metrics"));
                this.campaigns.put(activityId, campaign);
            }
            Map<String, Object> target = this.evaluatedTargets.get(activityId);
            if (target == null) {
                target = this.campaignTargetTrace(rule, context);
                this.evaluatedTargets.put(activityId, target);
            }
            List audienceIds = (List)meta.get("audienceIds");
            List ids;
            List rules;
            if (matched) {
                ids = (List)target.get(MATCHED_IDS_KEY);
                rules = (List)target.get(MATCHED_RULES_KEY);
            }
            else {
                ids = (List)target.get(UNMATCHED_IDS_KEY);
                rules = (List)target.get(UNMATCHED_RULES_KEY);
            }
            ids.addAll(audienceIds);
            rules.add(rule.getCondition());
        }
    }

    public Map<String, Object> getCurrentTrace() {
        Map<String, Object> currentTrace = new HashMap<>(this.trace);
        currentTrace.put("campaigns", campaigns.values());
        currentTrace.put("evaluatedCampaignTargets", this.evaluatedTargets.values());
        return currentTrace;
    }

    private Map<String, Object> artifactTrace(RuleLoader ruleLoader, LocalDecisioningRuleSet ruleSet) {
        Map<String, Object> artifacts = new HashMap<>(ruleSet.getMeta());
        artifacts.put("artifactVersion", ruleSet.getVersion());
        artifacts.put("pollingInterval", ruleLoader.getPollingInterval());
        artifacts.put("artifactRetrievalCount", ruleLoader.getNumFetches());
        artifacts.put("artifactLocation", ruleLoader.getLocation());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        format.setTimeZone(this.utc);
        artifacts.put("artifactLastRetrieved", format.format(ruleLoader.getLastFetch()));
        return artifacts;
    }

    private Map<String, Object> profileTrace(VisitorId visitorId) {
        Map<String, Object> profile = new HashMap<>();
        if (visitorId != null) {
            profile.put("visitorId", mapper.convertValue(visitorId, Map.class));
        }
        return profile;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Map<String, Object> requestTrace(TargetDeliveryRequest request,
            RequestDetails details) {
        Map<String, Object> req = new HashMap<>();
        req.put("sessionId", request.getSessionId());
        if (details instanceof ViewRequest) {
            req.put("view", mapper.convertValue(details, Map.class));
        }
        else {
            Map mbox = mapper.convertValue(details, Map.class);
            if (!mbox.containsKey("name")) {
                mbox.put("name", this.ruleSet.getGlobalMbox());
            }
            req.put("mbox", mbox);
        }
        Address address = details.getAddress();
        if (address != null) {
            String urlStr = address.getUrl();
            req.put("url", address.getUrl());
            try {
                URL url = new URL(urlStr);
                req.put("host", url.getHost());
            }
            catch (MalformedURLException e) {
                // ignore
            }
        }
        return req;
    }

    private Map<String, Object> campaignTargetTrace(LocalDecisioningRule rule,
            Map<String, Object> context) {
        Map<String, Object> target = campaignTrace(rule);
        target.put("context", context);
        target.put(MATCHED_IDS_KEY, new ArrayList<>());
        target.put(UNMATCHED_IDS_KEY, new ArrayList<>());
        target.put(MATCHED_RULES_KEY, new ArrayList<>());
        target.put(UNMATCHED_RULES_KEY, new ArrayList<>());
        return target;
    }

    private Map<String, Object> campaignTrace(LocalDecisioningRule rule) {
        Map<String, Object> campaign = new HashMap<>();
        Map<String, Object> meta = rule.getMeta();
        campaign.put("id", meta.get("activityId"));
        campaign.put("activityName", meta.get("activityName"));
        campaign.put("activityType", meta.get("activityType"));
        return campaign;
    }
}
