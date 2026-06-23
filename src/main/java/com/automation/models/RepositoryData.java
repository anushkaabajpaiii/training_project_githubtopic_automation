package com.automation.models;

/**
 * POJO representing extracted GitHub repository analytics data.
 */
public class RepositoryData {

    private String repositoryName;
    private String stars;
    private String forks;
    private String issues;
    private String primaryLanguage;
    private String description;

    public RepositoryData() {
    }

    public RepositoryData(String repositoryName, String stars, String forks,
                           String issues, String primaryLanguage, String description) {
        this.repositoryName = repositoryName;
        this.stars = stars;
        this.forks = forks;
        this.issues = issues;
        this.primaryLanguage = primaryLanguage;
        this.description = description;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getForks() {
        return forks;
    }

    public void setForks(String forks) {
        this.forks = forks;
    }

    public String getIssues() {
        return issues;
    }

    public void setIssues(String issues) {
        this.issues = issues;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Parses the star count string (handles formats like "1.2k", "15,234", "987").
     */
    public long getStarsAsLong() {
        return parseCount(stars);
    }

    public long getForksAsLong() {
        return parseCount(forks);
    }

    private long parseCount(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return 0L;
        }
        String cleaned = raw.trim().replace(",", "");
        try {
            if (cleaned.toLowerCase().endsWith("k")) {
                double val = Double.parseDouble(cleaned.substring(0, cleaned.length() - 1));
                return (long) (val * 1000);
            }
            if (cleaned.toLowerCase().endsWith("m")) {
                double val = Double.parseDouble(cleaned.substring(0, cleaned.length() - 1));
                return (long) (val * 1_000_000);
            }
            return Long.parseLong(cleaned);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public String toString() {
        return "RepositoryData{" +
                "repositoryName='" + repositoryName + '\'' +
                ", stars='" + stars + '\'' +
                ", forks='" + forks + '\'' +
                ", issues='" + issues + '\'' +
                ", primaryLanguage='" + primaryLanguage + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
