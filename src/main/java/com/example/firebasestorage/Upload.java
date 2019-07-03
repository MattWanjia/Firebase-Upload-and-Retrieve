package com.example.firebasestorage;

public class Upload {
    private String imageUrl;
    private String name;
    private String country;

    public Upload() {
    }

    public Upload(String imageUrl, String name, String country) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.country = country;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
