package com.solucitation.midpoint_backend.domain.FavPlace.api;

import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavPlaceResponse {
    private FavPlace.AddrType addrType;
    private String addr;

    public FavPlaceResponse(FavPlace.AddrType addrType, String addr) {
        this.addrType = addrType;
        this.addr = addr;
    }
}
