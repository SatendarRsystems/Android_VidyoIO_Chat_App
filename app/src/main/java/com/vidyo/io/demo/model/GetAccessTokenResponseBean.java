package com.vidyo.io.demo.model;

/**
 * Summary: POJO class for getAccessToken api response
 * Description: GetAccessTokenResponseBean use for parsing the json api and getter setter
 * @author RSI
 * @date 20.08.2018
 */

public class GetAccessTokenResponseBean {
    private String accessToken;
    private String username;
    private String status;
    private String meetingId;


    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
