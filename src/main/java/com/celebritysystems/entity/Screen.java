package com.celebritysystems.entity;

import com.celebritysystems.entity.enums.ScreenType;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "screen")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    private String location;

    private SolutionTypeInScreen solutionType;

    @Column(name = "power_supply")
    private String powerSupply;

    @Column(name = "power_supply_quantity")
    private Long powerSupplyQuantity;

    @Column(name = "spare_power_supply_quantity")
    private Long sparePowerSupplyQuantity;

    @Column(name = "receiving_card")
    private String receivingCard;

    @Column(name = "receiving_card_quantity")
    private Long receivingCardQuantity;

    @Column(name = "spare_receiving_card_quantity")
    private Long spareReceivingCardQuantity;

    @Column(name = "cable")
    private String cable;

    @Column(name = "cable_quantity")
    private Long cableQuantity;

    @Column(name = "spare_cable_quantity")
    private Long spareCableQuantity;

    @Column(name = "power_cable")
    private String powerCable;

    @Column(name = "power_cable_quantity")
    private Long powerCableQuantity;

    @Column(name = "spare_power_cable_quantity")
    private Long sparePowerCableQuantity;

    @Column(name = "data_cable")
    private String dataCable;

    @Column(name = "data_cable_quantity")
    private Long dataCableQuantity;

    @Column(name = "spare_data_cable_quantity")
    private Long spareDataCableQuantity;

    @Column(name = "media",nullable = true)
    private String media;

    @Column(name = "media_quantity",nullable = true)
    private Long mediaQuantity;

    @Column(name = "spare_media_quantity",nullable = true)
    private Long spareMediaQuantity;

    @Column(name = "fan")
    private String fan;

    @Column(name = "fan_quantity")
    private Long fanQuantity;

    @Column(name = "resolution")
    private Double resolution;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] connection;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] config;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] version;


    @OneToOne
    @JoinColumn(name = "module_id", nullable = true)
    private Module module;

    @OneToMany
    @JoinColumn(name = "screen_id", nullable = true)
    private List<Cabin> cabinList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Screen screen = (Screen) o;
        return id != null && id.equals(screen.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}