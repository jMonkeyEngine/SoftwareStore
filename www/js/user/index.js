var app = new Vue({
  el: "#app",

  data: {
    user: {},
    user_email: "",
    system_avatar: {
      backgroundColor: "f0e9e9",
      foregroundColor: "8b5d5d",
      bold: false,
      uppercase: false
    },
    validation: {},
    registerDuration: 0,
    pageStats: {
      pageCount: 0,
      reviewCount: 0,
      averateRating: 0.0
    }
  },

  mounted: function() {
    this.getUser();
    this.getValidation();
    mountBackgroundColorPicker();
    mountForegroundColorPicker();
    mountSystemAssignedCheckboxes();
  },

  updated: function() {
    $(".tooltip")
      .popup("destroy")
      .popup();
  },

  methods: {
    getUser: function() {
      $.ajax({
        url: "/api/user/",
        type: "GET",
        cache: false,
        success: function(data) {
          app.user = data;
          // app.getRegisteredDuration();
          // var da = getUserStats(data.id);
          getUserStats(data.id, app.statsRecievedCallback);
          app.registerDuration = dateToDays(data.registerDate);
        },
        error: toast.defaultAjaxError
      });
    },

    statsRecievedCallback: function(data) {
      app.pageStats = data;
    },

    requestUserEmail: function(userId) {
      $.ajax({
        url: "/api/user/email/",
        method: "GET",
        success: function(data) {
          app.user_email = data.message;
        },
        error: toast.defaultAjaxError
      });
    },

    getValidation: function() {
      $.ajax({
        url: "/api/validate/",
        type: "GET",
        cache: false,
        success: function(data) {
          app.validation = data;
        },
        error: toast.defaultAjaxError
      });
    },

    nameKeyUp: function() {
      $("#updateNameButton").attr("disabled", false);
    },

    updateName: function() {
      let newName = $("#nameInput").val();
      let formData = new FormData();
      formData.append("name", newName);

      $.ajax({
        url: "/api/user/name/",
        type: "PUT",
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(data) {
          $("#nameInputDiv").removeClass("error");
          $("#updateNameButton").attr("disabled", true);
          $("#nameInput").transition("glow");

          app.user.name = newName;
          toast.info(null, data.message, false);
        },
        error: function(xhr, status, error) {
          $("#nameInputDiv").addClass("error");
          toast.defaultAjaxError(xhr, status, error);
        }
      });
    },

    usernameKeyUp: function() {
      $("#updateUsernameButton").attr("disabled", false);
    },

    updateUsername: function() {
      let newUsername = $("#usernameInput").val();
      let formData = new FormData();
      formData.append("username", newUsername);

      $.ajax({
        url: "/api/user/username/",
        type: "PUT",
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(data) {
          $("#usernameInputDiv").removeClass("error");
          $("#updateUsernameButton").attr("disabled", true);
          $("#usernameInput").transition("glow");

          app.user.username = newUsername;
          toast.info(null, data.message, false);
        },
        error: function(xhr, status, error) {
          $("#usernameInputDiv").addClass("error");
          toast.defaultAjaxError(xhr, status, error);
        }
      });
    },

    emailKeyUp: function() {
      $("#changeEmailButton").attr("disabled", false);
    },

    passwordKeyUp1: function() {
      $("#passwordInput2").attr("disabled", false);
      this.checkEnableSendPassword();
    },

    passwordKeyUp2: function() {
      this.checkEnableSendPassword();
    },

    checkEnableSendPassword() {
      let val1 = $("#passwordInput1").val();
      let val2 = $("#passwordInput2").val();
      let match = val1 === val2;

      $("#changePasswordButton").attr("disabled", !match);
    },

    sendEmail: function() {
      $.ajax({
        url: "/api/user/preferences/email/",
        method: "POST",
        success: function(data) {
          // do nothing?
        },
        error: toast.defaultAjaxError
      });
    },

    systemAssignedAvatar: function() {
      $("#systemAssignedAvatarModal")
        .modal("destroy")
        .modal({
          onApprove: function() {
            let jsonString = JSON.stringify(app.system_avatar);

            $.ajax({
              url: "/api/avatar/system-managed/",
              type: "POST",
              contentType: "application/json; charset=utf-8",
              data: jsonString,
              success: function(data) {
                app.user.avatarId = data.message;
                toast.success(null, "Avatar Updated", false);
              },
              error: toast.defaultAjaxError
            });
          }
        })
        .modal("show");
    },

    updateSystemAssignedAvatar: function() {
      let usedName = !app.user.name || app.user.name.length == 0 ? app.user.username : app.user.name;
      usedName = encodeURIComponent(usedName);

      let url = "https://ui-avatars.com/api/" + "?name=" + usedName + "&size=128";
      url += "&background=" + app.system_avatar.backgroundColor;
      url += "&color=" + app.system_avatar.foregroundColor;
      url += "&bold=" + app.system_avatar.bold;
      url += "&uppercase=" + app.system_avatar.uppercase;

      $("#systemAssignedPreview").attr("src", url);
    },

    gravatarAvatar: function() {
      $("#gravatarAvatarModal")
        .modal("destroy")
        .modal({
          onApprove: function() {
            let email = $("#gravatarEmail").val();
            let hash = md5(email).trim();

            let data = {
              emailHash: hash
            };

            var jsonString = JSON.stringify(data);

            $.ajax({
              url: "/api/avatar/gravatar/",
              type: "POST",
              contentType: "application/json; charset=utf-8",
              data: jsonString,
              success: function(data) {
                app.user.avatarId = data.message;
                toast.success(null, "Avatar Updated", false);
              },
              error: toast.defaultAjaxError
            });
          }
        })
        .modal("show");
    },

    customAvatar: function() {
      $("#customAvatarModal")
        .modal("destroy")
        .modal({
          onApprove: function() {
            let form = $("#custom-avatar-form");
            var data = new FormData(form[0]);

            $.ajax({
              url: form.attr("action"),
              type: form.attr("method"),
              contentType: false,
              data: data,
              cache: false,
              processData: false,
              success: function(data) {
                app.user.avatarId = data.message;
                toast.success(null, "Avatar Updated", false);
              },
              error: toast.defaultAjaxError
            });
          }
        })
        .modal("show");
    },

    customAvatarChanged: function() {
      let input = document.getElementById("custom-avatar-input");

      let reader = new FileReader();
      reader.onload = function(event) {
        let img = new Image();
        img.onload = function() {
          if (this.width > 320 || this.height > 320) {
            toast.error("Invalid Dimensions", "Image must be 320 x 320 pixels or less.", true);
            input.value = "";
            return;
          }

          let imgElement = $("#custom-avatar-image");

          console.log("correct filetype and dimensions");
          $(imgElement).attr("src", event.target.result);
        };

        img.src = event.target.result;
      };

      reader.readAsDataURL(input.files[0]);
    },

    validateCode: function() {
      let validationCode = $("#validationCode").val();

      if (validationCode.length > 10) {
        let formData = new FormData();
        formData.append("code", validationCode);

        $.ajax({
          url: "/api/validate/",
          method: "POST",
          data: formData,
          cache: false,
          contentType: false,
          processData: false,
          success: function(data) {
            app.getValidation();
            toast.success(null, data.message, false);
          },
          error: toast.defaultAjaxError
        });
      }
    },

    resendValidation: function() {
      $.ajax({
        url: "/api/validate/resend/",
        method: "POST",
        cache: false,
        contentType: false,
        processData: false,
        success: function(data) {
          toast.info(null, data.message, true);
        },
        error: toast.defaultAjaxError
      });
    },

    changeDetails: function(type) {
      let val = "";

      switch (type) {
        case "email":
          val = $("#emailInput").val();
          $("#changeEmailButton").attr("disabled", true);
          break;
        case "password":
          val = $("#passwordInput1").val();
          $("#changePasswordButton").attr("disabled", true);
          if (val.length < 6) {
            toast.error(null, "Password must be at least 6 characters in length.", true);
            return;
          }

          break;
      }

      let formData = new FormData();
      formData.append("type", type);
      formData.append("value", val);

      $.ajax({
        url: "/api/validate/details/",
        method: "POST",
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(data) {
          app.getValidation();
          toast.info(null, data.message, true);
        },
        error: toast.defaultAjaxError
      });
    },

    cancelValidationRequest: function() {
      $.ajax({
        url: "/api/validate/cancel/",
        method: "POST",
        success: function(data) {
          app.getValidation();
          toast.success(null, data.message, false);
        },
        error: toast.defaultAjaxError
      });
    },

    getRegisteredDuration: function() {
      let now = moment(new Date().getTime());
      let registered = moment(this.user.registerDate);

      let diff = now.diff(registered, "days");
      app.registerDuration = diff;
    },

    millisToDate: function(millis) {
      return moment(millis).format("dddd Do MMMM YYYY - HH:mm");
    }
  }
});

