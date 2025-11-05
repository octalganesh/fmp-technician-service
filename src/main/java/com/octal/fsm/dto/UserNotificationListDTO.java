package com.octal.fsm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNotificationListDTO {
    private int totalPages;
    private long totalItems;
    private List<Record> items;
    private int pageNumber;
    private int pageSize;

    @Data
    public static class PermissionList {
        private String moduleName;
        private String permissionName;
        private Boolean isEnable;
    }

    @Data
    public static class ResponseWithPermission {
        private UserNotificationListDTO response;
        private List<PermissionList> permissionList;
    }

    @Data
    public static class Record {
        private String userId;
        private String userImageUrl;
        private String userName;
        private String message;
        private String typeId;
        private String type;
        private String notificationImageUrl;
        private String time;
        private Boolean seen;
    }

    @Data
    public static class ListRequest {
        private Integer page;
        private Integer limit;
        private String sort;
        private boolean order;
        private String userId;
    }

    @Data
    public static class AllNotificationList {
        private Integer pageNo = 0;
        private Integer pageSize = 10;
        private String sortBy = "createdAt";
        private boolean asc = true;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate startDate;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate endDate;
    }

}
