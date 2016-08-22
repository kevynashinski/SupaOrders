package com.ryletech.supaorders.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sydney on 6/26/2016.
 */
public class SuperMarket implements Parcelable {

    private String supermarketId,placeId,placeName,reference,icon,vicinity;

    private Double latitude,longitude;

    public String getSupermarketId() {
        return supermarketId;
    }

    public void setSupermarketId(String supermarket_id) {
        this.supermarketId = supermarket_id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return "Id= "+getSupermarketId()
                +" Place_id= "+getPlaceId()
                +" Place_name= "+getPlaceName()
                +" Reference= "+getReference()
                +" Icon= "+getIcon()
                +" Vicinity= "+getVicinity()
                +" Latitude= "+getLatitude()
                +" Longitude= "+getLongitude();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.supermarketId);
        dest.writeString(this.placeId);
        dest.writeString(this.placeName);
        dest.writeString(this.reference);
        dest.writeString(this.icon);
        dest.writeString(this.vicinity);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    public SuperMarket() {
    }

    private SuperMarket(Parcel in) {
        this.supermarketId = in.readString();
        this.placeId = in.readString();
        this.placeName = in.readString();
        this.reference = in.readString();
        this.icon = in.readString();
        this.vicinity = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Creator<SuperMarket> CREATOR = new Creator<SuperMarket>() {
        @Override
        public SuperMarket createFromParcel(Parcel source) {
            return new SuperMarket(source);
        }

        @Override
        public SuperMarket[] newArray(int size) {
            return new SuperMarket[size];
        }
    };
}
