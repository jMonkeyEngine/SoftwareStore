package com.jayfella.website.core;

import com.jayfella.website.core.page.PageState;
import com.jayfella.website.database.entity.page.StorePage;
import com.jayfella.website.http.response.SimpleApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static com.jayfella.website.core.ResponseStrings.*;

public class ApiResponses {



    public static final ResponseEntity<SimpleApiResponse> noChangesDetected() {
        return ResponseEntity.badRequest()
                .body(new SimpleApiResponse(NO_CHANGES_DETECTED));
    }

    public static ResponseEntity<SimpleApiResponse> userNotFound(long userId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new SimpleApiResponse(String.format(USER_NOT_FOUND, userId)));
    }

    public static ResponseEntity<SimpleApiResponse> pageRejected(StorePage page) {
        return ResponseEntity.ok()
                .body(new SimpleApiResponse(String.format(PAGE_REJECTED, page.getDetails().getTitle())));
    }

    public static ResponseEntity<SimpleApiResponse> searchTermTooShort() {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(new SimpleApiResponse(String.format(SEARCH_TOO_SHORT, PageRequirements.MIN_SEARCH_LENGTH)));
    }

    public static ResponseEntity<SimpleApiResponse> pageDeleted(StorePage page) {
        return ResponseEntity.ok()
                .body(new SimpleApiResponse(String.format(PAGE_DELETED, page.getDetails().getTitle())));
    }

    public static ResponseEntity<SimpleApiResponse> noPageIdSpecified() {
        return ResponseEntity.badRequest()
                .body(new SimpleApiResponse(NO_PAGE_ID_SPECIFIED));
    }

    public static ResponseEntity<SimpleApiResponse> notLoggedIn() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new SimpleApiResponse(NOT_LOGGED_IN));
    }

    public static ResponseEntity<SimpleApiResponse> badRequest(BindingResult bindingResult) {
        return ResponseEntity.badRequest()
                .body(new SimpleApiResponse(BAD_REQUEST, ServerUtilities.getErrorMessages(bindingResult)));
    }

    public static ResponseEntity<SimpleApiResponse> pageNotFound(PageState pageState, String pageId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new SimpleApiResponse(String.format(PAGE_NOT_FOUND, pageState.name(), pageId)));
    }

    public static ResponseEntity<SimpleApiResponse> insufficientPermission() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new SimpleApiResponse(INSUFFICIENT_PERMISSION));
    }

    public static ResponseEntity<SimpleApiResponse> staffApprovalSuccess(StorePage page) {
        return ResponseEntity.ok()
                .body(new SimpleApiResponse(String.format(STAFF_PAGE_APPROVED, page.getDetails().getTitle())));
    }

    public static ResponseEntity<SimpleApiResponse> parentPageNotFound(String pageId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new SimpleApiResponse(String.format(PARENT_PAGE_NOT_FOUND, pageId)));
    }

}
