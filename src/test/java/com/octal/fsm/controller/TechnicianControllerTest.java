package com.octal.fsm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.fsm.dto.TechnicianDto;
import com.octal.fsm.entities.enums.Gender;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.models.request.PageRequest;
import com.octal.fsm.service.TechnicianService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TechnicianController.class)
@ActiveProfiles("test")
class TechnicianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TechnicianService technicianService;

    @Autowired
    private ObjectMapper objectMapper;

    private TechnicianDto.Add addTechnicianDto;
    private TechnicianDto.list technicianListDto;
    private String technicianId;

    @BeforeEach
    void setUp() {
        technicianId = UUID.randomUUID().toString();
        
        // Setup Add DTO
        addTechnicianDto = new TechnicianDto.Add(
            null,
            "John Doe",
            "john.doe@example.com",
            "9876543210",
            null,
            null,
            "123 Main St, City, State",
            true,
            Gender.MALE
        );
        
        // Setup list DTO
        technicianListDto = new TechnicianDto.list();
        technicianListDto.setId(technicianId);
        technicianListDto.setName("John Doe");
        technicianListDto.setEmail("john.doe@example.com");
        technicianListDto.setMobileNumber("9876543210");
        technicianListDto.setEmployeeId("EMP001");
        technicianListDto.setAddress("123 Main St, City, State");
        technicianListDto.setIsActive(true);
        technicianListDto.setGender(Gender.MALE);
    }

    @Test
    void addTechnician_WhenValidInput_ShouldReturnSuccess() throws Exception {
        // Given
        when(technicianService.addTechnician(any(TechnicianDto.Add.class))).thenReturn(technicianId);

        // When & Then
        mockMvc.perform(post("/technician/add-technician")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addTechnicianDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("technician added Successfully!"))
                .andExpect(jsonPath("$.data").value(technicianId));
    }

    @Test
    void addTechnician_WhenUpdateExisting_ShouldReturnSuccessWithUpdateMessage() throws Exception {
        // Given
        addTechnicianDto.setId(technicianId);
        when(technicianService.addTechnician(any(TechnicianDto.Add.class))).thenReturn(technicianId);

        // When & Then
        mockMvc.perform(post("/technician/add-technician")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addTechnicianDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("technician updated Successfully!"))
                .andExpect(jsonPath("$.data").value(technicianId));
    }

    @Test
    void addTechnician_WhenServiceThrowsException_ShouldReturnErrorResponse() throws Exception {
        // Given
        when(technicianService.addTechnician(any(TechnicianDto.Add.class)))
                .thenThrow(new CodeException("name is required", ErrorCode.COMMON));

        // When & Then
        mockMvc.perform(post("/technician/add-technician")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addTechnicianDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTechnicianById_WhenValidId_ShouldReturnTechnician() throws Exception {
        // Given
        when(technicianService.getTechnicianByUuid(technicianId)).thenReturn(technicianListDto);

        // When & Then
        mockMvc.perform(get("/technician/get/by/{id}", technicianId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("fetched successfully!"))
                .andExpect(jsonPath("$.data.id").value(technicianId))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    void getTechnicianById_WhenInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(technicianService.getTechnicianByUuid(anyString()))
                .thenThrow(new CodeException("technician not Found!", ErrorCode.RECORD_NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/technician/get/by/{id}", "invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_WhenValidId_ShouldReturnSuccess() throws Exception {
        // Given
        when(technicianService.deleteById(technicianId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/technician/delete/by/id/{id}", technicianId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("technician deleted successfully"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void deleteById_WhenInvalidId_ShouldReturnErrorResponse() throws Exception {
        // Given
        when(technicianService.deleteById(anyString()))
                .thenThrow(new CodeException("technician not Found!", ErrorCode.RECORD_NOT_FOUND));

        // When & Then
        mockMvc.perform(delete("/technician/delete/by/id/{id}", "invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeStatus_WhenValidId_ShouldReturnSuccess() throws Exception {
        // Given
        when(technicianService.changeStatus(technicianId)).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/technician/change/status/{id}", technicianId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("technician activated Successfully!"));
    }

    @Test
    void changeStatus_WhenDeactivating_ShouldReturnDeactivationMessage() throws Exception {
        // Given
        when(technicianService.changeStatus(technicianId)).thenReturn(false);

        // When & Then
        mockMvc.perform(put("/technician/change/status/{id}", technicianId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("technician deactivated Successfully!"));
    }

    @Test
    void changeStatus_WhenInvalidId_ShouldReturnErrorResponse() throws Exception {
        // Given
        when(technicianService.changeStatus(anyString()))
                .thenThrow(new CodeException("technician not Found!", ErrorCode.RECORD_NOT_FOUND));

        // When & Then
        mockMvc.perform(put("/technician/change/status/{id}", "invalid-id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void technicianList_WhenValidRequest_ShouldReturnPaginatedList() throws Exception {
        // Given
        PageRequest.List listRequest = new PageRequest.List();
        listRequest.setPageNumber(0);
        listRequest.setPageSize(10);
        
        // Mock the service to return empty page for simplicity
        when(technicianService.getAllTechnician(any(PageRequest.List.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/technician/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(listRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}