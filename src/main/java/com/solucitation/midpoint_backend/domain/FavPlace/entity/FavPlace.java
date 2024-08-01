package com.solucitation.midpoint_backend.domain.FavPlace.entity;

import com.solucitation.midpoint_backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "fav_place", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "addr_type"})
})
public class FavPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fav_place_id")
    private Long favPlaceId;

    @Column(name = "addr", nullable = false, length = 255)
    private String addr;

    @Column(name = "latitude", nullable = false)
    private Float latitude;

    @Column(name = "longitude", nullable = false)
    private Float longitude;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "addr_type", nullable = false)
    private AddrType addrType;

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public enum AddrType {
        HOME, WORK
    }
}