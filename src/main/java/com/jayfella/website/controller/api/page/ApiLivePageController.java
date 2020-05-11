package com.jayfella.website.controller.api.page;

import com.jayfella.website.core.ApiResponses;
import com.jayfella.website.core.PageRequirements;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.core.page.SoftwareType;
import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.ReviewRepository;
import com.jayfella.website.database.repository.UserRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import com.jayfella.website.http.request.SimplePageRequest;
import com.jayfella.website.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.jayfella.website.core.ServerAdvice.KEY_USER;

/**
 * JSON-ONLY
 * An endpoint that returns LIVE asset collections based on various filters.
 */
@RestController
@RequestMapping("/api/page/")
public class ApiLivePageController {

    @Autowired private PageDraftRepository draftRepository;
    @Autowired private LivePageRepository livePageRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private ReviewRepository reviewRepository;

    @Autowired private UserRepository userRepository;
    @Autowired private PageService pageService;

    @GetMapping("/{assetId}")
    public ResponseEntity<?> getPage(@PathVariable("assetId") String assetId) {

        LivePage livePage = livePageRepository.findById(assetId).orElse(null);

        if (livePage == null) {
            return ApiResponses.pageNotFound(PageState.Live, assetId);
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .body(livePage);
    }

    // ALL returns the top ten highest rated assets.
    @GetMapping("/highest-rated/")
    public Iterable<LivePage> getHighestRated() {

        Pageable sortByRating = PageRequest.of(
                0,
                10,
                Sort.by("ratings")
                        .descending());

        return livePageRepository.findAll(sortByRating);

    }

    // ALL search by title, returns 10 results
    @GetMapping("/search/title/{title}")
    public ResponseEntity<?> searchByTitle(@PathVariable("title") String title) {


        if (title.length() < PageRequirements.MIN_SEARCH_LENGTH) {
            return ApiResponses.searchTermTooShort();
        }

        Iterable<LivePage> foundItems = livePageRepository.findByDetailsTitleContaining(title, PageRequest.of(0, 10));

        return ResponseEntity.ok()
                .body(foundItems);
    }

    // ALL search by title/page/size (limit size (amount per page) = 50)
    @GetMapping("/search/title/{title}/{page}/{size}")
    public ResponseEntity<?> searchByTitlePaged(@PathVariable("title") String title,
                                             @PathVariable("page") int page,
                                             @PathVariable("size") int size) {

        if (title.length() < PageRequirements.MIN_SEARCH_LENGTH) {
            return ApiResponses.searchTermTooShort();
        }

        if (size > 50) {
            size = 50;
        }

        Iterable<LivePage> foundItems = livePageRepository.findByDetailsTitleContaining(title, PageRequest.of(page, size));

        return ResponseEntity.ok()
                .body(foundItems);
    }

    /* removed because they should use the search api
    @GetMapping("/search/tag/{tag}")
    public ResponseEntity searchByTag(@PathVariable("tag") String tag) {

        if (tag.length() < PageRequirements.MIN_SEARCH_LENGTH) {
            return ApiResponses.searchTermTooShort();
        }

        Iterable<LivePage> foundItems = livePageRepository.findByDetailsTagsContainingIgnoreCase(tag);

        return ResponseEntity.ok()
                .body(foundItems);
    }

    @GetMapping("/search/author/{author}")
    public ResponseEntity searchByAuthor(@PathVariable("author") String author) {

        if (author.length() < PageRequirements.MIN_SEARCH_LENGTH) {
            return ApiResponses.searchTermTooShort();
        }

        Iterable<LivePage> foundItems = livePageRepository.findByOwnerUsernameContainingIgnoreCase(author);
        return ResponseEntity.ok()
                .body(foundItems);
    }

    @GetMapping("/search/user/{userId}")
    public ResponseEntity searchByUserId(@PathVariable("userId") long userId) {

        Iterable<LivePage> foundItems = livePageRepository.findByOwnerId(userId);

        return ResponseEntity.ok()
                .body(foundItems);
    }

     */

    @GetMapping("/top/")
    public ResponseEntity getTopAssets() {

        // I'm not sure how to optimize this. I want to choose a random
        // list of 10 assets but I don't want to hammer the database.
        // we could choose ten and keep them cached somewhere and change
        // them over time. I'm not certain about this....

        Map<String, Iterable<? extends LivePage>> assets = new HashMap<>();

        // showcase assets
        Iterable<LivePage> showcaseAssets = livePageRepository.getRandom(10);
        assets.put("showcase", showcaseAssets);
        // model.addAttribute("showcaseAssets", showcaseAssets);

        // highest rated
        Pageable highestRatedPageable = PageRequest.of(0, 10,
                Sort.by("rating.oneStarCount",
                        "rating.twoStarCount",
                        "rating.threeStarCount",
                        "rating.fourStarCount",
                        "rating.fiveStarCount")
                        .descending());

        Iterable<LivePage> highestRatedAssets = livePageRepository.findAll(highestRatedPageable);
        assets.put("highest_rated", highestRatedAssets);
        // model.addAttribute("highestRatedAssets", highestRatedAssets);


        // new additions
        Pageable newestAdditionsPageable = PageRequest.of(0, 10, Sort.by("dateCreated").descending());
        Iterable<LivePage> newAdditions = livePageRepository.findAll(newestAdditionsPageable);
        assets.put("new_additions", newAdditions);
        //model.addAttribute("newAdditions", newAdditions);

        // recently updated updated
        Pageable recentUpdatesPageable = PageRequest.of(0, 10, Sort.by("dateUpdated").descending());
        Iterable<LivePage> recentUpdates = livePageRepository.findAll(recentUpdatesPageable);
        assets.put("recently_updated", recentUpdates);
        // model.addAttribute("recentUpdates", recentUpdates);

        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

    @GetMapping("/forks/{assetId}")
    public ResponseEntity getForks(@PathVariable("assetId") String assetId) {

        LivePage livePage = livePageRepository.findById(assetId).orElse(null);

        if (livePage == null || livePage.getSoftwareType() == SoftwareType.Paid) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }

        Iterable<LivePage> otherForks = livePageRepository.findByOpenSourceDataForkRepositoryIgnoreCase(livePage.getOpenSourceData().getForkRepository());

        // remove this repository from the list
        Iterator<LivePage> iterator = otherForks.iterator();

        while (iterator.hasNext()) {
            LivePage other = iterator.next();

            if (other.equals(livePage)) {
                iterator.remove();
                break;
            }
        }

        return new ResponseEntity<>(otherForks, HttpStatus.OK);
    }

    @GetMapping("/dependencies/{pageId}")
    public ResponseEntity<Iterable<LivePage>> getPageStoreDependencies(@PathVariable("pageId") String pageId) {

        Iterable<LivePage> storeDependencies = livePageRepository.findByBuildDataStoreDependenciesContaining(pageId);
        return ResponseEntity.ok()
                .body(storeDependencies);
    }

    @GetMapping("/data/{pageId}")
    public ResponseEntity<?> getPageData(@PathVariable("pageId") String pageId) {

        LivePage livePage = livePageRepository.findById(pageId).orElse(null);

        if (livePage == null) {
            return ApiResponses.pageNotFound(PageState.Live, pageId);
        }

        Map<String, Iterable<LivePage>> data = new HashMap<>();

        // store dependencies: pages that this software depends on.
        if (livePage.getBuildData().getStoreDependencies().length() > 0) {
            String[] pages = livePage.getBuildData().getStoreDependencies().split(",");
            data.put("dependsOn", livePageRepository.findByIdIn(pages));
        }
        else {
            data.put("dependsOn", Collections.emptyList());
        }

        // forks: pages that fork this repository
        if (livePage.getSoftwareType() == SoftwareType.OpenSource || livePage.getSoftwareType() == SoftwareType.Sponsored) {
            data.put("forks", livePageRepository.findByOpenSourceDataForkRepositoryIgnoreCase(livePage.getOpenSourceData().getGitRepository()));
        }
        else {
            data.put("forks", Collections.emptyList());
        }

        // addons: pages that have listed this page as a store dependency.
        data.put("addons", livePageRepository.findByBuildDataStoreDependenciesContaining(pageId));

        return ResponseEntity.ok()
                .body(data);
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getUserStats(ModelMap model, @PathVariable("userId") long userId) {

        // User user = (User) model.get(KEY_USER);

        // if (user == null) {
            // return ApiResponses.notLoggedIn();
        // }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ApiResponses.userNotFound(-1);
        }

        Iterable<LivePage> livePages = livePageRepository.findByOwner(user);

        List<String> pageIds = StreamSupport.stream(livePages.spliterator(), false)
                .map(StorePage::getId)
                .collect(Collectors.toList());

        long reviewCount = reviewRepository.countByPageIdIn(pageIds);
        long pageCount = livePages.spliterator().getExactSizeIfKnown();

        final double averageRating;
        if (reviewCount > 0) {
            averageRating = StreamSupport.stream(livePages.spliterator(), false)
                    .mapToDouble(page -> page.getRating().getAverageRating())
                    .sum() / reviewCount;
        }
        else {
            averageRating = 0;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userProfile", user);
        // data.put("registerDate", user.getRegisterDate());
        data.put("pageCount", pageCount);
        data.put("reviewCount", reviewCount);
        data.put("averateRating", averageRating);

        return ResponseEntity.ok()
                .body(data);
    }

    @DeleteMapping
    public ResponseEntity deletePage(ModelMap model, @ModelAttribute @Valid SimplePageRequest deleteRequest, BindingResult bindingResult) {

        User user = (User) model.get(KEY_USER);

        if (user == null) {
            return ApiResponses.notLoggedIn();
        }

        if (bindingResult.hasErrors()) {
            return ApiResponses.badRequest(bindingResult);
        }

        if (!(user.isAdministrator() || user.isModerator())) {
            return ApiResponses.insufficientPermission();
        }

        LivePage livePage = livePageRepository.findById(deleteRequest.getPageId()).orElse(null);

        if (livePage == null) {
            return ApiResponses.pageNotFound(PageState.Live, deleteRequest.getPageId());
        }

        pageService.delete(livePage);

        return ApiResponses.pageDeleted(livePage);
    }
}
