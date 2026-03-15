package com.volter.backend.staff;

import com.volter.backend.expense.Expense;
import com.volter.backend.notification.Notification;
import com.volter.backend.monthlyReport.MonthlyReport;
import com.volter.backend.pawnEvent.PawnEvent;
import com.volter.backend.staff.enums.StaffRole;
import com.volter.backend.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        indexes = {
                @Index(name = "idx_staff_username", columnList = "username"),
                @Index(name = "idx_staff_embg", columnList = "embg"),
                @Index(name = "idx_staff_phone_number", columnList = "phoneNumber"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_staff_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_staff_embg", columnNames = "embg"),
                @UniqueConstraint(name = "uk_staff_phone_number", columnNames = "phoneNumber")
        }
)
public class Staff implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Staff role is required")
    @Column(nullable = false)
    private StaffRole role;

    @NotNull(message = "Staff name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Staff username is required")
    @Column(nullable = false)
    private String username;

    @NotNull(message = "Staff password hash is required")
    @Column(nullable = false)
    private String passwordHash;

    @NotNull(message = "Staff embg is required")
    @Column(columnDefinition = "CHAR(13)", nullable = false)
    private String embg;

    @NotNull(message = "Staff phone number is required")
    @Column(nullable = false)
    private String phoneNumber;

    @NotNull(message = "Staff created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Staff updated at is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @NotNull(message = "Staff is deleted is required")
    @Column(nullable = false)
    private boolean deleted;

    @Builder.Default
    @OneToMany(mappedBy = "staff", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = true, foreignKey = @ForeignKey(name = "fk_staff_manager"))
    private Staff manager;

    @Builder.Default
    @OneToMany(mappedBy = "manager")
    private List<Staff> subordinates = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "staff", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Expense> expenses = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "manager", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Notification> notifications = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "manager", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<MonthlyReport> monthlyReports = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "staff", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<PawnEvent> pawnEvents = new ArrayList<>();

    // USER DETAILS METHODS
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STAFF"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        deleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
