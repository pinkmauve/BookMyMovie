package com.vaibhavsood.business.domain;

import java.util.Date;

public class MovieScreening {
    private String movieName;
    private long theatreId;
    private String theatreName;
    private String theatreCity;
    private String screeningDate;
    private String screeningTime;

    public String getScreeningDate() {
        return screeningDate;
    }

    public void setScreeningDate(String screeningDate) {
        this.screeningDate = screeningDate;
    }

    public String getScreeningTime() {
        return screeningTime;
    }

    public void setScreeningTime(String screeningTime) {
        this.screeningTime = screeningTime;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public long getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(long theatreId) {
        this.theatreId = theatreId;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
    }

    public String getTheatreCity() {
        return theatreCity;
    }

    public void setTheatreCity(String theatreCity) {
        this.theatreCity = theatreCity;
    }
}
