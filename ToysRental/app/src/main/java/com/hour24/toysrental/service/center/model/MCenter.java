package com.hour24.toysrental.service.center.model;

import java.io.Serializable;

/**
 * Created by 장세진 on 2016-10-19.
 */
public class MCenter implements Serializable {

    private String centerSeq;
    private String centerName;
    private String placeName;
    private String addrStreet;
    private String addrLocal;
    private String addrEtc;
    private String tel;
    private String url;
    private String urlToySearch;
    private String latitude;
    private String longitude;

    // 이하 필터
    private String holiDay;
    private String ableDay;
    private String priceMax;
    private String priceMin;
    private String priceJoin;
    private String rentalMax;
    private String rentalMin;
    private String timeMax;
    private String timeMin;
    private String note;

    public String getCenterSeq() {
        return centerSeq;
    }

    public void setCenterSeq(String centerSeq) {
        this.centerSeq = centerSeq;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddrStreet() {
        return addrStreet;
    }

    public void setAddrStreet(String addrStreet) {
        this.addrStreet = addrStreet;
    }

    public String getAddrLocal() {
        return addrLocal;
    }

    public void setAddrLocal(String addrLocal) {
        this.addrLocal = addrLocal;
    }

    public String getAddrEtc() {
        return addrEtc;
    }

    public void setAddrEtc(String addrEtc) {
        this.addrEtc = addrEtc;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToySearch() {
        return urlToySearch;
    }

    public void setUrlToySearch(String urlToySearch) {
        this.urlToySearch = urlToySearch;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getHoliDay() {
        return holiDay;
    }

    public void setHoliDay(String holiDay) {
        this.holiDay = holiDay;
    }

    public String getAbleDay() {
        return ableDay;
    }

    public void setAbleDay(String ableDay) {
        this.ableDay = ableDay;
    }

    public String getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(String priceMax) {
        this.priceMax = priceMax;
    }

    public String getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(String priceMin) {
        this.priceMin = priceMin;
    }

    public String getPriceJoin() {
        return priceJoin;
    }

    public void setPriceJoin(String priceJoin) {
        this.priceJoin = priceJoin;
    }

    public String getRentalMax() {
        return rentalMax;
    }

    public void setRentalMax(String rentalMax) {
        this.rentalMax = rentalMax;
    }

    public String getRentalMin() {
        return rentalMin;
    }

    public void setRentalMin(String rentalMin) {
        this.rentalMin = rentalMin;
    }

    public String getTimeMax() {
        return timeMax;
    }

    public void setTimeMax(String timeMax) {
        this.timeMax = timeMax;
    }

    public String getTimeMin() {
        return timeMin;
    }

    public void setTimeMin(String timeMin) {
        this.timeMin = timeMin;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

