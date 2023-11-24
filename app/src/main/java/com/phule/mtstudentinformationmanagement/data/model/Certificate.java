package com.phule.mtstudentinformationmanagement.data.model;

public class Certificate {
    private String certiName;
    private String certiScore;

    public Certificate() {
    }

    public Certificate(String certiName, String certiScore) {
        this.certiName = certiName;
        this.certiScore = certiScore;
    }

    public String getCertiName() {
        return certiName;
    }

    public void setCertiName(String certiName) {
        this.certiName = certiName;
    }

    public String getCertiScore() {
        return certiScore;
    }

    public void setCertiScore(String certiScore) {
        this.certiScore = certiScore;
    }
}
