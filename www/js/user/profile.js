var app = new Vue({
  el: "#app",

  data: {
    user: {},
    assets: {},
    pageStats: {
      userProfile: {},
      registerDuration: 0,
      pageCount: 0,
      reviewCount: 0,
      averateRating: 0.0
    }
  },

  mounted: function() {
    this.getUser();
  },

  updated: function() {
    $(".tooltip")
      .popup("destroy")
      .popup();
  },

  methods: {
    getUser: function() {
      let userId = window.location.href.substr(window.location.href.lastIndexOf("/") + 1);

      $.ajax({
        url: "/api/page/stats/" + userId,
        method: "GET",
        success: function(data) {
          app.pageStats = data;
          app.pageStats.registerDuration = app.getRegisteredDuration(data.userProfile.registerDate);

        },
        error: toast.defaultAjaxError
      });
    },

    getRegisteredDuration: function(registerDate) {
      let now = moment(new Date().getTime());
      let registered = moment(registerDate);

      let diff = now.diff(registered, "days");
      return diff;
    }
  }
});