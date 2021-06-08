package com.bousaid.quefaireaparis;

public class FavoriteItem {
    private String key_id;
    private String title;
    private String address;
    private String text;
    private String url;
    private String favoriteStatus;

    public FavoriteItem(){
    }

    public FavoriteItem(String key_id, String title, String address, String text, String url, String favoriteStatus) {
        this.key_id = key_id;
        this.title = title;
        this.address = address;
        this.text = text;
        this.url = url;
        this.favoriteStatus = favoriteStatus;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFavoriteStatus() {
        return favoriteStatus;
    }

    public void setFavoriteStatus(String favoriteStatus) {
        this.favoriteStatus = favoriteStatus;
    }
}
