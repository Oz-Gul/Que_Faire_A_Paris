package com.bousaid.quefaireaparisv2;

public class Activite {
    private String text;
    private String url;

    public Activite() {
    }

    public Activite(String text, String url) {
        this.text = text;
        this.url = url;
    }

    public void setText(String text) {
        this.text=text;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}