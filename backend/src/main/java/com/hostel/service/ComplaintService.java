package com.hostel.service;

import com.hostel.dto.ComplaintDTO;
import com.hostel.dto.UserDTO;
import com.hostel.entity.Category;
import com.hostel.entity.Complaint;
import com.hostel.entity.Status;
import com.hostel.entity.User;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComplaintService {
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserService userService;
    
    private static final String UPLOAD_DIR = "uploads/";
    
    public ComplaintDTO createComplaint(String title, String description, Category category, 
                                       Long userId, MultipartFile file) throws IOException {
        // Get user
        User user = userService.getUserById(userId);
        
        // Create complaint
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setCategory(category);
        complaint.setStatus(Status.OPEN);
        complaint.setRaisedBy(user);
        
        // Auto-assign based on category
        complaint.setAssignedTo(getAssignedPerson(category));
        
        // Handle file upload
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            complaint.setAttachmentUrl("/uploads/" + fileName);
        }
        
        Complaint savedComplaint = complaintRepository.save(complaint);
        return convertToDTO(savedComplaint);
    }
    
    public List<ComplaintDTO> getAllComplaints() {
        return complaintRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ComplaintDTO getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        return convertToDTO(complaint);
    }
    
    public ComplaintDTO updateComplaintStatus(Long id, Status status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        complaint.setStatus(status);
        Complaint updatedComplaint = complaintRepository.save(complaint);
        return convertToDTO(updatedComplaint);
    }
    
    private String getAssignedPerson(Category category) {
        return switch (category) {
            case CARPENTRY -> "Ram";
            case RAGGING -> "Shyam";
            case ELECTRICAL -> "Electric Team";
            case PLUMBING -> "Plumber Team";
        };
    }
    
    private String saveFile(MultipartFile file) throws IOException {
        // Create uploads directory if it doesn't exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
        String fileName = UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());
        
        return fileName;
    }
    
    private ComplaintDTO convertToDTO(Complaint complaint) {
        ComplaintDTO dto = new ComplaintDTO();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCategory(complaint.getCategory());
        dto.setStatus(complaint.getStatus());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setAttachmentUrl(complaint.getAttachmentUrl());
        dto.setAssignedTo(complaint.getAssignedTo());
        
        // Convert user to DTO
        User user = complaint.getRaisedBy();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setRole(user.getRole());
        dto.setRaisedBy(userDTO);
        
        return dto;
    }
}
