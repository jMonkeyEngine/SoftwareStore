// force the page to re-load if the back button was used
$(window).bind("pageshow", function(event) {
	if (event.originalEvent.persisted) {
		window.location.reload();
	}
});

var app = new Vue({
	el: "#app",
	data: {
		pages: []
	},

	mounted: function() {
		this.getAllPages();
	},

	methods: {
		getAllPages: function() {
			$.ajax({
				url: "/api/page/user/all/",
				method: "GET",
				success: function(data) {
					app.pages = data;
				}
			});
		},

		millisToDate: millisToDate,

		onCreateAmendment: function(pageId) {
			let formData = new FormData();
			formData.append("pageId", pageId);

			$.ajax({
				url: "/api/page/amendment/",
				method: "POST",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					window.location.href = "/edit/amendment/" + data.id;
				},
				error: toast.defaultAjaxError
			});
		},

		onDeleteAmendmentClicked: deleteAmendment,
		onDeleteDraftClicked: deleteDraft
	},

	updated: function() {
		$("#brand-name").addClass("active");
		$(".tooltip")
			.popup("destroy")
			.popup();
	}
});
