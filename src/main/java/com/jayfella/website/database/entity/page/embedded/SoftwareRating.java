package com.jayfella.website.database.entity.page.embedded;

import javax.persistence.*;

@Embeddable
public class SoftwareRating {

    private int oneStarCount = 0;
    private int twoStarCount = 0;
    private int threeStarCount = 0;
    private int fourStarCount = 0;
    private int fiveStarCount = 0;

    private int ratingCount = 0;
    private float averageRating = 0;


    public int getOneStarCount() { return oneStarCount; }
    public void setOneStarCount(int oneStarCount) { this.oneStarCount = oneStarCount; }

    public int getTwoStarCount() { return twoStarCount; }
    public void setTwoStarCount(int twoStarCount) { this.twoStarCount = twoStarCount; }

    public int getThreeStarCount() { return threeStarCount; }
    public void setThreeStarCount(int threeStarCount) { this.threeStarCount = threeStarCount; }

    public int getFourStarCount() { return fourStarCount; }
    public void setFourStarCount(int fourStarCount) { this.fourStarCount = fourStarCount; }

    public int getFiveStarCount() { return fiveStarCount; }
    public void setFiveStarCount(int fiveStarCount) { this.fiveStarCount = fiveStarCount; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public float getAverageRating() { return averageRating; }
    public void setAverageRating(float averageRating) { this.averageRating = averageRating; }

    @Transient
    public void addRating(int rating) {

        switch (rating) {
            case 1: setOneStarCount(getOneStarCount() + 1); break;
            case 2: setTwoStarCount(getTwoStarCount() + 1); break;
            case 3: setThreeStarCount(getThreeStarCount() + 1); break;
            case 4: setFourStarCount(getFourStarCount() + 1); break;
            case 5: setFiveStarCount(getFiveStarCount() + 1); break;
        }

        recalcRatings();
    }

    @Transient
    public void removeRating(int rating) {

        switch (rating) {
            case 1: setOneStarCount(getOneStarCount() - 1); break;
            case 2: setTwoStarCount(getTwoStarCount() - 1); break;
            case 3: setThreeStarCount(getThreeStarCount() - 1); break;
            case 4: setFourStarCount(getFourStarCount() - 1); break;
            case 5: setFiveStarCount(getFiveStarCount() - 1); break;
        }

        recalcRatings();
    }

    private void recalcRatings() {
        int totalRatings = getOneStarCount();
        totalRatings += getTwoStarCount();
        totalRatings += getThreeStarCount();
        totalRatings += getFourStarCount();
        totalRatings += getFiveStarCount();

        int averageRating = 0;

        if (totalRatings > 0) {
            averageRating += (getOneStarCount());
            averageRating += (getTwoStarCount() * 2);
            averageRating += (getThreeStarCount() * 3);
            averageRating += (getFourStarCount() * 4);
            averageRating += (getFiveStarCount() * 5);

            averageRating /= totalRatings;
        }

        setRatingCount(totalRatings);
        setAverageRating(averageRating);
    }
    
}
