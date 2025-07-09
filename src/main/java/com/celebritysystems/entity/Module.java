package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "module")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "batch_number")
    private String batchNumber;

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

//    @OneToOne(mappedBy = "module", cascade = CascadeType.ALL)
//    private Screen screen;
}
