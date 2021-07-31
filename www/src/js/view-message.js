var app = new Vue({
	el: "#ViewMessage",

	data: {
		message: [],
		replies: []
	},

	mounted: function() {
		this.fetchMessage();
	},

	methods: {
		fetchMessage: function() {
			let messageId = window.location.href.substr(window.location.href.lastIndexOf("/") + 1);
			// console.log("messageId: " + messageId);

			// get the primary message
			$.ajax({
				url: "/api/messages/" + messageId,
				method: "GET",

				// if successful, get the replies.
				success: message => {
					this.message = message;

					$.ajax({
						url: "/api/messages/replies/" + messageId,
						method: "GET",
						success: replies => {
							this.replies = replies;
							$(".ui.active.dimmer").removeClass("active");
						}
					});
				}
			});
		}
	}
});

/* START reply to message */

$("#ReplyButton").click(function() {
	$("#NewReplyModal").modal("show");
});

$("#PostReplyButton").click(function() {
	let replyData = {
		messageId: $("#messageId").val(),
		content: $("#replyContent").val()
	};

	replyData.content = replyData.content.trim();

	let animationName = "shake";

	let badData = false;

	if (replyData.content.length == 0) {
		$("#replyContent").transition(animationName);
		badData = true;
	}

	if (badData) {
		return false;
	}

	//replyData = JSON.stringify(replyData);
	//console.log("Reply Data: " + replyData);

	$.ajax({
		url: "/api/messages/reply/",
		method: "POST",
		data: replyData,
		//contentType: "application/json; charset=utf-8",
		//dataType: "json",
		success: function(newReply) {
			app._data.replies.push(newReply);
			$("#NewReplyModal").modal("hide");
		}
	});
});

/* END reply to message */
