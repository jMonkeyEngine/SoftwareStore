var app = new Vue({
  el: "#app",

  data: {
    userId: "",
    pageId: "",
    pageState: "",
    preview: false,

    page: {},
    reviewData: [],

    // dependencyPages: [],
    // forks: [],
    // addons: [],
    authorStats: {
      userProfile: {},
      registerDuration: 0,
      pageCount: 0,
      reviewCount: 0,
      averateRating: 0.0
    },

    pageData: {
      dependsOn: [], // store pages this software depends on.
      addons: [], // store pages that use this software.
      forks: [] // forks of the same github repository.
    }
  },

  mounted: function() {
    this.userId = $("#userId").val();
    this.pageId = $("#pageId").val();
    this.pageState = $("#pageState").val();

    this.preview =
      $("#preview")
      .val()
      .toLowerCase() == "true";

    this.getPage();
    this.fetchReviews();
  },

  updated: function() {
    $(".tooltip")
      .popup("destroy")
      .popup();

    $(".ui.accordion")
      .accordion("destroy")
      .accordion();

    mountCarousel();
    mountMarkdown(this.page.details.description);
  },

  methods: {
    getPage: function() {
      let pageUrl = "/api/page/";

      if (this.pageState.length > 0) {
        switch (this.pageState) {
          case "Draft":
            pageUrl += "draft/";
            break;
          case "Amendment":
            pageUrl += "amendment/";
            break;
        }
      }

      $.ajax({
        url: pageUrl + this.pageId,
        method: "GET",
        success: function(data) {
          app.page = data;

          if (!app.preview) {
            app.getPageData();
            app.getAuthorStats();
          }
        },
        error: toast.defaultAjaxError,
        complete: function(jqXHR, textStatus) {
          $("#pageLoader").removeClass("active");
        }
      });
    },

    getPageData: function() {
      // gets additional data about this page: forks, addons, etc.

      $.ajax({
        url: "/api/page/data/" + this.pageId,
        method: "GET",
        success: function(data) {
          app.pageData = data;
        },
        error: toast.defaultAjaxError
      });
    },

    getAuthorStats: function() {
      let userId = app.page.owner.id;

      $.ajax({
        url: "/api/page/stats/" + userId,
        method: "GET",
        success: function(data) {

          app.authorStats = data;
          app.getRegisteredDuration(data.userProfile.registerDate);

        },
        error: toast.defaultAjaxError
      });
    },

    getRegisteredDuration: function(registerDate) {
      let now = moment(new Date().getTime());
      let registered = moment(registerDate);

      let diff = now.diff(registered, "days");
      this.registerDuration = diff;
    },

    getBackgroundImageStyle: function() {

      var backgroundIndex = app.page.mediaLinks.backgroundImageIndex;

      if (backgroundIndex == -1) {
        return "";
      }

      var imageCount = app.page.mediaLinks.imageIds.split(',').length;

      if (backgroundIndex >= imageCount) {
        return "";
      }

      var style = "background-image: linear-gradient(to bottom, rgba(255,255,255,0.7) 0%,rgba(255,255,255,0.7) 100%), url(/image/" + app.page.mediaLinks.imageIds.split(',')[backgroundIndex] + ");";
      style += "background-size: cover;";
      style += "background-attachment: fixed;";
      style += "background-position: center;";

      return style;
    },

    /*
    getDependencyPages: function() {
    	let storeIds = app.page.buildData.storeDependencies.split(",");

    	for (let i = 0; i < storeIds.length; i++) {
    		$.ajax({
    			url: "/api/page/" + storeIds[i],
    			method: "GET",
    			success: function(data) {
    				// app.dependencyPages = data;
    				app.dependencyPages.push(data);
    			},
    			error: function(xhr, status, error) {},
    			complete: function(jqXHR, textStatus) {}
    		});
    	}
    },

    getPageAddons: function() {
    	$.ajax({
    		url: "/api/page/dependencies/" + app.page.id,
    		method: "GET",
    		success: function(data) {
    			app.addons = data;
    		},
    		error: toast.defaultAjaxError
    	});
    },

    getForks: function() {
    	$.ajax({
    		url: "/api/asset/forks/" + assetId,
    		method: "GET",
    		success: function(data) {
    			app.forks = data;
    		},
    		error: toast.defaultAjaxError,
    		complete(jqXHR, textStatus) {}
    	});
    },
    */

    showWriteReviewModal() {
      $("#reviewRating").rating({
        initialRating: 1,
        maxRating: 5,
        onRate: function(rating) {
          $("#reviewRatingVal").val("" + rating);
        }
      });

      $("#writeReviewModal")
        .modal("destroy")
        .modal({
          onApprove: function() {
            let jsonData = {
              pageId: app.pageId,
              reviewContent: $("#newReviewContent").val(),
              rating: $("#reviewRatingVal").val()
            };

            jsonData = JSON.stringify(jsonData);

            $.ajax({
              url: "/api/review/",
              type: "POST",
              data: jsonData,
              contentType: "application/json; charset=utf-8",
              dataType: "json",
              success: function(newReview) {
                app._data.reviewData.unshift(newReview);
              },
              error: toast.defaultAjaxError
            });
          }
        })
        .modal("show");
    },

    fetchReviews: function() {
      $.ajax({
        url: "/api/review/page/" + this.pageId,
        method: "GET"
      }).done(responseData => {
        app.reviewData = responseData;
      });
    },

    deleteReview: function(id) {
      deleteReviewId = id;

      $("#deleteReviewModal")
        .modal("destroy")
        .modal({
          onApprove: function() {
            let formData = new FormData();
            formData.append("pageId", app.pageId);

            $.ajax({
              url: "/api/review/",
              type: "DELETE",
              data: formData,
              cache: false,
              contentType: false,
              processData: false,
              success: function(newReview) {
                app._data.reviewData.splice(deleteReviewId, 1);
                toast.info(null, "Review deleted successfully.", false);
              },
              error: toast.defaultAjaxError
            });
          }
        })
        .modal("show");
    },

    editReview: function(id) {
      editReviewId = id;
      let currentReview = this.reviewData[id];
      $("#edit_reviewRatingVal").val("" + currentReview.rating);

      // rating for "edit review"
      $("#edit_reviewRating").rating({
        initialRating: currentReview.rating,
        maxRating: 5,
        onRate: function(rating) {
          $("#edit_reviewRatingVal").val("" + rating);
        }
      });

      $("#edit_newReviewContent").val(currentReview.content);

      $("#editReviewModal")
        .modal("destroy")
        .modal({
          onApprove: function() {
            let jsonData = {
              pageId: app.pageId,
              reviewContent: $("#edit_newReviewContent").val(),
              rating: $("#edit_reviewRatingVal").val()
            };

            jsonData = JSON.stringify(jsonData);

            $.ajax({
              url: "/api/review/",
              type: "PUT",
              data: jsonData,
              contentType: "application/json; charset=utf-8",
              dataType: "json",
              success: function(newReview) {
                app._data.reviewData.splice(editReviewId, 1, newReview);
              },
              error: toast.defaultAjaxError
            });
          }
        })
        .modal("show");
    },

    getJitpackDependency() {
      // let githubRepo = /*[[${asset.gitRepository}]]*/ "";
      let githubRepo = this.page.openSourceData.gitRepository;

      let owner = githubRepo.replace("https://github.com/", "");
      owner = owner.substr(0, owner.lastIndexOf("/"));

      let name = githubRepo.substr(githubRepo.lastIndexOf("/") + 1).replace(".git", "");
      let version = "master-SNAPSHOT";

      let implementation = 'implementation "com.github.' + owner + ":" + name + ":" + version + '"';

      // $("#gradleDependency").text(implementation);
      return implementation;
    },

    getArrayLength(input) {
      // string.split(",") returns an array length of 1 for empty strings. This method returns 0 if the string is empty.

      if (input.length === 0) {
        return 0;
      }

      return input.split(",").length;
    },

    millisToDate: function(millis) {
      return moment(millis).format("dddd Do MMMM YYYY");
    }
  }
});

