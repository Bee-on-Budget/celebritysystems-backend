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

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    private Double height;

    private Double width;

    //    @OneToOne(mappedBy = "cabin", cascade = CascadeType.ALL)
//    private Module module;
    @OneToOne
    @JoinColumn(name = "module_id")
    private Module module;
}
