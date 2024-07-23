package com.solucitation.midpoint_backend.domain.favorites.service;

import com.solucitation.midpoint_backend.domain.favorites.entity.FavoritePlace;
import com.solucitation.midpoint_backend.domain.favorites.entity.FavoriteFriend;
import com.solucitation.midpoint_backend.domain.favorites.entity.User;
import com.solucitation.midpoint_backend.domain.favorites.repository.FavoritePlaceRepository;
import com.solucitation.midpoint_backend.domain.favorites.repository.FavoriteFriendRepository;
import com.solucitation.midpoint_backend.domain.favorites.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FavoritesService {

    private final FavoritePlaceRepository favoritePlaceRepository;
    private final FavoriteFriendRepository favoriteFriendRepository;
    private final UserRepository userRepository;

    public FavoritesService(FavoritePlaceRepository favoritePlaceRepository,
                            FavoriteFriendRepository favoriteFriendRepository,
                            UserRepository userRepository) {
        this.favoritePlaceRepository = favoritePlaceRepository;
        this.favoriteFriendRepository = favoriteFriendRepository;
        this.userRepository = userRepository;
    }

    public void addFavoritePlace(Long userId, String placeName, String placeLocation) {
        validateUser(userId);

        if (placeName == null || placeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Place name cannot be empty");
        }

        if (placeLocation == null || placeLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Place location cannot be empty");
        }

        FavoritePlace favoritePlace = FavoritePlace.builder()
                .user(userRepository.getReferenceById(userId))
                .placeName(placeName)
                .placeLocation(placeLocation)
                .build();

        favoritePlaceRepository.save(favoritePlace);
    }

    public void addFavoriteFriend(Long userId, Long friendId) {
        validateUser(userId);
        validateUser(friendId);

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("User cannot add themselves as a favorite friend.");
        }

        FavoriteFriend favoriteFriend = FavoriteFriend.builder()
                .user(userRepository.getReferenceById(userId))
                .friend(userRepository.getReferenceById(friendId))
                .build();

        favoriteFriendRepository.save(favoriteFriend);
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }
    }
}


