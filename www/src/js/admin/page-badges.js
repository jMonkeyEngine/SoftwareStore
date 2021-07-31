var app = new Vue({
	el: "#app",

	data: {
		badges: []
	},

	mounted: function() {
		this.getBadges();
		mountBadges();
	},

	methods: {
		getBadges: function() {
			$.ajax({
				url: "/api/badges/",
				method: "GET",
				success: function(data) {
					app.badges = data;
				},
				error: toast.defaultAjaxError
			});
		},

		createNewBadge: function() {
			$("#badgesForm input[name=id]").val("-1");
			$("#badgesForm input[name=name]").val("");
			$("#badgesForm input[name=description]").val("");
			$("#badgesForm input[name=icon]").val("");
		},

		postBadgeData: function() {
			let formElement = $("#badgesForm").get(0);
			let formData = new FormData(formElement);

			let isNewBadge = $("#badgesForm input[name=id]").val() == "-1";

			let method = "";
			let action = "";

			if (isNewBadge) {
				method = "POST";
				action = "created";
			} else {
				method = "PUT";
				action = "updated";
			}

			$.ajax({
				url: "/api/badges/",
				method: method,
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					if (isNewBadge) {
						app.badges.push(data);
					} else {
						for (let i = 0; i < app.badges.length; i++) {
							if (app.badges[i].id == data.id) {
								app.badges[i].name = data.name;
								break;
							}
						}
					}

					toast.success(null, "Badge " + action + " successfully.", false);
				},
				error: toast.defaultAjaxError
			});
		}
	},

	updated: function() {}
});

function mountBadges() {
	$("#badgesDropdown").dropdown("setting", "onChange", function(val) {
		let badge = app.badges[val];

		$("#badgesForm input[name=id]").val(badge.id);
		$("#badgesForm input[name=name]").val(badge.name);
		$("#badgesForm input[name=description]").val(badge.description);
		$("#badgesForm input[name=icon]").val(badge.icon);
	});
}

$("#menu-badges").addClass("active");
