// force the page to re-load if the back button was used
$(window).bind("pageshow", function(event) {
  if (event.originalEvent.persisted) {
    window.location.reload();
  }
});

function createGetUrl(pageState, pageId) {
  let url = "/api/page/";

  switch (pageState) {
    case "Draft":
      url += "draft/";
      break;
    case "Amendment":
      url += "amendment/";
      break;
  }

  url += pageId;
  return url;
}

function createPutUrl(pageState) {
  let url = "/api/page/";

  switch (pageState) {
    case "Draft":
      url += "draft/";
      break;
    case "Amendment":
      url += "amendment/";
      break;
  }

  return url;
}

function createSubmitUrl(pageState) {
  let url = "/api/page/approve/";

  switch (pageState) {
    case "Draft":
      url += "draft/";
      break;
    case "Amendment":
      url += "amendment/";
      break;
  }

  return url;
}

function createPreviewUrl(pageState, pageId) {
  let url = "/preview/";

  switch (pageState) {
    case "Draft":
      url += "draft/";
      break;
    case "Amendment":
      url += "amendment/";
      break;
  }

  url += pageId;
  return url;
}

var app = new Vue({
  el: "#app",
  data: {
    pageId: "",
    pageState: "",
    page: {}
  },

  beforeMount: function() {
    this.pageId = $("#pageId").attr("value");
    this.pageState = $("#pageState").attr("value");
  },

  mounted: function() {
    this.getPage();
  },

  updated: function() {
    mountControls();

    // update the description counters.
    if (this.page.reviewState === "None" || this.page.reviewState === "Rejected") {
      this.titleKeyUp();
      this.shortDescKeyUp();
      this.longDescKeyUp();
    }

    this.buildJitPackHelperLink();

    $(".tooltip")
      .popup("destroy")
      .popup();
  },

  methods: {
    getPage: function() {
      let url = createGetUrl(this.pageState, this.pageId);

      $.ajax({
        url: url,
        method: "GET",
        success: function(data) {
          app.page = data;
        },
        error: toast.defaultAjaxError
      });
    },

    getCategoryNameFromId: function(id) {
      for (let i = 0; i < this.categories.length; i++) {
        if (this.categories[i].id === id) {
          return this.categories[i].name;
        }
      }

      return "UNKNOWN";
    },

    updatePage: function(then) {
      let url = createPutUrl(this.pageState, this.pageId);

      let formElement = $("#form").get(0);
      let formData = new FormData(formElement);

      $.ajax({
        url: url,
        method: "PUT",
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(data) {
          if (data.areasUpdated.length == 0) {
            toast.info("Save Changes", "No changes were detected.", false);
          } else {
            // clear all screenshot input values.
            $("input:file").each(function(index, element) {
              $(element).val("");
            });

            // clear all the deleted images.
            $("#deletedImages").empty();

            // remove any image that starts with "data:"
            $(".sshot-img").each(function(index, element) {
              let src = $(element).attr("src");

              if (src.startsWith("data:")) {
                $(element).attr("src", "");
                $(element).addClass("hidden-element");
              }
            });

            app.page = data.page;

            // toastVue.showToast("green", "Save Successful", "The following areas were updated:", data.areasUpdated);
            let msg = "<p>The following areas were updated:</p>" + toast.arrayToHtmlList(data.areasUpdated);
            toast.success("Save Changes", msg, false);
          }

          if (then != null) {
            then();
          }
        },
        error: toast.defaultAjaxError
      });
    },

    saveAndSubmitPage() {
      this.updatePage(() => {
        this.submitPage();
      });
    },

    submitPage: function() {
      let url = createSubmitUrl(this.pageState);

      let formElement = $("#submitPageForm").get(0);
      let formData = new FormData(formElement);

      $.ajax({
        url: url,
        method: "POST",
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(data) {
          let redirectToStorePage = app.page.owner.moderator || app.page.owner.administrator || app.page.owner.trustLevel == 2;

          if (redirectToStorePage) {
            window.location.href = "/" + data.id;
          } else {
            window.location.reload();
          }
        },
        error: toast.defaultAjaxError
      });
    },

    previewPage: function() {
      let url = createPreviewUrl(this.pageState, this.pageId);
      window.open(url, "_blank");
    },

    screenshotChanged: function(event, elem) {
      let input = event.target;

      if (input.files && input.files[0]) {
        //clearImgError(elem, input);

        // the file is not a JPEG
        if (input.files[0].type != "image/jpeg") {
          // displayImgError(elem, "Not a JPEG.", input);
          $(input).val("");
          toastVue.showToast("red", "Image Rejected", "Image must be in JPEG format.");
          return;
        }

        let reader = new FileReader();
        reader.onload = function(event) {
          let img = new Image();
          img.onload = function() {
            if (this.width != 1280 || this.height != 720) {
              $(input).val("");
              toastVue.showToast("red", "Image Rejected", "Image dimenstions must be 1280 x 720");
              return;
            }

            $(elem).attr("src", event.target.result);
            $(elem).removeClass("hidden-element");
          };

          img.src = event.target.result;
        };

        reader.readAsDataURL(input.files[0]);
      }
    },

    clearScreenshot: function(event) {
      let imageIndex = $(event.target).attr("data-index");
      let imageIds = app.page.mediaLinks.imageIds.split(",");

      if (imageIndex < imageIds.length) {
        let imageId = imageIds[imageIndex];

        if (imageId.length > 0) {
          // in order to send data in the form, we add the deleted image to a SELECT MULTIPLE and mark it as selected.
          let newOption = new Option(imageId, imageId);
          newOption.setAttribute("selected", "selected");

          $("#deletedImages").append(newOption);

          // remove the file value from the file INPUT
          $(event.target)
            .parent()
            .children("input[type=file]")
            .val("");
        }

        imageIds.splice(imageIndex, 1);
        app.page.mediaLinks.imageIds = imageIds.join(",");
      }

      // if the index is larger than imageIds.length, it hasn't been uploaded.
      else {
        $(event.target)
          .parent()
          .children("img")
          .attr("src", "")
          .addClass("hidden-element");
      }
    },

    titleKeyUp: function() {
      let length = $("#titleInput").val().length;
      let elem = $("#titleCount");
      processInputCounter(elem, 6, length); // com.jayfella.website.core.PageRequirements
    },

    shortDescKeyUp: function() {
      let length = $("#shortDescTextArea").val().length;
      let elem = $("#shortDescCount");
      processInputCounter(elem, 10, length); // com.jayfella.website.core.PageRequirements
    },

    longDescKeyUp: function() {
      let length = $("#descriptionTextArea").val().length;
      let elem = $("#longDescCount");
      processInputCounter(elem, 50, length); // com.jayfella.website.core.PageRequirements
    },

    gitRepoLinkKeyUp: function() {
      var isValidLink = isUrlValid($("#gitRepoLink").val());

      if (isValidLink) {
        $("#gitRepoLinkDiv").removeClass("error");
      } else {
        $("#gitRepoLinkDiv").addClass("error");
      }
    },

    getArrayLength(input) {
      if (input.length === 0) {
        return 0;
      }

      return input.split(",").length;
    },

    getImageIndex: function(row, col) {
      let imges = this.page.mediaLinks.imageIds.split(",");

      let rowIndex = row - 1;
      let colIndex = col - 1;

      return rowIndex * 3 + colIndex;
    },

    isForkChanged: function(event) {
      let checked = !$("#forkCheckbox").hasClass("checked");

      if (checked) {
        // console.log("checked");
        $("#forkedRepoField").removeClass("hidden");
      } else {
        // console.log("unchecked");
        $("#forkedRepoField").addClass("hidden");
      }
    },

    addRepo: function(repo) {
      this.page.buildData.repositories = addToStringArray(this.page.buildData.repositories, repo);
    },

    removeRepo: function(index) {
      this.page.buildData.repositories = removeFromStringArray(this.page.buildData.repositories, index);
    },

    updateRepo: function(index, event) {
      this.page.buildData.repositories = updateStringArray(this.page.buildData.repositories, index, event);
    },

    addStoreDependency: function(dep) {
      this.page.buildData.storeDependencies = addToStringArray(this.page.buildData.storeDependencies, dep);
    },

    removeStoreDependency: function(index) {
      this.page.buildData.storeDependencies = removeFromStringArray(this.page.buildData.storeDependencies, index);
    },

    updateStoreDependency: function(index, event) {
      this.page.buildData.storeDependencies = updateStringArray(this.page.buildData.storeDependencies, index, event);
    },

    addHostedDependency: function(dep) {
      this.page.buildData.hostedDependencies = addToStringArray(this.page.buildData.hostedDependencies, dep);
    },

    removeHostedDependency: function(index) {
      this.page.buildData.hostedDependencies = removeFromStringArray(this.page.buildData.hostedDependencies, index);
    },

    updateHostedDependency: function(index, event) {
      this.page.buildData.hostedDependencies = updateStringArray(this.page.buildData.hostedDependencies, index, event);
    },

    buildJitPackHelperLink: function() {
      let repo = app.page.openSourceData.gitRepository;

      // https://github.com/jayfella/jme-materialize
      // https://bitbucket.org/JavaSabr/jmonkeybuilder/src/master/
      // https://gitlab.com/gitlab-org/gitlab-ce

      // https://jitpack.io/#jayfella/jme-materialize

      let link = "";

      if (repo.toLowerCase().startsWith("https://github.com")) {
        link = "https://jitpack.io/" + repo.replace("https://github.com/", "#");
      } else if (repo.toLowerCase().startsWith("https://bitbucket.org/")) {
        link = "https://jitpack.io/" + repo.replace("https://bitbucket.org/", "#").replace("/src/master/", "");
      } else if (repo.toLowerCase().startsWith("https://gitlab.com/")) {
        link = "https://jitpack.io/" + repo.replace("https://gitlab.com/", "#");
      }

      $("#jitpackHelperLink").attr("href", link);
    },

    getBackgroundImageIndex: function(index) {

      if (index < 0 || index > 8) {
        return 9;
      }

      return index;
    }

  }
});

