package com.mad.techfix.models.admin;

import com.google.gson.annotations.SerializedName;

public class LogEntry {
    private int id;
    private String level;
    private String method;
    private String path;
    private String message;
    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public String getLevel() { return level; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
}
