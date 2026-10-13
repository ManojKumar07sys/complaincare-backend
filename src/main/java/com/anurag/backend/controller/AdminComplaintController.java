
package com.anurag.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anurag.backend.entity.Complaint;
import com.anurag.backend.service.ComplaintService;

@RestController
@RequestMapping("/api/admin/complaints")
public class AdminComplaintController {

    private final ComplaintService complaintService;

    private static final Set<String> ALLOWED_STATUSES =
            Set.of("PENDING", "IN_PROGRESS", "RESOLVED");

    public AdminComplaintController(
            ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateComplaint(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        Complaint complaint =
                complaintService.getComplaintById(id);

        if (complaint == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Complaint not found"
                    ));
        }

        String status = request.get("status");
        String resolutionNote = request.get("resolutionNote");

        if (status == null
                || !ALLOWED_STATUSES.contains(status)) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Status must be PENDING, IN_PROGRESS, or RESOLVED"
                    ));
        }

        if ("RESOLVED".equals(status)
                && (resolutionNote == null
                || resolutionNote.isBlank())) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Add a resolution note before resolving a complaint"
                    ));
        }

        complaint.setStatus(status);
        complaint.setResolutionNote(
                resolutionNote == null
                        ? null
                        : resolutionNote.trim()
        );

        Complaint updated =
                complaintService.updateComplaint(complaint);

        return ResponseEntity.ok(updated);
    }
}
