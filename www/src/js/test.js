import Vue from "vue";
import { Carousel3d, Slide } from 'vue-carousel-3d';
import { toast } from "/src/js/toast.js";
import 'highlight.js/styles/monokai-sublime.css';

const app = new Vue({

	el: '#app',

	data: {
		assets: {
			showcase: [],
			highest_rated: [],
			new_additions: [],
			recently_updated: []
		}

  },

	components: {
    'carousel-3d': Carousel3d,
    'slide': Slide
  },

	 mounted: function() {
		this.getTopAssets();
 	},

		methods: {

			getTopAssets: function() {
				$.ajax({
					url: "/api/page/top/",
					method: "GET",
					success: data => {

						app.assets.showcase = data.showcase;
						app.assets.highest_rated = data.highest_rated;
						app.assets.new_additions = data.new_additions;
						app.assets.recently_updated = data.recently_updated;

					},
					error: toast.defaultAjaxError,
					complete: function(jqXHR, textStatus) {
						$("#pageLoader").removeClass("active");
					}
				});
			},

		}

})
