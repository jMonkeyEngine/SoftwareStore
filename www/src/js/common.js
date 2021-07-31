$("#sideMenuButton, #closeSideMenuButton").click(function() {
	$(".ui.sidebar")
		.sidebar("setting", "transition", "overlay")
		.sidebar("toggle");
});

/* START asset search */

/*
$("#topMenuSearch").focus(function() {
	$("#topSearchContainer").animate({ width: "300px" });
});

$("#topMenuSearch").focusout(function() {
	$("#topSearchContainer").animate({ width: "150px" });
});
*/

$("#topSearchContainer").search({
	minCharacters: 3,
	searchDelay: 500,
	searchOnFocus: false,

	apiSettings: {
		url: "/api/search/?title={query}",

		onResponse: function(serverResponse) {
			var response = {
				results: []
			};

			$.each(serverResponse.content, function(index, asset) {
				response.results.push({
					title: asset.details.title,
					description: asset.owner.username,
					image: "/image/" + asset.mediaLinks.imageIds.split(",")[0],
					actionURL: "/" + asset.id,
					actionText: "asdasdasd"
				});
			});

			return response;
		}
	},
	onSelect: function(result, response) {
		window.location = result.actionURL;
	}
});

/* END asset search */

function millisToDate(millis) {
	return moment(millis).format("dddd Do MMMM YYYY - HH:mm");
}
