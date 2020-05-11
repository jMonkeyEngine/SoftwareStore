package com.jayfella.website.http.request.badge;

import javax.validation.constraints.NotNull;

/**
 * Used to GRANT or REVOKE a badge from a user.
 */
public class UserBadgeRequest {

    @NotNull(message = "badge id required.")
    private long badgeId;

    @NotNull(message = "user id required.")
    private long userId;

    public long getBadgeId() { return badgeId; }
    public void setBadgeId(long badgeId) { this.badgeId = badgeId; }

    public long getUserId() { return userId; }

    public void setUserId(long userId) { this.userId = userId; }

}
