// force the page to re-load if the back button was used
$(window).bind("pageshow", function(event) {
	if (event.originalEvent.persisted) {
		window.location.reload();
	}
});

var app = new Vue({
	el: "#app",
	data: {
		userId: -1,

		drafts: [],
		amendments: [],

		jobs: [],

		page_search_results: null
	},

	mounted: function() {
		this.userId = $("#userId").val();

		this.getAllPages();
	},

	methods: {
		getAllPages: function() {
			$.ajax({
				url: "/api/page/draft/pending/",
				method: "GET",
				success: function(data) {
					app.drafts = data;
				},
				error: toast.defaultAjaxError
			});

			$.ajax({
				url: "/api/page/amendment/pending/",
				method: "GET",
				success: function(data) {
					app.amendments = data;
				},
				error: toast.defaultAjaxError
			});
		},

		takeJob: function(pageState, pageId, index) {
			let formData = new FormData();
			formData.append("pageId", pageId);

			$.ajax({
				url: "/api/approve/job/" + pageState + "/",
				method: "POST",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					app.getAllPages();
					toast.success(null, "You are now reviewing the selected page.", false);
				},
				error: toast.defaultAjaxError
			});
		},

		approveDraft: function(pageId, index) {
			let formData = new FormData();
			formData.append("pageId", pageId);

			$.ajax({
				url: "/api/page/approve/draft/accept/",
				method: "POST",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					app.drafts.splice(index, 1);
					toast.success(null, "Draft approved successfully.", false);
				},
				error: toast.defaultAjaxError
			});
		},

		rejectDraft: function(pageId, index) {
			$("#RejectDraftModal")
				.modal("destroy")
				.modal({
					closeable: false,

					onApprove: function() {
						let formData = new FormData();
						formData.append("pageId", pageId);
						formData.append("reason", $("#RejectDraftReason").val());

						$.ajax({
							url: "/api/reject/draft/",
							method: "POST",
							data: formData,
							cache: false,
							contentType: false,
							processData: false,
							success: function(data) {
								app.drafts[index].reviewState = "Rejected";
								toast.info(null, "Draft rejected successfully.", false);
							},
							error: toast.defaultAjaxError
						});
					}
				})
				.modal("show");
		},

		deleteDraft: function(pageId, index) {
			$("#DeleteDraftModal")
				.modal("destroy")
				.modal({
					closeable: false,

					onApprove: function() {
						let formData = new FormData();
						formData.append("pageId", pageId);
						// formData.append("reason", $("#RejectDraftReason").val());

						$.ajax({
							url: "/api/page/draft/",
							method: "DELETE",
							data: formData,
							cache: false,
							contentType: false,
							processData: false,
							success: function(data) {
								app.drafts.splice(index, 1);
								toast.info(null, "Draft deleted successfully.", false);
							},
							error: toast.defaultAjaxError
						});
					}
				})
				.modal("show");
		},

		approveAmendment: function(pageId, index) {
			let formData = new FormData();
			formData.append("pageId", pageId);

			$.ajax({
				url: "/api/page/approve/amendment/accept/",
				method: "POST",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					app.amendments.splice(index, 1);
					toast.success(null, "Amendment approved successfully.", false);
				},
				error: toast.defaultAjaxError
			});
		},

		rejectAmendment: function(pageId, index) {
			$("#rejectAmendmentModal")
				.modal("destroy")
				.modal({
					closeable: false,

					onApprove: function() {
						let formData = new FormData();
						formData.append("pageId", pageId);
						formData.append("reason", $("#RejectAmendmentReason").val());

						$.ajax({
							url: "/api/reject/amendment/",
							method: "POST",
							data: formData,
							cache: false,
							contentType: false,
							processData: false,
							success: function(data) {
								app.amendments[index].reviewState = "Rejected";
								toast.info(null, "Amendment rejected successfully.", false);
							},
							error: toast.defaultAjaxError
						});
					}
				})
				.modal("show");
		},

		deleteAmendment: function(pageId, index) {
			$("#DeleteAmendmentModal")
				.modal("destroy")
				.modal({
					closeable: false,

					onApprove: function() {
						let formData = new FormData();
						formData.append("pageId", pageId);
						// formData.append("reason", $("#DeleteAmendmentReason").val());

						$.ajax({
							url: "/api/page/amendment/",
							method: "DELETE",
							data: formData,
							cache: false,
							contentType: false,
							processData: false,
							success: function(data) {
								app.amendments.splice(index, 1);
								toast.info(null, "Amendment deleted successfully.", false);
							},
							error: toast.defaultAjaxError
						});
					}
				})
				.modal("show");
		},

		searchPages: function() {
			let searchTerm = $("#searchPagesInput").val();

			if (searchTerm.length > 0) {
				let searchUrl = "/api/page/search/title/" + searchTerm;

				$.ajax({
					url: searchUrl,
					method: "GET",
					success: function(data) {
						app.page_search_results = data;
					},
					error: toast.defaultAjaxError
				});
			}
		},

		deleteLivePage: function(pageId, index) {
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
								app.page_search_results.splice(index, 1);
								toast.info(null, "Live Store Page deleted successfully.", false);
							},
							error: toast.defaultAjaxError
						});
					}
				})
				.modal("show");
		},

		millisToDate: function(millis) {
			return moment(millis).format("dddd Do MMMM YYYY - HH:mm");
		}
	},
	updated: function() {
		$(".tooltip")
			.popup("destroy")
			.popup();
	}
});

$("#menu-pages").addClass("active");
