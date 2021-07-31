$(document).ready(function() {
	$(".ui.form").form({
		fields: {
			email: {
				identifier: "username",
				rules: [
					{
						type: "length[5]",
						prompt: "Your username must be at least 5 characters long."
					}
				]
			},
			password: {
				identifier: "password",
				rules: [
					{
						type: "length[6]",
						prompt: "Your password must be at least 6 characters long."
					}
				]
			}
		}
	});
});
