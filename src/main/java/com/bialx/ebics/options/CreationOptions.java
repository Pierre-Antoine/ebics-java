package com.bialx.ebics.options;

/**
 * Created by pierre-antoine.marc on 15/07/2016.
 */
public class CreationOptions {

    private String userId;
    private String hostId;
    private String bankUrl;
    private String bankName;
    private String partnerId;

    public CreationOptions(String userId, String hostId, String bankUrl, String bankName, String partnerId) {
        this.userId = userId;
        this.hostId = hostId;
        this.bankUrl = bankUrl;
        this.bankName = bankName;
        this.partnerId = partnerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getBankUrl() {
        return bankUrl;
    }

    public void setBankUrl(String bankUrl) {
        this.bankUrl = bankUrl;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
}
