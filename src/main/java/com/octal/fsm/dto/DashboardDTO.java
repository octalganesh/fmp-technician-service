package com.octal.fsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DashboardDTO {
    private SummaryCardsDTO summaryCards;
    private List<RevenueOverviewPointDTO> revenueOverview;
    private List<JobTypeSummaryDTO> jobsByType;
    private List<LocationSummaryDTO> popularLocations;
    private List<UserGrowthDTO> userGrowth;
    private List<ActivityDTO> recentActivity;

    @Data
    public static  class SummaryCardsDTO {
        private int totalTechnicians;
        private int frontOfficeStaff;
        private int totalCustomers;
        private int activeJobs;
        private int totalJobs;
        private double totalRevenue;
        private double pendingAmount;
    }

    @Data
    @AllArgsConstructor
    public class RevenueOverviewPointDTO {
        private String month; // "Jan", "Feb" etc.
        private double value; // revenue value
    }
    @Data
    @AllArgsConstructor
    public class JobTypeSummaryDTO {
        private String jobType; // "Plumbing", "Electrical", etc.
        private int numJobs;
        private int percentage; // 0-100
    }

    @Data
    @AllArgsConstructor
    public class LocationSummaryDTO {
        private String name; // "Downtown", etc.
        private int jobs;
        private double revenue;
    }

    @Data
    @AllArgsConstructor
    public class UserGrowthDTO {
        private String week; // "Week 1", etc.
        private int count;
    }

    @Data
    @AllArgsConstructor
    public class ActivityDTO {
        private String user; // "John Doe"
        private String activity; // "created a new account"
        private String timeAgo; // "2 minutes ago", or use Instant for precise time
    }


    public DashboardDTO createMockDashboard() {
        DashboardDTO dto = new DashboardDTO();

        // Summary cards mock
        SummaryCardsDTO summaryCards = new SummaryCardsDTO();
        summaryCards.setTotalTechnicians(45);
        summaryCards.setFrontOfficeStaff(12);
        summaryCards.setTotalCustomers(2847);
        summaryCards.setActiveJobs(127);
        summaryCards.setTotalJobs(1234);
        summaryCards.setTotalRevenue(145231.89);
        summaryCards.setPendingAmount(12456.78);
        dto.setSummaryCards(summaryCards);

        // Revenue Overview mock
        List<RevenueOverviewPointDTO> revenueOverview =List.of(
                new RevenueOverviewPointDTO("Jan", 3800),
                new RevenueOverviewPointDTO("Feb", 4200),
                new RevenueOverviewPointDTO("Mar", 6100),
                new RevenueOverviewPointDTO("Apr", 5400),
                new RevenueOverviewPointDTO("May", 6000),
                new RevenueOverviewPointDTO("Jun", 6600),
                new RevenueOverviewPointDTO("Jul", 7800)
        );
        dto.setRevenueOverview(revenueOverview);

        // Jobs by type mock
        List<JobTypeSummaryDTO> jobsByType = List.of(
                new JobTypeSummaryDTO("Plumbing", 45, 35),
                new JobTypeSummaryDTO("Electrical", 32, 25),
                new JobTypeSummaryDTO("HVAC", 28, 22),
                new JobTypeSummaryDTO("Carpentry", 23, 18)
        );
        dto.setJobsByType(jobsByType);

        // Popular locations mock
        List<LocationSummaryDTO> popularLocations = List.of(
                new LocationSummaryDTO("Downtown", 89, 45230),
                new LocationSummaryDTO("Suburbs", 67, 32450),
                new LocationSummaryDTO("Industrial Area", 45, 28670),
                new LocationSummaryDTO("Business District", 34, 19890)
        );
        dto.setPopularLocations(popularLocations);

        // User growth mock
        List<UserGrowthDTO> userGrowth = List.of(
                new UserGrowthDTO("Week 1", 1200),
                new UserGrowthDTO("Week 2", 1480),
                new UserGrowthDTO("Week 3", 1045),
                new UserGrowthDTO("Week 4", 1610)
        );
        dto.setUserGrowth(userGrowth);

        // Recent activity mock
        List<ActivityDTO> recentActivity = List.of(
                new ActivityDTO("John Doe", "created a new account", "2 minutes ago"),
                new ActivityDTO("Sarah Wilson", "made a purchase", "1 hour ago"),
                new ActivityDTO("Mike Johnson", "updated profile", "3 hours ago"),
                new ActivityDTO("Emma Davis", "left a review", "5 hours ago")
        );
        dto.setRecentActivity(recentActivity);

        return dto;
    }
}






