var app = new Vue({
	el: "#app",

	data: {
		categoryName: "",
		pages: []
	},

	mounted: function() {
		this.categoryName = $("#categoryName").val();
		this.getCategoryPages();
	},

	methods: {
		getCategoryPages: function() {
			let data = {
				category: this.categoryName
			};

			$.ajax({
				url: "/api/blob/",
				method: "GET",
				data: data,
				success: function(data) {
					app.pages = data.categories[0].pages;
				},
				error: toast.defaultAjaxError
			});
		}
	}
});
