package com.example.landahand.model.dto;

import com.example.landahand.model.Availability;
import com.example.landahand.model.Request;
import lombok.Data;

import java.util.List;

@Data

public class RequestDto {
    private Long requestId;
    private Long postId;
    private Request.RequestStatus status;
    private Long userPosterId;
    private Long userRequesterId;
    private Long availabilityId;
    private String userPosterName;
    private String userRequesterName;
    private String userBio;
    private String PostName;
    private String availabilityTime;
    private List<String> picture;
    private String userRequesterEmail;

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Request.RequestStatus getStatus() {
        return status;
    }

    public void setStatus(Request.RequestStatus status) {
        this.status = status;
    }

    public Long getUserPosterId() {
        return userPosterId;
    }

    public void setUserPosterId(Long userPosterId) {
        this.userPosterId = userPosterId;
    }

    public Long getUserRequesterId() {
        return userRequesterId;
    }

    public void setUserRequesterId(Long userRequesterId) {
        this.userRequesterId = userRequesterId;
    }

    public Long getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }

    public String getUserPosterName() {
        return userPosterName;
    }

    public void setUserPosterName(String userPosterName) {
        this.userPosterName = userPosterName;
    }

    public String getUserRequesterName() {
        return userRequesterName;
    }

    public void setUserRequesterName(String userRequesterName) {
        this.userRequesterName = userRequesterName;
    }

    public String getPostName() {
        return PostName;
    }

    public void setPostName(String postName) {
        PostName = postName;
    }

    public String getAvailabilityTime() {
        return availabilityTime;
    }

    public void setAvailabilityTime(String availabilityTime) {
        this.availabilityTime = availabilityTime;
    }

    public List<String> getPicture() {
        return picture;
    }

    public void setPicture(List<String> picture) {
        this.picture = picture;
    }

    public String getUserRequesterEmail() {
        return userRequesterEmail;
    }

    public void setUserRequesterEmail(String userRequesterEmail) {
        this.userRequesterEmail = userRequesterEmail;
    }

    public Request toRequest(Availability availability) {
        Request request = new Request();
        request.setAvailability(availability);
        request.setStatus(status);
        return request;
    }


}
