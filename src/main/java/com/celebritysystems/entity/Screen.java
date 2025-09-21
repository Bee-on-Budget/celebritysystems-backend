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

    //    private String pixelScreen;
    @Column(name = "pixel_pitch_width")
    private Double pixelPitchWidth;
    @Column(name = "pixel_pitch_height")
    private Double pixelPitchHeight;

    @Column(name = "screen_width")
    private Double screenWidth;
    @Column(name = "screen_height")
    private Double screenHeight;

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

    //power
    @Column(name = "main_powe_cable")
    private String mainPowerCable;
    @Column(name = "main_power_cable_quantity")
    private Long mainPowerCableQuantity;
    @Column(name = "spare_main_power_cable_quantity")
    private Long spareMainPowerCableQuantity;

    @Column(name = "loop_power_cable")
    private String loopPowerCable;
    @Column(name = "loop_power_cable_quantity")
    private Long loopPowerCableQuantity;
    @Column(name = "spare_loop_power_cable_quantity")
    private Long spareLoopPowerCableQuantity;

    //data
    @Column(name = "main_data_cable")
    private String mainDataCable;
    @Column(name = "main_data_cable_quantity")
    private Long mainDataCableQuantity;
    @Column(name = "spare_main_data_cable_quantity")
    private Long spareMainDataCableQuantity;

    @Column(name = "loop_data_cable")
    private String loopDataCable;
    @Column(name = "loop_data_cable_quantity")
    private Long loopDataCableQuantity;
    @Column(name = "spare_loop_data_cable_quantity")
    private Long spareLoopDataCableQuantity;

    @Column(name = "media", nullable = true)
    private String media;

    @Column(name = "media_quantity", nullable = true)
    private Long mediaQuantity;

    @Column(name = "spare_media_quantity", nullable = true)
    private Long spareMediaQuantity;

    @Column(name = "fan", nullable = true)
    private String fan;

    @Column(name = "fan_quantity", nullable = true)
    private Long fanQuantity;

    @Column(name = "hub", nullable = true)
    private String hub;

    @Column(name = "hub_quantity", nullable = true)
    private Long hubQuantity;

    @Column(name = "spare_hub_quantity", nullable = true)
    private Long spareHubQuantity;

    @Column(name = "resolution")
    private String resolution;

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
