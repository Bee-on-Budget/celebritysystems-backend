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

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    private Double height;

    private Double width;

    private String type;

//    @OneToOne(mappedBy = "cabin", cascade = CascadeType.ALL)
//    private Screen screen;
}
