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

    @Enumerated(EnumType.STRING)
    private SolutionTypeInScreen solutionType;

    private String pixelScreen;

    private String batchScreen;

    @Column(length = 1000)
    private String description;

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

    @Column(name = "fan",nullable = true)
    private String fan;

    @Column(name = "fan_quantity",nullable = true)
    private Long fanQuantity;

    @Column(name = "hub",nullable = true)
    private String hub;

    @Column(name = "hub_quantity",nullable = true)
    private Long hubQuantity;

    @Column(name = "spare_hub_quantity",nullable = true)
    private Long spareHubQuantity;

    @Column(name = "resolution")
    private Double resolution;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "connection_file_url")
    private String connectionFileUrl;
    
    @Column(name = "connection_file_name")
    private String connectionFileName;

    @Column(name = "config_file_url")
    private String configFileUrl;
    
    @Column(name = "config_file_name")
    private String configFileName;

    @Column(name = "version_file_url")
    private String versionFileUrl;
    
    @Column(name = "version_file_name")
    private String versionFileName;


    @OneToMany
    @JoinColumn(name = "screen_id", nullable = true)
    private List<Module> moduleList;

    @OneToMany
    @JoinColumn(name = "screen_id", nullable = true)
    private List<Cabin> cabinList;

    //test
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