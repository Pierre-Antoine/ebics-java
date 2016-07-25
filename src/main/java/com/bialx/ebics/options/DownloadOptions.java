package com.bialx.ebics.options;

/**
 * Created by pierre-antoine.marc on 15/07/2016.
 */
public class DownloadOptions {
    private String userId;
    private String hostId;
    private String partnerId;
    private String format;
    private String destination;

    public DownloadOptions(String userId, String hostId, String partnerId, String format, String destination) {
        this.userId = userId;
        this.hostId = hostId;
        this.partnerId = partnerId;
        this.format = format;
        this.destination = destination;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDestination() {
        return destination;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public void setDestination(String destination) {
        this.destination = destination;


    }
}
