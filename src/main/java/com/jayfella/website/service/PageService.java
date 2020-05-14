package com.jayfella.website.service;

import com.jayfella.website.core.GitRepository;
import com.jayfella.website.core.PageRequirements;
import com.jayfella.website.core.VersionState;
import com.jayfella.website.core.page.PageState;
import com.jayfella.website.core.page.SoftwareType;
import com.jayfella.website.database.entity.Category;
import com.jayfella.website.database.entity.page.Editable;
import com.jayfella.website.database.entity.page.PageReview;
import com.jayfella.website.database.entity.page.StaffPageReview;
import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.database.entity.page.stages.LivePage;
import com.jayfella.website.database.entity.page.stages.PageAmendment;
import com.jayfella.website.database.entity.page.stages.PageDraft;
import com.jayfella.website.database.entity.user.User;
import com.jayfella.website.database.repository.CategoryRepository;
import com.jayfella.website.database.repository.ReviewRepository;
import com.jayfella.website.database.repository.StaffPageReviewRepository;
import com.jayfella.website.database.repository.page.LivePageRepository;
import com.jayfella.website.database.repository.page.PageAmendmentRepository;
import com.jayfella.website.database.repository.page.PageDraftRepository;
import com.jayfella.website.exception.InvalidImageException;
import com.jayfella.website.license.OpenSourceLicense;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PageService {

    @Autowired private PageDraftRepository draftRepository;
    @Autowired private LivePageRepository livePageRepository;
    @Autowired private PageAmendmentRepository amendmentRepository;

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ReviewRepository reviewRepository;

    @Autowired private ImageService imageService;
    @Autowired private StaffPageReviewRepository staffReviewRepository;

    /**
     * Determines whether a user is the owner of the page or an administrator.
     * @param user      the user to check.
     * @param storePage the page in question.
     * @return whether or not the specified user is either the owner of the page or an administrator.
     */
    public boolean isOwnerOrAdmin(User user, StorePage storePage) {
        return (!( storePage.getOwner().equals(user) || user.isAdministrator() ));
    }

    /**
     * Determines whether a user is the owner of the page or an administrator.
     * @param user      the user to check.
     * @param storePage the page in question.
     * @return whether or not the specified user is either the owner of the page or a moderator.
     */
    public boolean isOwnerOrModerator(User user, StorePage storePage) {
        return ( storePage.getOwner().equals(user) || user.isAdministrator() || user.isModerator() );
    }

    /**
     * Checks all values of the given page and determines if the page contains the required data to qualify for a staff review.
     * @param page the page to check.
     * @return a list of errors to return to the user, or an empty list if no errors are found.
     */
    public List<String> validatePage(StorePage page) {

        List<String> errors = new ArrayList<>();

        // DETAILS
        if (page.getDetails().getTitle().length() < PageRequirements.TITLE_MIN_LENGTH) {
            errors.add(String.format("Title must be at least %d characters in length.",
                    PageRequirements.TITLE_MIN_LENGTH));
        }

        if (page.getDetails().getShortDescription().length() < PageRequirements.SHORTDESC_MIN_LENGTH) {
            errors.add(String.format("Short Description must be at least %d characters in length.",
                    PageRequirements.SHORTDESC_MIN_LENGTH));
        }

        if (page.getDetails().getDescription().length() < PageRequirements.DESC_MIN_LENGTH) {
            errors.add(String.format("Description must be at least %d characters in length.",
                    PageRequirements.DESC_MIN_LENGTH));
        }

        // Category
        if (page instanceof Editable) {
            Editable editable = (Editable) page;

            if (editable.getCategoryId() == null) {
                errors.add("You must specify a category.");
            }
        }

        // VERSION
        if (page.getVersionData().getVersion().length() < PageRequirements.VERSION_MIN_LENGTH) {
            errors.add(String.format("Version must be at least %d characters in length (major.minor.patch)",
                    PageRequirements.VERSION_MIN_LENGTH));
        }

        // this is zero at the time of writing, but if we increase it one day, the check
        // will already be here.
        String videoIds = page.getMediaLinks().getVideoIds();
        if (videoIds != null && videoIds.length() > 0) {
            String[] splitIds = videoIds.split(",");
            if (splitIds.length < PageRequirements.VIDEOS_MIN_COUNT) {
                errors.add(String.format("You must have at least %d video(s).", PageRequirements.VIDEOS_MIN_COUNT));
            }
        }
        else {
            // if we required more than zero videos, we would return an error.
        }

        String imageIds = page.getMediaLinks().getImageIds();
        if (imageIds != null && imageIds.length() > 0) {
            String[] splitIds = imageIds.split(",");
            if (splitIds.length < PageRequirements.IMAGES_MIN_COUNT) {
                errors.add(String.format("You must have at least %d screenshot(s).", PageRequirements.IMAGES_MIN_COUNT));
            }
        }
        else {
            errors.add(String.format("You must have at least %d screenshot(s).", PageRequirements.IMAGES_MIN_COUNT));
        }

        if (page.getSoftwareType() == SoftwareType.OpenSource || page.getSoftwareType() == SoftwareType.Sponsored) {

            if (page.getOpenSourceData().getSoftwareLicense() == null) {
                errors.add("You must specify a Source Code license.");
            }

            if (page.getOpenSourceData().getMediaLicense() == null) {
                errors.add("You must specify a Media license.");
            }

            if (!GitRepository.startsWithAny(page.getOpenSourceData().getGitRepository())) {
                errors.add("You must specify a Git Repository URL.");
            }
        }
        else if (page.getSoftwareType() == SoftwareType.Paid) {

            if (page.getPaymentData().getPrice().compareTo(PageRequirements.PRICE_MINIMUM) < 0) {
                errors.add("You must set the price equal or higher than " + PageRequirements.PRICE_MINIMUM + ".");
            }
        }

        return errors;
    }

    private String getRequestParam(String parameter, MultipartHttpServletRequest request) {
        String value = request.getParameter(parameter);

        if (value != null) {
            value = value.trim();
        }
        else {
            value = "";
        }

        return value;
    }

    // trims tags and removes duplicates ignoring case.
    private String processStringArray(String existingArray, String parameter, MultipartHttpServletRequest request) {

        String[] reqTags = request.getParameterValues(parameter);

        if (reqTags == null) {
            reqTags = new String[0];
        }
        else {
            Arrays.stream(reqTags).forEach(tag -> tag = tag.trim());
        }

        List<String> newTags = new ArrayList<>();

        for (String reqTag : reqTags) {

            boolean addTag = true;

            for (String newTag : newTags) {
                if (newTag.equalsIgnoreCase(reqTag)) {
                    addTag = false;
                    break;
                }
            }

            if (addTag) {
                newTags.add(reqTag);
            }
        }

        return String.join(",", newTags);
    }

    private List<String> createScreenshotsFromRequest(MultipartHttpServletRequest request) throws IOException, InvalidImageException {

        List<String> images = new ArrayList<>();
        int maxScreenshots = 9;

        for (int i = 0; i < maxScreenshots; i++) {

            MultipartFile multipartFile = request.getFile("sshot_" + i);

            boolean isValidImage = imageService.isValidImageMultipartFile(multipartFile);

            if (isValidImage) {
                String newImage = imageService.create(multipartFile.getBytes());
                images.add(imageService.removeImageExtension(newImage));
            }
        }

        return images;
    }

    /**
     * Updates any changes to the asset. Trims inputs, etc. Returns lists of updated values.
     * @param page      the asset that's being changed.
     * @param request   the request to change asset details.
     * @return a list of changes that were not equal to the existing values.
     */
    public List<String> updateAndNotifyChanges(StorePage page, PageState pageState, MultipartHttpServletRequest request) throws IOException, InvalidImageException {

        List<String> updated = new ArrayList<>();

        // DETAILS

        // title
        String title = getRequestParam("title", request);
        if(!page.getDetails().getTitle().equals(title)) {
            page.getDetails().setTitle(title);
            updated.add("Title");
        }

        // short description
        String shortDesc = getRequestParam("short-description", request);
        if (!page.getDetails().getShortDescription().equals(shortDesc)) {
            page.getDetails().setShortDescription(shortDesc);
            updated.add(("Short Description"));
        }

        // long description
        String description = getRequestParam("description", request);
        if (!page.getDetails().getDescription().equals(description)) {
            page.getDetails().setDescription(description);
            updated.add("Description");
        }

        // tags
        String newTags = processStringArray(page.getDetails().getTags(), "tags", request);
        if (!StringUtils.equals(newTags, page.getDetails().getTags())) {
            page.getDetails().setTags(newTags);
            updated.add("Tags");
        }

        // category
        if (page instanceof Editable) {
            Editable editable = (Editable) page;

            String categoryName = getRequestParam("category", request);

            if (!categoryName.isEmpty()) {
                try {
                    int catId = Integer.parseInt(categoryName);

                    if (catId == -1) {
                        if (editable.getCategoryId() != null) {
                            editable.setCategoryId(null);
                            updated.add("Category");
                        }
                    }
                    else {
                        Category category = categoryRepository.findById(catId).orElse(null);
                        if (category != null) {

                            if (editable.getCategoryId() == null || editable.getCategoryId() != catId) {
                                editable.setCategoryId(catId);
                                updated.add("Category");
                            }
                        }
                    }

                }
                catch(NumberFormatException ex) {
                    // do nothing.
                }
            }
        }

        // VERSION DATA

        // version
        String version = getRequestParam("versiondata-version", request);
        if (!page.getVersionData().getVersion().equals(version)) {
            page.getVersionData().setVersion(version);
            updated.add("Version");
        }

        // version state
        VersionState versionState = VersionState.fromString(request.getParameter("versiondata-state"));
        if (versionState != null && !versionState.equals(page.getVersionData().getState())) {
            page.getVersionData().setState(versionState);
            updated.add("Version State");
        }

        // engine compatibility
        String engineCompatibility = getRequestParam("versiondata-enginecompatibility", request);
        if (!StringUtils.equals(engineCompatibility, page.getVersionData().getEngineCompatibility())) {
            page.getVersionData().setEngineCompatibility(engineCompatibility);
            updated.add("Engine Compatibility");
        }

        // BUILD DATA

        // custom repositories
        String repositories = processStringArray(page.getDetails().getTags(), "builddata-repositories", request);
        repositories = cleanupCommas(repositories);
        if (!StringUtils.equals(repositories, page.getBuildData().getRepositories())) {
            page.getBuildData().setRepositories(repositories);
            updated.add("Repositories");
        }

        // store dependencies
        String storeDependencies = processStringArray(page.getBuildData().getStoreDependencies(), "builddata-storedependencies", request);
        storeDependencies = cleanupCommas(storeDependencies);
        if (!StringUtils.equals(storeDependencies, page.getBuildData().getStoreDependencies())) {
            page.getBuildData().setStoreDependencies(storeDependencies);
            updated.add("Store Dependencies");
        }

        String dependencies = processStringArray(page.getBuildData().getHostedDependencies(), "builddata-hostedependencies", request);
        dependencies = cleanupCommas(dependencies);
        if (!StringUtils.equals(dependencies, page.getBuildData().getHostedDependencies())) {
            page.getBuildData().setHostedDependencies(dependencies);
            updated.add("Dependencies");
        }

        // MEDIA LINKS

        // screenshots
        List<String> newImages = createScreenshotsFromRequest(request);
        String[] deletedImages = request.getParameterValues("deleted-images");

        if (deletedImages != null) {

            String[] imgArray = page.getMediaLinks().getImageIds().split(",");
            List<String> images = new ArrayList<>(Arrays.asList(imgArray));

            for (String imageId : deletedImages) {
                imageService.deleteImage(imageId + ImageService.IMAGE_EXT);
                images.remove(imageId);
            }

            String newImagesString = String.join(",", images);
            newImagesString = cleanupCommas(newImagesString);
            page.getMediaLinks().setImageIds(newImagesString);
            updated.add("Deleted " + deletedImages.length + " screenshot(s).");
        }

        if (!newImages.isEmpty()) {

            List<String> images;

            if (page.getMediaLinks().getImageIds() != null) {
                String[] imgArray = page.getMediaLinks().getImageIds().split(",");
                images = new ArrayList<>(Arrays.asList(imgArray));
            }
            else {
                images = new ArrayList<>();
            }

            images.addAll(newImages);

            String newImagesString = String.join(",", images);
            newImagesString = cleanupCommas(newImagesString);
            page.getMediaLinks().setImageIds(newImagesString);
            updated.add("Added " + newImages.size() + " screenshot(s)");
        }

        // Background Image
        String backgroundIndex = request.getParameter("backgroundImageIndex");
        if (backgroundIndex != null) {

            int index = Integer.parseInt(backgroundIndex);

            // if the chosen index is not within our 0-8 range (9 images), select "no background".
            if (index < 0 || index > 8) {
                index = -1;
            }

            if (page.getMediaLinks().getBackgroundImageIndex() != index) {
                page.getMediaLinks().setBackgroundImageIndex(index);
                updated.add("Changed background image index.");
            }
        }


        // youtube videos
        // youtube video id's are a length of 11 characters.
        // that's all we'll validate for now.
        // @todo: provide a better validation ruleset for youtube videos.
        String[] youtubeVideos = request.getParameterValues("medialinks-youtubevideo");
        String newVideosString = String.join(",", youtubeVideos);
        newVideosString = cleanupCommas(newVideosString);

        // validate them here because we have a sanitized set of data now.
        newVideosString = validateYoutubeLinks(newVideosString);

        // we can just compare the list since it's an ordered list.
        // Order of lists matters in list comparison. They are not equal if they are not in the same order.
        // The order is important to the user because the first item in the list will be the first to be shown.
        if (!StringUtils.equals(newVideosString, page.getMediaLinks().getVideoIds())) {
            page.getMediaLinks().setVideoIds(newVideosString);
            updated.add("Youtube Videos");
        }

        // EXTERNAL LINKS

        // external docs link
        String docsLink = getRequestParam("externallinks-documentation", request);
        if (!StringUtils.equals(docsLink, page.getExternalLinks().getDocsWebsite())) {
            page.getExternalLinks().setDocsWebsite(docsLink);
            updated.add(("Documentation Website"));
        }

        // external publisher website
        String publisherSite = getRequestParam("externallinks-publishersite", request);
        if (!StringUtils.equals(publisherSite, page.getExternalLinks().getPublisherWebsite())) {
            page.getExternalLinks().setPublisherWebsite(publisherSite);
            updated.add("Publisher Website");
        }

        // external hub link
        String hubLink = getRequestParam("externallinks-hublink", request);
        if (!StringUtils.equals(hubLink, page.getExternalLinks().getHubLink())) {
            page.getExternalLinks().setHubLink(hubLink);
            updated.add("Hub Link");
        }

        // OPEN SOURCE DATA

        String gitRepo = getRequestParam("opensource-git-repo", request);
        if (!page.getOpenSourceData().getGitRepository().equals(gitRepo)) {
            page.getOpenSourceData().setGitRepository(gitRepo);
            updated.add("Git Repository");
        }

        // isFork
        String forkedString = getRequestParam("opensource-forked", request);
        boolean forked = forkedString.equalsIgnoreCase("on");
        if (forked != page.getOpenSourceData().isFork()) {
            page.getOpenSourceData().setFork(forked);
            updated.add("Is a Fork");
        }

        // fork repo
        String forkRepo = getRequestParam("opensource-fork-repo", request);
        if (!page.getOpenSourceData().getForkRepository().equals(forkRepo)) {
            page.getOpenSourceData().setForkRepository(forkRepo);
            updated.add("Fork Repository");
        }

        // software license
        OpenSourceLicense softwareLicense = OpenSourceLicense.fromString(request.getParameter("opensource-softwarelicense"));
        if (softwareLicense != null && softwareLicense != page.getOpenSourceData().getSoftwareLicense()) {
            page.getOpenSourceData().setSoftwareLicense(softwareLicense);
            updated.add("Software License");
        }

        // media license
        OpenSourceLicense mediaLicense = OpenSourceLicense.fromString(request.getParameter("opensource-medialicense"));
        if (mediaLicense != null && mediaLicense != page.getOpenSourceData().getMediaLicense()) {
            page.getOpenSourceData().setMediaLicense(mediaLicense);
            updated.add("Media License");
        }

        // PAYMENT DETAILS
        String price = getRequestParam("paymentdata-price", request);

        try {
            BigDecimal newPrice = new BigDecimal(price);

            if (newPrice.compareTo(page.getPaymentData().getPrice()) != 0) {
                page.getPaymentData().setPrice(newPrice);
                updated.add("Price");
            }
        }
        catch (NumberFormatException ex) {
            // @TODO: unable to parse new price.
        }

        if (!updated.isEmpty()) {

            // last updated
            // asset.setDateUpdated(System.currentTimeMillis());

            switch (pageState) {
                case Draft: draftRepository.save((PageDraft) page); break;
                case Amendment: amendmentRepository.save((PageAmendment) page); break;

            }
        }

        return updated;
    }

    private String cleanupCommas(String input) {

        while (input.contains(",,")) {
            input = input.replace(",,", ",");
        }

        while (input.startsWith(",")) {
            input = input.substring(1);
        }

        while (input.endsWith(",")) {
            input = input.substring(0, input.length() - 1);
        }

        return input;
    }

    // takes a comma-seperated string of youtube links and removes OR cleans any invalid links.
    private String validateYoutubeLinks(String videoLinks) {

        // if the link starts with "https://youtube" or variants, extract the ID.
        // check the length of the ID is 11.
        // @todo: we "could" validate the links from a youtube API. probably a good idea at some point.

        String[] ids = videoLinks.split(",");
        List<String> newIds = new ArrayList<>();

        for (String id : ids) {

            // "assume" it's an ID
            if (id.length() == 11) {
                newIds.add(id);
            }
            else {

                Pattern p = Pattern.compile("^(?:https?:\\/\\/)?(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*", Pattern.CASE_INSENSITIVE);
                Matcher matcher = p.matcher(id);

                if (matcher.matches()) {

                    /*
                    for (int i = 0; i <= matcher.groupCount(); i++) {
                        System.out.println(String.format("Match [%d] %s", i, matcher.group(i)));
                    }
                     */

                    if (matcher.groupCount() >= 1) {
                        String match = matcher.group(1);

                        if (match.length() == 11) {
                            newIds.add(match);
                        }
                    }
                }
            }
        }

        return String.join(",", newIds);
    }

    public void delete(StorePage page) throws UnsupportedOperationException {

        final PageState pageState = PageState.fromPage(page);

        if (pageState == null) {
            throw new UnsupportedOperationException("Unknown Pagestate Specified!");
        }

        switch (pageState) {
            case Draft: draftRepository.delete((PageDraft) page); break;
            case Live: livePageRepository.delete((LivePage) page); break;
            case Amendment: amendmentRepository.delete((PageAmendment) page); break;
        }

        // delete all images
        if (page.getMediaLinks().getImageIds() != null) {
            String[] images = page.getMediaLinks().getImageIds().split(",");
            Arrays.stream(images).forEach(id -> imageService.deleteImage(id + ImageService.IMAGE_EXT));
        }

        // if this is a live asset, delete any amendments.
        if (pageState == PageState.Live) {

            List<PageReview> reviews = reviewRepository.findByPageId(page.getId());
            reviewRepository.deleteAll(reviews);

            amendmentRepository.findByParentPageId(page.getId()).ifPresent(this::delete);
        }

        if (page instanceof Editable) {
            Iterable<StaffPageReview> staffReviews = staffReviewRepository.findByPageId(page.getId());
            staffReviewRepository.deleteAll(staffReviews);
        }
    }

}
