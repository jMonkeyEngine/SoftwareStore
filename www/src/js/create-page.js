$(document).ready(function(event) {
	$("#asset-store-link").addClass("active");
	$(".checkbox").checkbox();

	$(".softwareType").checkbox("setting", "onChange", function() {
		toggleRepostoryOptionsVisibility(this);
	});
});

function toggleRepostoryOptionsVisibility(elem) {
	let attrVal = $(elem).attr("value");
	let visible = attrVal == "opensource" || attrVal == "sponsored";

	if (visible) {
		$("#git-repository-data").css("display", "initial");
	} else {
		$("#git-repository-data").css("display", "none");
	}
}

$("#submitAssetButton").click(function() {
	let formElement = $("#newAssetForm").get(0);
	let formData = new FormData(formElement);

	$.ajax({
		url: "/api/page/draft/",
		method: "POST",
		data: formData,
		cache: false,
		contentType: false,
		processData: false,
		success: function(data) {
			let assetId = data.id;
			window.location.href = "/edit/draft/" + assetId;
		},
		error: function(xhr, status, error) {
			let message = "<p>" + xhr.responseJSON.message + "</p>";
			message += toast.arrayToHtmlList(xhr.responseJSON.details);

			toast.error(null, message, true);
		}
	});
});

$("#forkedCheckBox").checkbox();
$("#forkedCheckBox").click(function() {
	// this is the opposite value because it evaluates before the checkbox has changed.
	let checked = !$("#forkedCheckBox").checkbox("is checked");

	if (checked) {
		// console.log("checked");
		$("#parentRepoInput").removeClass("hidden");
	} else {
		// console.log("unchecked");
		$("#parentRepoInput").addClass("hidden");
	}
});
