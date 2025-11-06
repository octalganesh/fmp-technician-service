package com.octal.fsm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserDetailsDTO {

    private UserDetailsDTO() {
        //private default constructor
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    public static class ProfileDetails {
        private String id;
        private String fullName;
        private String userName;
        private Boolean followed;
        private String email;
        private String ext;
        private String contactNumber;
        private String dob;
        private String profilePic;
        private String bio;
        private String location;
        private String totalFollowers;
        private String totalFollowing;
        private String totalVideos;
        private String totalLikes;
        private Boolean isBadged;
        private Boolean isSubscribed;
        private Boolean messageEnable;

        private ProfileDetails() {
            // default constructor
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Register {
        private String id;
        private String email;
        private String userName;
        private String userType;
    }

    @Data
    public static class SignUpInit {
        private String countryId;
        private String extNumber;
        private Boolean isMobile;
        private String mobileOrEmail;

        private SignUpInit() {
            //no args constructor
        }
    }
}
