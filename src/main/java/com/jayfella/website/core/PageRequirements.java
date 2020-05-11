package com.jayfella.website.core;

import java.math.BigDecimal;

public class PageRequirements {

    public static final int TITLE_MIN_LENGTH = 6;
    public static final int TITLE_MAX_LENGTH = 64;

    public static final int SHORTDESC_MIN_LENGTH = 10;
    public static final int DESC_MIN_LENGTH = 50;

    public static final int VERSION_MIN_LENGTH = 5;
    public static final int VIDEOS_MIN_COUNT = 0;
    public static final int IMAGES_MIN_COUNT = 1;

    public static final int MAX_SCREENSHOTS = 9;
    public static final int MAX_POTENTIAL_ASSETS = 5;

    public static final BigDecimal PRICE_MINIMUM = new BigDecimal(5.0);

    public static final int MIN_SEARCH_LENGTH = 3;

}
