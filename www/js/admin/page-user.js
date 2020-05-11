$("#menu-users").addClass("active");

var app = new Vue({
	el: "#app",
	data: {
		user: {},
		user_email: "",
		all_badges: [],
		filtered_badges: [],

		selected_grant: -1,
		selected_revoke: -1,

		pages: []
	},

	mounted: function() {
		this.getUser();
	},

	methods: {
		getUser() {
			let username = $("#username").val();

			$.ajax({
				url: "/api/user/" + username,
				method: "GET",
				success: function(data) {
					app.user = data;
					app.getAllBadges();
					app.getAllPages();
				},
				error: toast.defaultAjaxError
			});
		},

		getAllPages: function() {
			$.ajax({
				url: "/api/page/user/all/" + app.user.id,
				method: "GET",
				success: function(data) {
					app.pages = data;
				},
				error: toast.defaultAjaxError
			});
		},

		requestUserEmail: function(userId) {
			$.ajax({
				url: "/api/user/email/" + userId,
				method: "GET",
				success: function(data) {
					app.user_email = data.message;
				},
				error: toast.defaultAjaxError
			});
		},

		getAllBadges() {
			$.ajax({
				url: "/api/badges/",
				method: "GET",
				success: function(data) {
					app.all_badges = data;
					app.filterBadges(data);
				},
				error: toast.defaultAjaxError
			});
		},

		// gets all badges that the user does not have.
		filterBadges(data) {
			var badge_filter = [];

			for (let i = 0; i < this.all_badges.length; i++) {
				let addme = true;

				if (this.user.badges) {
					for (let j = 0; j < this.user.badges.length; j++) {
						if (this.user.badges[j].id == this.all_badges[i].id) {
							addme = false;
						}
					}

					if (addme) {
						badge_filter.push(this.all_badges[i]);
					}
				} else {
					for (let i = 0; i < this.all_badges.length; i++) {
						badge_filter.push(this.all_badges[i]);
					}
				}
			}

			this.filtered_badges = badge_filter;
		},

		grantBadge() {
			if (this.selected_grant == -1) {
				return;
			}

			let badge = this.filtered_badges[this.selected_grant];

			let formData = new FormData();
			formData.append("userId", app.user.id);
			formData.append("badgeId", badge.id);

			$.ajax({
				url: "/api/badges/grant/",
				method: "POST",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					app.filtered_badges.splice(app.selected_grant, 1);
					app.user.badges.push(badge);
					app.selected_grant = -1;
					$("#badgeGrants").dropdown("clear");
					toast.info(null, "Badge Granted: " + badge.name, false);
				},
				error: toast.defaultAjaxError
			});
		},

		revokeBadge() {
			if (this.selected_revoke == -1) {
				return;
			}

			let badge = this.user.badges[this.selected_revoke];

			let formData = new FormData();
			formData.append("userId", app.user.id);
			formData.append("badgeId", badge.id);

			$.ajax({
				url: "/api/badges/revoke/",
				method: "POST",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					app.user.badges.splice(app.selected_revoke, 1);
					app.filtered_badges.push(data);
					app.selected_revoke = -1;
					$("#badgeRevokes").dropdown("clear");
					toast.info(null, "Badge Revoked: " + badge.name, false);
				},
				error: toast.defaultAjaxError
			});
		},

		// delete

		deleteDraft: deleteDraft,
		deleteLivePage: deleteLivePage,
		deleteAmendment: deleteAmendment,

		millisToDate: millisToDate
	}
});

$("#badgeGrants").dropdown({
	action: "hide",
	onChange: function(value, text, $selectedItem) {
		let selectedBadge = app.filtered_badges[value];
		$("#badgeGrants").dropdown("set selected", value);

		app.selected_grant = value;
	}
});

$("#badgeRevokes").dropdown({
	action: "hide",
	onChange: function(value, text, $selectedItem) {
		let selectedBadge = app.filtered_badges[value];
		$("#badgeRevokes").dropdown("set selected", value);
		app.selected_revoke = value;
	}
});
