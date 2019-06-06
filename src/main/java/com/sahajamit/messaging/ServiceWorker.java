package com.sahajamit.messaging;

public class ServiceWorker {
    private String versionId;
    private String registrationId;
    private String targetId;
    private String status;
    private String runningStatus;

    public ServiceWorker(String versionId, String registrationId, String targetId) {
        this.versionId = versionId;
        this.registrationId = registrationId;
        this.targetId = targetId;
    }

    public String getVersionId() {
        return versionId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(String runningStatus) {
        this.runningStatus = runningStatus;
    }

    @Override
    public String toString() {
        return "ServiceWorker{" +
                "versionId='" + versionId + '\'' +
                ", registrationId='" + registrationId + '\'' +
                ", targetId='" + targetId + '\'' +
                ", status='" + status + '\'' +
                ", runningStatus='" + runningStatus + '\'' +
                '}';
    }
}