$(document).ready(function() {});

// screenshots + videos carousel
function mountCarousel() {
  let media = $("#carousel-links > a");

  let options = {
    container: "#gallery-carousel",
    carousel: true,
    slideshowInterval: 5000
  };

  // we might not have a screenshot or video in "preview" mode, and mounting without any links causes a JS error.
  if (media.length > 0) {
    blueimp.Gallery(media, options);
  }
}

function mountMarkdown(content) {
  let md = window
    .markdownit({
      linkify: true,
      typographer: true,
      quotes: "“”‘’",
      langPrefix: "language-",
      html: false,
      xhtmlOut: false,

      highlight: function(str, lang) {
        if (lang && hljs.getLanguage(lang)) {
          try {
            return '<pre class="hljs"><code>' + hljs.highlight(lang, str, true).value + "</code></pre>";
          } catch (__) {}
        }

        return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + "</code></pre>";
      }
    })
    .use(window.markdownitAbbr)
    .use(window.markdownitDeflist)
    .use(window.markdownitEmoji)
    .use(window.markdownitFootnote)
    .use(window.markdownitIns)
    .use(window.markdownitMark)
    .use(window.markdownitSub)
    .use(window.markdownitSup);

  md.renderer.rules.table_open = function() {
    return '<table class="ui celled table">\n';
  };

  md.renderer.rules["h1_open"] = function() {
    return '<h1 class="ui header">\n';
  };

  $("#page-content-div").html(md.render(content));
}