function mountBackgroundColorPicker() {
  // these colors are set manually in the modal, here and in JS below.
  // we should probably centralize them somewhere.
  // var bgColor = "f0e9e9";
  // var fgColor = "8b5d5d";
  // var useBold = false;
  // var useUppercase = false;

  $("#backgroundColorSelector").ColorPicker({
    color: "#f0e9e9",
    onShow: function(colpkr) {
      $(colpkr).fadeIn(100);
      return false;
    },
    onHide: function(colpkr) {
      $(colpkr).fadeOut(100);
      return false;
    },
    onChange: function(hsb, hex, rgb) {
      $("#backgroundColorSelector div").css("backgroundColor", "#" + hex);
      app.system_avatar.backgroundColor = hex;
    }
  });
}

function mountForegroundColorPicker() {
  $("#fontColorSelector").ColorPicker({
    color: "#8b5d5d",
    onShow: function(colpkr) {
      $(colpkr).fadeIn(100);
      return false;
    },
    onHide: function(colpkr) {
      $(colpkr).fadeOut(100);
      return false;
    },
    onChange: function(hsb, hex, rgb) {
      $("#fontColorSelector div").css("backgroundColor", "#" + hex);
      app.system_avatar.foregroundColor = hex;
    }
  });
}

function mountSystemAssignedCheckboxes() {
  $("#uppercaseSystemAvatar").click(function() {
    app.system_avatar.uppercase = $(this).is(":checked");
  });

  $("#boldSystemAvatar").click(function() {
    app.system_avatar.bold = $(this).is(":checked");
  });
}