package com.volter.backend.employee;

import com.volter.backend.expense.Expense;
import com.volter.backend.manager.Manager;
import com.volter.backend.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_employee_username", columnList = "username"),
                @Index(name = "idx_employee_embg", columnList = "embg"),
                @Index(name = "idx_employee_phone_number", columnList = "phoneNumber"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_employee_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_employee_embg", columnNames = "embg"),
                @UniqueConstraint(name = "uk_employee_phone_number", columnNames = "phoneNumber")
        }
)
public class Employee implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "Employee name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Employee username is required")
    @Column(nullable = false)
    private String username;

    @NotNull(message = "Employee password hash is required")
    @Column(nullable = false)
    private String passwordHash;

    @NotNull(message = "Employee embg is required")
    @Column(columnDefinition = "CHAR(13)", nullable = false)
    private String embg;

    @NotNull(message = "Employee phone number is required")
    @Column(nullable = false)
    private String phoneNumber;

    @NotNull(message = "Employee created at is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Employee updated at is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @NotNull(message = "Employee is deleted is required")
    @Column(nullable = false)
    private boolean deleted;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Transaction> transactions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false, foreignKey = @ForeignKey(name = "fk_employee_manager"))
    private Manager manager;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Expense> expenses;

    // USER DETAILS METHODS
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
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
