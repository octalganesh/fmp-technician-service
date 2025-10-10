package com.octal.fsm.service.impl;

import com.octal.fsm.clients.JobClient;
import com.octal.fsm.common.ApiResponse;
import com.octal.fsm.dto.JobDTO;
import com.octal.fsm.dto.PageItem;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.service.JobService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobClient jobClient;

    @Override
    public ResponseEntity<ApiResponse> getAllJobs(JobDTO.JobFilterRequest jobFilterRequestDTO, Technician loggedInTechnician)throws CodeException {
//        JobDTO.Response job1 = new JobDTO.Response(UUID.randomUUID().toString(),
//                "3201", "Fence Installation", "New",
//                "2025-09-20", "2025-09-23", "10:00 AM", "05:00 PM",
//                "Sophie Rivas","https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y", "135 South Losalle Street, Washington"
//        );
//        JobDTO.Response job2 = new JobDTO.Response(UUID.randomUUID().toString(),
//                "3250", "Drain Maintenance", "Ongoing",
//                "2025-09-20", "2025-09-23", "10:00 AM", "05:00 PM",
//                "Sophie Rivas","https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y", "135 South Losalle Street, Washington"
//        );
//        JobDTO.Response job3 = new JobDTO.Response(UUID.randomUUID().toString(),
//                "3520", "Fence Installation", "Completed",
//                "2025-09-20", "2025-09-23", "10:00 AM", "05:00 PM",
//                "Sophie Rivas","https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y", "135 South Losalle Street, Washington"
//        );
//        List<JobDTO.Response> todaysJobs = Arrays.asList(job1, job2, job3);

        try {
            ResponseEntity<ApiResponse> response = jobClient.getJobTasksForTechnician(jobFilterRequestDTO,loggedInTechnician.getUuid(),loggedInTechnician.getEmail());

            return response;

        } catch (FeignException e) {
            throw new CodeException("Remote admin-service failed: " + e.contentUTF8(), ErrorCode.COMMON);
        }

        //return new ResponseEntity<>(new ApiResponse("Jobs list", new PageItem<>(jobFilterRequestDTO.getPage(), todaysJobs.size(), todaysJobs, jobFilterRequestDTO.getLimit(), jobFilterRequestDTO.getPage()), "200", HttpStatus.OK), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> getJobById(String id) {
        // MockJobDetails
        JobDTO.Details mockJob = new JobDTO.Details();
        mockJob.setId(UUID.randomUUID().toString());
        mockJob.setCustomerName("Sophie Rivas");
        mockJob.setEmail("sophierivas@gmail.com");
        mockJob.setMobileNumber("+14087412589");
        mockJob.setAddress("135 South Lasalle Street, Washington");
        mockJob.setJobTitle("Fence Installation");
        mockJob.setJobId(id);
        mockJob.setJobType("New Window Installation");
        mockJob.setStartDate("2025-09-20");
        mockJob.setEndDate("2025-09-23");
        mockJob.setStartTime("10:00 AM");
        mockJob.setEndTime("05:00 PM");
        mockJob.setStatus("New");
        mockJob.setJobTags(Arrays.asList("Installation Needed", "Onsite Data Collected", "Permit Required"));
        mockJob.setUploadedDocuments(Arrays.asList(
                new JobDTO.Document("https://yutka-fence.s3.ap-south-1.amazonaws.com/sample-pdf/Yukta_Fence_Work_Permit.pdf", "pdf"),
                new JobDTO.Document("https://yutka-fence.s3.ap-south-1.amazonaws.com/sample-pdf/Yukta_Fence_Work_Permit.pdf", "pdf")
        ));
        mockJob.setJobDescription("We are seeking a reliable Cooling Technician to carry out company-assigned cooling tune-up tasks. The role involves inspecting, cleaning, and servicing cooling systems to ensure efficiency, safety, and long-lasting performance.");

        return new ResponseEntity<>(new ApiResponse("Job detail", mockJob, "200", HttpStatus.OK), HttpStatus.OK);
    }
}
