package com.solucitation.midpoint_backend.domain.FavPlace.api;

import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavPlaceResponse {
    private Long favPlaceId;
    private String addr;
    private String addrType;

    public FavPlaceResponse(Long favPlaceId, String addr, String addrType) {
        this.favPlaceId = favPlaceId;
        this.addr = addr;
        this.addrType = addrType;
    }

    public FavPlaceResponse(FavPlace.AddrType addrType, String addr) {
        this.addrType = addrType.name();
        this.addr = addr;
    }
}
