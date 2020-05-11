package com.jayfella.website.core.page;

import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.entity.page.stages.PageAmendment;
import com.jayfella.website.database.entity.page.stages.PageDraft;

public enum PageState {

    Draft,
    Live,
    Amendment;

    public static PageState fromPage(StorePage page) {

        if (page instanceof PageDraft) return PageState.Draft;
        else if (page instanceof LivePage) return PageState.Live;
        else if (page instanceof PageAmendment) return PageState.Amendment;

        return null;
    }

}
