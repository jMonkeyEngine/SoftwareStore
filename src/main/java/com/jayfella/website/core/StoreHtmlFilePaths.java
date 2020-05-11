package com.jayfella.website.core;

public class StoreHtmlFilePaths {

    public static class StorePath {

        private final String urlPath, htmlFilePath;

        StorePath(String urlPath, String htmlFilePath) {
            this.urlPath = urlPath;
            this.htmlFilePath = htmlFilePath;
        }

        public String getUrlPath() { return urlPath; }
        public String getHtmlFilePath() { return htmlFilePath; }
    }

    // public static final String ACCOUNT_VALIDATION_RESPONSE = "/user/account-validation-response.html";


    public static class Admin {
        private static final String ADMIN_DIR = "/admin/";
        public static final StorePath INDEX = new StorePath(ADMIN_DIR, ADMIN_DIR + "index.html");
        public static final StorePath PAGES = new StorePath(ADMIN_DIR + "assets/", ADMIN_DIR + "pages.html");
        public static final StorePath USER = new StorePath(ADMIN_DIR + "user/", ADMIN_DIR + "user.html");
        public static final StorePath USERS = new StorePath(ADMIN_DIR + "users/", ADMIN_DIR + "users.html");
        public static final StorePath BADGES = new StorePath(ADMIN_DIR + "badges/", ADMIN_DIR + "badges.html");
        public static final StorePath CATEGORIES = new StorePath(ADMIN_DIR + "categories/", ADMIN_DIR + "categories.html");

    }

    public static class Store {
        private static final String STORE_DIR = "/";

        public static final StorePath INDEX = new StorePath(STORE_DIR, STORE_DIR + "index.html");
        public static final StorePath VIEW_PAGE = new StorePath(STORE_DIR, STORE_DIR + "view-page.html");
        public static final StorePath CREATE_PAGE = new StorePath(STORE_DIR + "create/", STORE_DIR + "create-page.html");
        public static final StorePath EDIT_PAGE = new StorePath(STORE_DIR + "edit/", STORE_DIR + "edit-page.html");
    }

    public static class User {
        private static final String USER_DIR = "/user/";

        public static final StorePath INDEX = new StorePath(USER_DIR, USER_DIR + "index.html");

        public static final StorePath LOGIN = new StorePath(USER_DIR + "login/", USER_DIR + "login.html");
        public static final StorePath REGISTER = new StorePath(USER_DIR + "register/", USER_DIR + "register.html");
        public static final StorePath REGISTERED = new StorePath(USER_DIR + "registered/", USER_DIR + "registered.html");

        public static final StorePath MY_PAGES = new StorePath(USER_DIR + "my-pages/", USER_DIR + "my-pages.html");

        public static final StorePath VALIDATION_RESPONSE = new StorePath(USER_DIR + "validate/", USER_DIR + "validation-response.html");

        public static final StorePath PROFILE = new StorePath(USER_DIR + "profile/", USER_DIR + "profile.html");
    }

    public static class Contact {
        private static final String CONTACT_DIR = "/contact/";

        public static final StorePath INDEX = new StorePath(CONTACT_DIR, CONTACT_DIR + "index.html");
        public static final StorePath SUCCESS = new StorePath(CONTACT_DIR, CONTACT_DIR + "success.html");
    }

}
