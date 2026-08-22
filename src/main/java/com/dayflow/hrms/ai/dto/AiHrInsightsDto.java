package com.dayflow.hrms.ai.dto;

import java.util.List;

public class AiHrInsightsDto {
    private String title;
    private String overview;
    private List<String> keyInsights;
    private List<String> recommendations;

    public AiHrInsightsDto() {}

    public AiHrInsightsDto(String title, String overview, List<String> keyInsights, List<String> recommendations) {
        this.title = title;
        this.overview = overview;
        this.keyInsights = keyInsights;
        this.recommendations = recommendations;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public List<String> getKeyInsights() { return keyInsights; }
    public void setKeyInsights(List<String> keyInsights) { this.keyInsights = keyInsights; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}
