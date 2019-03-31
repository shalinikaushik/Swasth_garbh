package com.swasthgarbh.root.swasthgarbh;

import android.util.Log;

public class WholeImagesListClass {

    String extraComments, date, month, year, imgByte;
    Integer id;

    public WholeImagesListClass (String extraComments, String date, String imgByte, Integer id) {
        this.extraComments = extraComments;
        this.date = get_date_date(date);
        this.month = get_date_month(date);
        this.year = get_date_year(date);
        this.imgByte = imgByte;
        this.id = id;
    }

    public WholeImagesListClass (String extraComments, String date, Integer id) {
        this.extraComments = extraComments;
        this.date = get_date_date(date);
        this.month = get_date_month(date);
        this.year = get_date_year(date);
        this.id = id;
    }

    public Integer getId() { return id; }

    public String getExtraComments() {
        return extraComments;
    }

    public String getDateString(){
        return (date + "-" + month + "-" + year);
    }

    private String get_date_year(String date) {
        return date.split("-")[0];
    }

    public String getImgByte(){ return  imgByte;}

    private String get_date_month(String date) {
        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        int mon_int = Integer.parseInt(date.split("-")[1]);
        return months[mon_int];
    }

    private String get_date_date(String date) {
        return date.split("-")[2].split("T")[0];
    }

}
