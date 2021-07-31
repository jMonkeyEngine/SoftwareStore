// force the page to re-load if the back button was used
$(window).bind("pageshow", function(event) {
	if (event.originalEvent.persisted) {
		window.location.reload();
	}
});

$("#brand-name").addClass("active");

var app = new Vue({
	el: "#MessagesPage",

	data: {
		messages: []
	},

	mounted: function() {
		this.fetchMessages();
	},

	methods: {
		fetchMessages: function() {
			$.ajax({
				url: "/api/messages/",
				method: "GET"
			}).done(responseData => {
				this.messages = responseData;
			});
		}
	}
});

$("#NewMessageButton").click(function() {
	$("#NewMessageModal")
		.modal({
			closeable: false
		})
		.modal("show");
});

$("#SendNewMessageButton").click(function() {
	let messageData = {
		recipient: $("#NewMessageUsername").val(),
		title: $("#NewMessageTitle").val(),
		content: $("#NewMessageContent").val()
	};

	console.log(messageData);

	messageData.recipient = messageData.recipient.trim();
	messageData.title = messageData.title.trim();
	messageData.content = messageData.content.trim();

	var badData = false;

	var animationName = "shake";

	if (messageData.recipient.length == 0) {
		$("#NewMessageUsername").transition(animationName);
		console.log("Username empty");
		badData = true;
	}

	if (messageData.title.length == 0) {
		$("#NewMessageTitle").transition(animationName);
		badData = true;
	}

	if (messageData.content.length == 0) {
		$("#NewMessageContent").transition(animationName);
		badData = true;
	}

	if (badData) {
		return false;
	}

	$.ajax({
		url: "/api/messages/",
		method: "POST",
		data: messageData,
		success: function(responseData) {
			$("#NewMessageModal").modal("hide");
		}
	});
});

$(document).ready(function() {
	$(".ui.search").search({
		minCharacters: 3,
		searchDelay: 500,
		searchOnFocus: false,

		apiSettings: {
			url: "/api/user/search/{query}",

			onResponse: function(serverResponse) {
				var response = {
					results: []
				};

				$.each(serverResponse, function(index, user) {
					response.results.push({
						title: user.username
					});
				});

				return response;
			}
		}
	});
});
