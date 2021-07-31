$("#menu-users").addClass("active");

var app = new Vue({
	el: "#app",
	data: {
		users: null,
		recentUsers: []
	},

	mounted: function() {
		this.getUsersSince();
	},

	methods: {
		findUsers(searchTerm) {
			if (searchTerm.length < 1) {
				return false;
			}

			$.ajax({
				url: "/api/user/search/" + searchTerm,
				method: "GET",
				success: function(data) {
					app.users = data;
				},
				error: toast.defaultAjaxError
			});
		},

		getUsersSince: function() {
			let timeLength = Date.now() - 86400000; // 24 hours

			$.ajax({
				url: "/api/user/since/" + timeLength,
				method: "GET",
				success: function(data) {
					app.recentUsers = data;
				},
				error: toast.defaultAjaxError
			});
		},

		displayNewUserModal: function() {
			$("#newUserModal")
				.modal("destroy")
				.modal({
					onApprove: function() {
						let form = $("#newUserForm");
						let formData = new FormData(form[0]);

						$.ajax({
							url: "/api/user/create/",
							method: "POST",
							data: formData,
							cache: false,
							contentType: false,
							processData: false,
							success: function(data) {
								app.recentUsers.unshift(data);
							},
							error: toast.defaultAjaxError
						});
					}
				})
				.modal("show");
		},

		millisToDate: millisToDate
	}
});
