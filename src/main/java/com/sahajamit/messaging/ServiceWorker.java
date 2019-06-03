package com.sahajamit.messaging;

public class ServiceWorker {
    private String versionId;
    private String registrationId;
    private String targetId;

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
}
