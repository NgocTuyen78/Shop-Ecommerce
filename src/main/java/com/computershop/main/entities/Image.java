package com.computershop.main.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class Image {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    public Image() {}
    
    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Integer getImageId() {return imageId;}
    public String getImageUrl() {return imageUrl;}

    public void setImageId(Integer imageId) {this.imageId = imageId;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + imageId +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}