function addToStringArray(stringIn, value) {
  if (stringIn.length !== 0) {
    stringIn += ",";
  }

  stringIn += value;
  return stringIn;
}

function removeFromStringArray(stringIn, index) {
  let values = stringIn.split(",");
  values.splice(index, 1);

  let newValue = values.join(",");
  return newValue;
}

function updateStringArray(stringIn, index, event) {
  let value = $(event.target).val();
  let values = stringIn.split(",");
  values[index] = value;

  let newValue = values.join(",");
  return newValue;
}

// marks text as red if the length is less than minValue.
function processInputCounter(elem, minValue, length) {
  elem.html("" + numberWithCommas(length));

  if (length < minValue) {
    elem.addClass("ui red text");
  } else {
    elem.removeClass("ui red text");
  }
}

// https://stackoverflow.com/a/2901298
function numberWithCommas(x) {
  return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function isUrlValid(url) {
  return /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(
    url
  );
}

function mountControls() {
  $(".dropdown")
    .dropdown("destroy")
    .dropdown(); // this must be before the tags dropdown
  $(".checkbox")
    .dropdown("destroy")
    .checkbox();

  // stop tag additions form submitting the form.
  $("#tagsDropdown")
    .dropdown("destroy")
    .dropdown({
      allowAdditions: true,
      delimiter: "|",
      keys: {
        delimiter: 13 // stop the enter key submitting the form
      }
    });
}