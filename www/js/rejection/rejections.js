var app = new Vue({
	el: "#app",

	data: {
		pageId: "",
		pageState: "",

		page: {},
		rejections: [],

		loaded: false
	},

	mounted: function() {
		this.pageId = $("#pageId").val();
		this.pageState = $("#pageState").val();

		this.getPage();
	},

	methods: {
		getPage: function() {
			let url = "/api/page/";

			switch (this.pageState) {
				case "Draft":
					url += "draft/";
					break;
				case "Amendment":
					url += "amendment/";
			}

			$.ajax({
				url: url + this.pageId,
				method: "GET",
				success: function(data) {
					app.page = data;
					app.getRejections();
				},
				error: toast.defaultAjaxError
			});
		},

		getRejections: function() {
			let url = "/api/page/";

			if (this.pageState === "Draft") {
				url += "draft/";
			} else if (this.pageState === "Amendment") {
				url += "amendment/";
			}

			url += "rejections/" + this.pageId;

			$.ajax({
				url: url,
				method: "GET",
				success: function(data) {
					app.rejections = data;
					app.loaded = true;
				},
				complete: function(jqXHR, textStatus) {
					$("#pageLoader").removeClass("active");
				},
				error: toast.defaultAjaxError
			});
		},

		millisToDate: millisToDate
	}
});
