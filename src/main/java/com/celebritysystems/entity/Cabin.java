package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cabin")
public class Cabin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cabin_name")
    private String cabinName;

    @Column(name = "quantity", nullable = true)
    private Long quantity;

    @Column(name = "cabins_by_height", nullable = false)
    private Long cabinsByHeight;

    @Column(name = "cabins_by_width", nullable = false)
    private Long cabinsByWidth;

    private Double pixelHeight;

    private Double pixelWidth;

//    @Column(name = "is_height", nullable = true, columnDefinition = "TINYINT(1)")
//    private Boolean isHeight;
//
//    @Column(name = "is_width", nullable = true, columnDefinition = "TINYINT(1)")
//    private Boolean isWidth;

    @OneToOne
    @JoinColumn(name = "module_id", nullable = true)
    private Module module;
}
