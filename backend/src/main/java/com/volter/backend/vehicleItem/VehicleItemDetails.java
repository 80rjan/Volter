package com.volter.backend.vehicleItem;

import com.volter.backend.item.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        indexes = {
                @Index(name = "idx_vehicle_item_details_brand", columnList = "brand"),
                @Index(name = "idx_vehicle_item_details_model", columnList = "model"),
                @Index(name = "idx_vehicle_item_details_vehicle_type", columnList = "vehicleType"),
                @Index(name = "idx_vehicle_item_details_year", columnList = "year"),
                @Index(name = "idx_vehicle_item_details_registration_number", columnList = "registrationNumber"),
                @Index(name = "idx_vehicle_item_details_service_history_available", columnList = "serviceHistoryAvailable"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vehicle_item_details_registration_number", columnNames = {"registrationNumber"})
        }
)
public class VehicleItemDetails {

    @Id
    private Long itemId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vehicle_item_details_item"))
    private Item item;

    @NotNull(message = "Vehicle brand is required")
    @Column(nullable = false)
    private String brand;

    @NotNull(message = "Vehicle model is required")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Vehicle type is required")
    @Column(nullable = false)
    private String vehicleType;

    @NotNull(message = "Vehicle year is required")
    @Column(nullable = false)
    private Integer year;

    @NotNull(message = "Vehicle registration number is required")
    @Column(nullable = false)
    private String registrationNumber;

    @NotNull(message = "Vehicle mileage is required")
    @Column(nullable = false)
    private Integer mileage;

    @NotNull(message = "Vehicle is service history available is required")
    @Column(nullable = false)
    private boolean serviceHistoryAvailable;

    @Column(nullable = true)
    private LocalDate lastServiceDate;

    @NotNull(message = "Vehicle registration expiry date is required")
    @Column(nullable = false)
    private LocalDate registrationExpiryDate;

    @NotNull(message = "Vehicle number of keys is required")
    @Column(nullable = false)
    private Integer numberOfKeys;
}
