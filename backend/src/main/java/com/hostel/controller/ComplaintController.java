package com.hostel.controller;

import com.hostel.dto.ComplaintDTO;
import com.hostel.dto.UpdateStatusRequest;
import com.hostel.entity.Category;
import com.hostel.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {
    
    @Autowired
    private ComplaintService complaintService;
    
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ComplaintDTO> createComplaint(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") Category category,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        
        ComplaintDTO complaint = complaintService.createComplaint(title, description, category, userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(complaint);
    }
    
    @GetMapping
    public ResponseEntity<List<ComplaintDTO>> getAllComplaints() {
        List<ComplaintDTO> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDTO> getComplaintById(@PathVariable Long id) {
        ComplaintDTO complaint = complaintService.getComplaintById(id);
        return ResponseEntity.ok(complaint);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ComplaintDTO> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        ComplaintDTO complaint = complaintService.updateComplaintStatus(id, request.getStatus());
        return ResponseEntity.ok(complaint);
    }
}
