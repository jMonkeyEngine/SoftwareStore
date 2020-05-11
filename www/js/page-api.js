function deleteAmendment(pageId, index) {
  $("#deleteAmendmentModal")
    .modal("destroy")
    .modal({
      closeable: false,
      onApprove: function() {
        let formData = new FormData();
        formData.append("pageId", pageId);

        $.ajax({
          url: "/api/page/amendment/",
          method: "DELETE",
          data: formData,
          cache: false,
          contentType: false,
          processData: false,
          success: function(data) {
            app.pages.amendments.splice(index, 1);
            toast.info(null, "Amendment deleted successfully.", false);
          },
          error: toast.defaultAjaxError
        });
      }
    })
    .modal("show");
}

function deleteLivePage(pageId, index) {
  $("#deleteLivePageModal")
    .modal("destroy")
    .modal({
      closeable: false,

      onApprove: function() {
        let formData = new FormData();
        formData.append("pageId", pageId);

        $.ajax({
          url: "/api/page/",
          method: "DELETE",
          data: formData,
          cache: false,
          contentType: false,
          processData: false,
          success: function(data) {
            app.pages.drafts.splice(index, 1);
            toast.info(null, "Draft deleted successfully.", false);
          },
          error: toast.defaultAjaxError
        });
      }
    })
    .modal("show");
}

function deleteDraft(pageId, index) {
  $("#deleteDraftModal")
    .modal("destroy")
    .modal({
      closeable: false,

      onApprove: function() {
        let formData = new FormData();
        formData.append("pageId", pageId);

        $.ajax({
          url: "/api/page/draft/",
          method: "DELETE",
          data: formData,
          cache: false,
          contentType: false,
          processData: false,
          success: function(data) {
            app.pages.drafts.splice(index, 1);
            toast.info(null, "Draft deleted successfully.", false);
          },
          error: toast.defaultAjaxError
        });
      }
    })
    .modal("show");
}

function giveUserData(data) {
  return data;
}

function getUserStats(userId, callback) {
  $.ajax({
    url: "/api/page/stats/" + userId,
    method: "GET",
    success: function(data) {
      // app.pageStats = data;
      // app.pageStats.registerDuration = dateToString(data.userProfile.registerDate);
      // return (data);
      callback(data);
    },
    error: toast.defaultAjaxError
  });
}


function dateToDays(javaDate) {
  let now = moment(new Date().getTime());
  let registered = moment(javaDate);

  let diff = now.diff(registered, "days");
  return diff;
}