package org.ats.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {
    @Id
    private String id;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String jobId;

    // Lưu trữ URL của file CV được lưu treen cloud
    private String cvFileId;

    private LocalDateTime appliedAt;
}
