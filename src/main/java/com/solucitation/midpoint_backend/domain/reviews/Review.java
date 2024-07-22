package com.solucitation.midpoint_backend.domain.reviews;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeId;
    private String authorName;
    private String text;
    private Integer rating;

    public Review() {}

    public Review(String placeId, String authorName, String text, Integer rating) {
        this.placeId = placeId;
        this.authorName = authorName;
        this.text = text;
        this.rating = rating;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", placeId='" + placeId + '\'' +
                ", authorName='" + authorName + '\'' +
                ", text='" + text + '\'' +
                ", rating=" + rating +
                '}';
    }
}
