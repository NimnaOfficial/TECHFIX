package com.mad.techfix.models;

public class ImageUploadRequest {
    private String image_url;
    private String image_type;

    public ImageUploadRequest(String image_url, String image_type) {
        this.image_url = image_url;
        this.image_type = image_type;
    }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public String getImage_type() { return image_type; }
    public void setImage_type(String image_type) { this.image_type = image_type; }
}