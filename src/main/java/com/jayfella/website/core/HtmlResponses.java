package com.jayfella.website.core;

import com.jayfella.website.core.page.PageState;

import static com.jayfella.website.core.ResponseStrings.*;

public class HtmlResponses {

    public static String pageNotFound(PageState pageState, String pageId) {
        return String.format(PAGE_NOT_FOUND, pageState, pageId);
    }


}
