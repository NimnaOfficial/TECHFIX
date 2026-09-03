package com.mad.techfix.models;

public class CloudinarySignatureResponse {
    private boolean success;
    private CloudinaryData data;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public CloudinaryData getData() { return data; }
    public void setData(CloudinaryData data) { this.data = data; }

    public static class CloudinaryData {
        private String cloudName;
        private String apiKey;
        private long timestamp;
        private String signature;
        private String folder;
        private String uploadPreset;

        public String getCloudName() { return cloudName; }
        public void setCloudName(String cloudName) { this.cloudName = cloudName; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }

        public String getFolder() { return folder; }
        public void setFolder(String folder) { this.folder = folder; }

        public String getUploadPreset() { return uploadPreset; }
        public void setUploadPreset(String uploadPreset) { this.uploadPreset = uploadPreset; }
    }
}