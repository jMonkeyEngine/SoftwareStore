package com.jayfella.website.controller.http;

import com.jayfella.website.config.external.ServerConfig;
import com.jayfella.website.core.StoreHtmlFilePaths;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.repository.page.LivePageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
public class IndexPageController {

    @Autowired private LivePageRepository livePageRepository;

    /**
     * AUTH: PUBLIC
     * Displays the store index page.
     */
    @GetMapping
    public String index() {
        return StoreHtmlFilePaths.Store.INDEX.getHtmlFilePath();
    }

    /**
     * AUTH: PUBLIC
     * Displays a live page.
     */
    @GetMapping(value = "/{pageId}")
    public String viewAsset(Model model, HttpServletResponse response, @PathVariable("pageId") String pageId) throws IOException {

        LivePage livePage = livePageRepository.findById(pageId).orElse(null);

        if (livePage == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), "The requested page could not be found.");
            return null;
        }

        model.addAttribute("pageId", livePage.getId());
        model.addAttribute("assetType", PageState.Live);
        model.addAttribute("preview", false);

        String previewImageId = livePage.getMediaLinks().getImageIds().split(",")[0];

        // for previews, we need to transfer the preview data.
        model.addAttribute("previewTitle", livePage.getDetails().getTitle());
        model.addAttribute("previewDescription", livePage.getDetails().getShortDescription());
        model.addAttribute("previewUrl", ServerConfig.getInstance().getFullUrl()+"/" + livePage.getId());
        model.addAttribute("previewImage", ServerConfig.getInstance().getFullUrl()+"/image/" + previewImageId + ".jpg");

        return StoreHtmlFilePaths.Store.VIEW_PAGE.getHtmlFilePath();
    }

}
