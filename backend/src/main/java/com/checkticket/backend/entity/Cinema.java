package com.checkticket.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cinemas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_osm_id", nullable = false, unique = true)
    private String externalOsmId;

    @Column(nullable = false)
    private String name;

    @Column
    private String city;

    @Column
    private String zone;

    @Column
    private Double latitude;

    @Column
    private Double longitude;
}
