package com.socks.selectcity.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CityBean implements Parcelable {

    private String id;
    private String name;
    private String sortLetters;
    private String level;
    private String wepiao_id;
    private String parent_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getWepiao_id() {
        return wepiao_id;
    }

    public void setWepiao_id(String wepiao_id) {
        this.wepiao_id = wepiao_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.sortLetters);
        dest.writeString(this.level);
        dest.writeString(this.wepiao_id);
        dest.writeString(this.parent_id);
    }

    public CityBean() {
    }

    protected CityBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.sortLetters = in.readString();
        this.level = in.readString();
        this.wepiao_id = in.readString();
        this.parent_id = in.readString();
    }

    public static final Creator<CityBean> CREATOR = new Creator<CityBean>() {
        public CityBean createFromParcel(Parcel source) {
            return new CityBean(source);
        }

        public CityBean[] newArray(int size) {
            return new CityBean[size];
        }
    };
}
