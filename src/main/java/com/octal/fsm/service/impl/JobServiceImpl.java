package com.octal.fsm.service.impl;

import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.JobDTO;
import com.octal.fsm.dto.PageItem;
import com.octal.fsm.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    @Override
    public ResponseEntity<ApiResponse> getAllJobs(JobDTO.JobFilterRequestDTO jobFilterRequestDTO) {
        JobDTO.Response job1 = new JobDTO.Response(
                "#3201", "Fence Installation", "New",
                "20 Sep, 2025 - 23 Sep, 2025", "10:00 AM", "05:00 PM",
                "Sophie Rivas", "135 South Losalle Street, Washington"
        );
        JobDTO.Response job2 = new JobDTO.Response(
                "#3250", "Drain Maintenance", "New",
                "20 Sep, 2025 - 23 Sep, 2025", "10:00 AM", "05:00 PM",
                "Sophie Rivas", "135 South Losalle Street, Washington"
        );
        JobDTO.Response job3 = new JobDTO.Response(
                "#3520", "Fence Installation", "New",
                "20 Sep, 2025 - 23 Sep, 2025", "10:00 AM", "05:00 PM",
                "Sophie Rivas", "135 South Losalle Street, Washington"
        );
        List<JobDTO.Response> todaysJobs = Arrays.asList(job1, job2, job3);


        return new ResponseEntity<>(new ApiResponse("Jobs list",new PageItem<>(jobFilterRequestDTO.getPage(), todaysJobs.size(), todaysJobs,jobFilterRequestDTO.getLimit(),jobFilterRequestDTO.getPage()),"success",HttpStatus.OK), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> getJobById(String id) {
//        JobDTO.StartJobData jobDetail = new JobDTO.StartJobData(
//                "#3201", "Fence Installation", "New",
//                "20 Sep, 2025 - 23 Sep, 2025", "10:00 AM", "05:00 PM",
//                "Sophie Rivas", "135 South Losalle Street, Washington",
//                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
//                        "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
//                        "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
//                        "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",null,null
//        );
        return new ResponseEntity<>(new ApiResponse("Job detail",null,"success",HttpStatus.OK), HttpStatus.OK);
    }
}
