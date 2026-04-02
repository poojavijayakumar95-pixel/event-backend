package com.eventmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "speakers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 200)
    private String title;

    @Column(length = 200)
    private String organization;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 500)
    private String photoUrl;

    @Column(length = 200)
    private String linkedinUrl;

    @Column(length = 200)
    private String websiteUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "speakers")
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
