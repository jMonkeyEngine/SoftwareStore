var app = new Vue({
	el: "#app",
	data: {
		search: {
			categoryId: -1,
			title: "",
			tag: "",
			author: "",
			orderBy: "updated",
			direction: "descending",
			page: 0
		},

		// results: [],
		searchResult: {},
		searched: false
	},

	mounted: function() {
		// pull data from the url path

		$(".tooltip").popup();

		let searchOnLoad = false;

		let searchParams = new URLSearchParams(window.location.search);

		if (searchParams.has("categoryId")) {
			this.search.categoryId = parseInt(searchParams.get("categoryId"));
			searchOnLoad = true;
		}

		if (searchParams.has("title")) {
			this.search.title = searchParams.get("title");
			searchOnLoad = true;
		}

		if (searchParams.has("tag")) {
			this.search.tag = searchParams.get("tag");
			searchOnLoad = true;
		}

		if (searchParams.has("author")) {
			this.search.author = searchParams.get("author");
			searchOnLoad = true;
		}

		if (searchParams.has("orderBy")) {
			this.search.orderBy = searchParams.get("orderBy");
			searchOnLoad = true;
		}

		if (searchParams.has("direction")) {
			this.search.direction = searchParams.get("direction");
			searchOnLoad = true;
		}

		let vue = this;

		// update the props for the dropdowns when they're changed.
		$("#categoriesDropdown").dropdown("setting", "onChange", function(index, text, choice) {
			vue.search.categoryId = index;
		});

		$("#orderByDropdown").dropdown("setting", "onChange", function(index, text, choice) {
			vue.search.orderBy = index;
		});

		$("#directionDropdown").dropdown("setting", "onChange", function(index, text, choice) {
			vue.search.direction = index;
		});

		// set the data.

		// category
		$("#categoriesDropdown").dropdown("set selected", this.search.categoryId);
		$("#orderByDropdown").dropdown("set selected", this.search.orderBy);
		$("#directionDropdown").dropdown("set selected", this.search.direction);

		// start searching on load if required
		if (searchOnLoad) {
			this.beginSearch();
		}
	},

	methods: {
		beginSearch() {
			this.searched = false;

			$.ajax({
				url: "/api/search/",
				method: "GET",
				data: this.search,
				success: function(data) {
					app.searchResult = data;
					app.searched = true;

					// var params = jQuery.param(app.search);
					// window.history.pushState(null, null, "/search/?" + params);
				},
				error: toast.defaultAjaxError
			});
		}
	}
});
