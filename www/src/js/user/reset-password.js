var app = new Vue({
	el: "#app",

	data: {
		sent: false
	},

	methods: {
		sendRequest: function() {
			let formElement = $("#reset-password-form").get(0);
			let formData = new FormData(formElement);

			$.ajax({
				url: "/api/validate/reset-password/",
				method: "POST",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					app.sent = true;
					toast.info(null, data.message, true);
				},
				error: toast.defaultAjaxError
			});
		}
	}
});
