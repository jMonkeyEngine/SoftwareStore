let toastPosition = "bottom left";
let progressPosition = "bottom";
let displayTime = 5000;

module.exports = class toast {
	static defaultAjaxError(xhr, status, error) {
		let title = "";
		let message = "";

		if (xhr.status == 404) {
			title = "Endpoint Error: 404";
			message = "The endpoint could not be found.";
			toast.error(title, message, true);
		} else {
			message = xhr.responseJSON.message;

			if (xhr.responseJSON.details != null) {
				message = "<p>" + message + "</p>";
				message += toast.arrayToHtmlList(xhr.responseJSON.details);
			}

			toast.error(null, message, true);
		}
	}

	static info(title, message, fixed) {
		this.showToast(title, message, fixed, "info");
	}

	static success(title, message, fixed) {
		this.showToast(title, message, fixed, "success");
	}

	static error(title, message, fixed) {
		this.showToast(title, message, fixed, "error");
	}

	static showToast(title, message, fixed, className) {
		let options = {
			displayTime: fixed ? 0 : displayTime,
			showProgress: progressPosition,
			position: toastPosition,
			compact: false
		};

		if (title != null) {
			options.title = title;
		}

		if (className != null) {
			options.class = className;
		}

		options.message = message;

		$("body").toast(options);
	}

	static arrayToHtmlList(input) {
		let message = "<div class='ui bulleted list'>";

		for (let i = 0; i < input.length; i++) {
			message += "<div class='item'>" + input[i] + "</div>";
		}

		message += "</div>";

		return message;
	}
}
