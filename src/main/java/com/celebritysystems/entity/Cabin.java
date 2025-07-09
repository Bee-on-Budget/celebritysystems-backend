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

    @Column(name = "height_quantity", nullable = false)
    private Long heightQuantity;

    @Column(name = "width_quantity", nullable = false)
    private Long widthQuantity;

    private Double height;

    private Double width;

    @Column(name = "is_height", nullable = true)
    private Boolean isHeight;

    @Column(name = "is_width", nullable = true)
    private Boolean isWidth;

    //    @OneToOne(mappedBy = "cabin", cascade = CascadeType.ALL)
//    private Module module;
    @OneToOne
    @JoinColumn(name = "module_id",nullable = true)
    private Module module;
}
