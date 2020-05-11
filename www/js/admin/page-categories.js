let app = new Vue({
	el: "#app",

	data: {
		categories: []
	},

	mounted: function() {
		this.getCategories();

		$("#categoriesDropdown").dropdown("setting", "onChange", function(index, text, $choice) {
			app.selectedCategory = index;

			let cat = app.categories[index];

			if (cat.parent != null) {
				console.log("parent not null");

				for (let i = 0; i < app.categories.length; i++) {
					if (app.categories[i].name == cat.parent.name) {
						$("#categoriesParentDropdown").dropdown("set selected", i);
						break;
					}
				}
			} else {
				console.log("parent null");
				$("#categoriesParentDropdown").dropdown("clear");
			}
		});

		$("#categoriesParentDropdown").dropdown("setting", "onChange", function(index, text, $choice) {
			app.selectedCategory = index;
		});
	},

	updated: function() {},

	methods: {
		getCategories: function() {
			let data = {
				category: "all"
			};

			$.ajax({
				url: "/api/blob/",
				method: "GET",
				data: data,
				success: function(data) {
					app.categories = data.categories;
				},
				error: toast.defaultAjaxError
			});
		},

		createNewCategory: function(event, index, parent) {
			let parentId = parent == null ? -1 : parent.id;

			let formData = new FormData();
			formData.append("name", "New Category");
			formData.append("parentId", parentId);

			$.ajax({
				url: "/api/category/",
				method: "POST",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					app.categories.push(data);
					toast.success(null, "Category created: " + data.name, false);
				},
				error: toast.defaultAjaxError
			});
		},

		getChildCount: function(category) {
			let count = 0;

			for (let i = 0; i < this.categories.length; i++) {
				if (this.categories[i].parent != null && this.categories[i].parent.id == category.id) {
					count++;
				}
			}

			return count;
		},

		mouseEnterCategory: function(event) {
			$(event.target)
				.children("div")
				.removeClass("transition hidden");
		},

		mouseLeaveCategory: function(event) {
			$(event.target)
				.children("div")
				.addClass("transition hidden");
		},

		mouseLeaveCategoryTextField: function(event) {
			$(event.target).addClass("transition hidden");

			$(event.target)
				.parent()
				.children("div")
				.first()
				.removeClass("transition hidden");
		},

		editCategory: function(event) {
			$(event.target)
				.parent()
				.addClass("transition hidden");

			$(event.target)
				.parent()
				.parent()
				.children("div:nth-child(2)")
				.last()
				.removeClass("transition hidden");
		},

		updateSelectedCategory: function(category, index) {
			let formData = new FormData();
			formData.append("id", category.id);
			formData.append("name", category.name);
			formData.append("parentId", category.parent == null ? -1 : category.parent.id);

			$.ajax({
				url: "/api/category/",
				method: "PUT",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					app.categories[index] = data;
					toast.success(null, "Category updated: " + data.name, false);
				},
				error: toast.defaultAjaxError
			});
		},

		deleteCategory: function(category, index) {
			let formData = new FormData();
			formData.append("id", category.id);

			$.ajax({
				url: "/api/category/",
				method: "DELETE",
				data: formData,
				cache: false,
				contentType: false,
				processData: false,
				success: function(data) {
					app.categories.splice(index, 1);
					$("#categoriesDropdown").dropdown("clear");
					toast.success(null, "Category deleted: " + data.name, false);
				},
				error: toast.defaultAjaxError
			});
		}
	}
});
