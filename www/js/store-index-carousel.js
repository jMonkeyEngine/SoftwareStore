function mountMainCarousel() {
	!(function() {
		"use strict";
		for (
			var e,
				t =
					"function" == typeof Symbol && "symbol" == typeof Symbol.iterator
						? function(e) {
								return typeof e;
						  }
						: function(e) {
								return e && "function" == typeof Symbol && e.constructor === Symbol && e !== Symbol.prototype ? "symbol" : typeof e;
						  },
				n = new ((function(e) {
					!(function() {
						var n, i;

						function o(e) {
							return void 0 === this || Object.getPrototypeOf(this) !== o.prototype
								? new o(e)
								: (((n = this).version = "3.4.0"),
								  (n.tools = new w()),
								  n.isSupported()
										? (n.tools.extend(n.defaults, e || {}),
										  (n.defaults.container = r(n.defaults)),
										  (n.store = {
												elements: {},
												containers: []
										  }),
										  (n.sequences = {}),
										  (n.history = []),
										  (n.uid = 0),
										  (n.initialized = !1))
										: "undefined" != typeof console && null !== console && console.log("ScrollReveal is not supported in this browser."),
								  n);
						}

						function r(e) {
							if (e && e.container) {
								if ("string" == typeof e.container) return window.document.documentElement.querySelector(e.container);
								if (n.tools.isNode(e.container)) return e.container;
								console.log('ScrollReveal: invalid container "' + e.container + '" provided.'),
									console.log("ScrollReveal: falling back to default container.");
							}
							return n.defaults.container;
						}

						function s() {
							return ++n.uid;
						}

						function a(e, t, i) {
							t.container && (t.container = i),
								e.config ? (e.config = n.tools.extendClone(e.config, t)) : (e.config = n.tools.extendClone(n.defaults, t)),
								"top" === e.config.origin || "bottom" === e.config.origin ? (e.config.axis = "Y") : (e.config.axis = "X");
						}

						function l(e) {
							var t = window.getComputedStyle(e.domEl);
							e.styles ||
								((e.styles = {
									transition: {},
									transform: {},
									computed: {}
								}),
								(e.styles.inline = e.domEl.getAttribute("style") || ""),
								(e.styles.inline += "; visibility: visible; "),
								(e.styles.computed.opacity = t.opacity),
								t.transition && "all 0s ease 0s" !== t.transition
									? (e.styles.computed.transition = t.transition + ", ")
									: (e.styles.computed.transition = "")),
								(e.styles.transition.instant = c(e, 0)),
								(e.styles.transition.delayed = c(e, e.config.delay)),
								(e.styles.transform.initial = " -webkit-transform:"),
								(e.styles.transform.target = " -webkit-transform:"),
								f(e),
								(e.styles.transform.initial += "transform:"),
								(e.styles.transform.target += "transform:"),
								f(e);
						}

						function c(e, t) {
							var n = e.config;
							return (
								"-webkit-transition: " +
								e.styles.computed.transition +
								"-webkit-transform " +
								n.duration / 1e3 +
								"s " +
								n.easing +
								" " +
								t / 1e3 +
								"s, opacity " +
								n.duration / 1e3 +
								"s " +
								n.easing +
								" " +
								t / 1e3 +
								"s; transition: " +
								e.styles.computed.transition +
								"transform " +
								n.duration / 1e3 +
								"s " +
								n.easing +
								" " +
								t / 1e3 +
								"s, opacity " +
								n.duration / 1e3 +
								"s " +
								n.easing +
								" " +
								t / 1e3 +
								"s; "
							);
						}

						function f(e) {
							var t,
								n = e.config,
								i = e.styles.transform;
							(t = "top" === n.origin || "left" === n.origin ? (/^-/.test(n.distance) ? n.distance.substr(1) : "-" + n.distance) : n.distance),
								parseInt(n.distance) && ((i.initial += " translate" + n.axis + "(" + t + ")"), (i.target += " translate" + n.axis + "(0)")),
								n.scale && ((i.initial += " scale(" + n.scale + ")"), (i.target += " scale(1)")),
								n.rotate.x && ((i.initial += " rotateX(" + n.rotate.x + "deg)"), (i.target += " rotateX(0)")),
								n.rotate.y && ((i.initial += " rotateY(" + n.rotate.y + "deg)"), (i.target += " rotateY(0)")),
								n.rotate.z && ((i.initial += " rotateZ(" + n.rotate.z + "deg)"), (i.target += " rotateZ(0)")),
								(i.initial += "; opacity: " + n.opacity + ";"),
								(i.target += "; opacity: " + e.styles.computed.opacity + ";");
						}

						function d(e) {
							var t = e.config.container;
							t && -1 === n.store.containers.indexOf(t) && n.store.containers.push(e.config.container), (n.store.elements[e.id] = e);
						}

						function u() {
							if (n.isSupported()) {
								m();
								for (var e = 0; e < n.store.containers.length; e++)
									n.store.containers[e].addEventListener("scroll", p), n.store.containers[e].addEventListener("resize", p);
								n.initialized || (window.addEventListener("scroll", p), window.addEventListener("resize", p), (n.initialized = !0));
							}
							return n;
						}

						function p() {
							i(m);
						}

						function m() {
							var e, t, i, o, r;
							n.tools.forOwn(n.sequences, function(e) {
								(r = n.sequences[e]), (i = !1);
								for (var t = 0; t < r.elemIds.length; t++) (o = r.elemIds[t]), v(n.store.elements[o]) && !i && (i = !0);
								r.active = i;
							}),
								n.tools.forOwn(n.store.elements, function(i) {
									(t = n.store.elements[i]),
										(e = (function(e) {
											var t = e.config.useDelay;
											return "always" === t || ("onload" === t && !n.initialized) || ("once" === t && !e.seen);
										})(t)),
										(function(e) {
											if (e.sequence) {
												var t = n.sequences[e.sequence.id];
												return t.active && !t.blocked && !e.revealing && !e.disabled;
											}
											return v(e) && !e.revealing && !e.disabled;
										})(t)
											? (t.config.beforeReveal(t.domEl),
											  e
													? t.domEl.setAttribute("style", t.styles.inline + t.styles.transform.target + t.styles.transition.delayed)
													: t.domEl.setAttribute("style", t.styles.inline + t.styles.transform.target + t.styles.transition.instant),
											  y("reveal", t, e),
											  (t.revealing = !0),
											  (t.seen = !0),
											  t.sequence &&
													(function(e, t) {
														var i = 0,
															o = 0,
															r = n.sequences[e.sequence.id];
														(r.blocked = !0), t && "onload" === e.config.useDelay && (o = e.config.delay);
														e.sequence.timer &&
															((i = Math.abs(e.sequence.timer.started - new Date())), window.clearTimeout(e.sequence.timer));
														(e.sequence.timer = {
															started: new Date()
														}),
															(e.sequence.timer.clock = window.setTimeout(function() {
																(r.blocked = !1), (e.sequence.timer = null), p();
															}, Math.abs(r.interval) + o - i));
													})(t, e))
											: (function(e) {
													if (e.sequence) {
														var t = n.sequences[e.sequence.id];
														return !t.active && e.config.reset && e.revealing && !e.disabled;
													}
													return !v(e) && e.config.reset && e.revealing && !e.disabled;
											  })(t) &&
											  (t.config.beforeReset(t.domEl),
											  t.domEl.setAttribute("style", t.styles.inline + t.styles.transform.initial + t.styles.transition.instant),
											  y("reset", t),
											  (t.revealing = !1));
								});
						}

						function y(e, t, n) {
							var i = 0,
								o = 0,
								r = "after";
							switch (e) {
								case "reveal":
									(o = t.config.duration), n && (o += t.config.delay), (r += "Reveal");
									break;
								case "reset":
									(o = t.config.duration), (r += "Reset");
							}
							t.timer && ((i = Math.abs(t.timer.started - new Date())), window.clearTimeout(t.timer.clock)),
								(t.timer = {
									started: new Date()
								}),
								(t.timer.clock = window.setTimeout(function() {
									t.config[r](t.domEl), (t.timer = null);
								}, o - i));
						}

						function g(e) {
							var t = 0,
								n = 0,
								i = e.offsetHeight,
								o = e.offsetWidth;
							do {
								isNaN(e.offsetTop) || (t += e.offsetTop), isNaN(e.offsetLeft) || (n += e.offsetLeft), (e = e.offsetParent);
							} while (e);
							return {
								top: t,
								left: n,
								height: i,
								width: o
							};
						}

						function v(e) {
							var t,
								n,
								i,
								o,
								r,
								s,
								a,
								l,
								c = g(e.domEl),
								f = (function(e) {
									return {
										width: e.clientWidth,
										height: e.clientHeight
									};
								})(e.config.container),
								d = (function(e) {
									if (e && e !== window.document.documentElement) {
										var t = g(e);
										return {
											x: e.scrollLeft + t.left,
											y: e.scrollTop + t.top
										};
									}
									return {
										x: window.pageXOffset,
										y: window.pageYOffset
									};
								})(e.config.container),
								u = e.config.viewFactor,
								p = c.height,
								m = c.width,
								y = c.top,
								v = c.left;
							return (
								(t = y + p * u),
								(n = v + m * u),
								(i = y + p - p * u),
								(o = v + m - m * u),
								(r = d.y + e.config.viewOffset.top),
								(s = d.x + e.config.viewOffset.left),
								(a = d.y - e.config.viewOffset.bottom + f.height),
								(l = d.x - e.config.viewOffset.right + f.width),
								(t < a && i > r && n < l && o > s) || "fixed" === window.getComputedStyle(e.domEl).position
							);
						}

						function w() {}
						(o.prototype.defaults = {
							origin: "bottom",
							distance: "20px",
							duration: 500,
							delay: 0,
							rotate: {
								x: 0,
								y: 0,
								z: 0
							},
							opacity: 0,
							scale: 0.9,
							easing: "cubic-bezier(0.6, 0.2, 0.1, 1)",
							container: window.document.documentElement,
							mobile: !0,
							reset: !1,
							useDelay: "always",
							viewFactor: 0.2,
							viewOffset: {
								top: 0,
								right: 0,
								bottom: 0,
								left: 0
							},
							beforeReveal: function(e) {},
							beforeReset: function(e) {},
							afterReveal: function(e) {},
							afterReset: function(e) {}
						}),
							(o.prototype.isSupported = function() {
								var e = document.documentElement.style;
								return (("WebkitTransition" in e) && ("WebkitTransform" in e)) || (("transition" in e) && ("transform" in e));
							}),
							(o.prototype.reveal = function(e, t, i, o) {
								var c, f, p, m, y, g;
								if (
									(void 0 !== t && "number" == typeof t ? ((i = t), (t = {})) : (void 0 !== t && null !== t) || (t = {}),
									!(f = (function(e, t) {
										if ("string" == typeof e) return Array.prototype.slice.call(t.querySelectorAll(e));
										if (n.tools.isNode(e)) return [e];
										if (n.tools.isNodeList(e)) return Array.prototype.slice.call(e);
										if (Array.isArray(e)) return e.filter(n.tools.isNode);
										return [];
									})(e, (c = r(t)))).length)
								)
									return console.log('ScrollReveal: reveal on "' + e + '" failed, no elements found.'), n;
								i &&
									"number" == typeof i &&
									((g = s()),
									(y = n.sequences[g] = {
										id: g,
										interval: i,
										elemIds: [],
										active: !1
									}));
								for (var v = 0; v < f.length; v++)
									(m = f[v].getAttribute("data-sr-id"))
										? (p = n.store.elements[m])
										: (p = {
												id: s(),
												domEl: f[v],
												seen: !1,
												revealing: !1
										  }).domEl.setAttribute("data-sr-id", p.id),
										y &&
											((p.sequence = {
												id: y.id,
												index: y.elemIds.length
											}),
											y.elemIds.push(p.id)),
										a(p, t, c),
										l(p),
										d(p),
										(n.tools.isMobile() && !p.config.mobile) || !n.isSupported()
											? (p.domEl.setAttribute("style", p.styles.inline), (p.disabled = !0))
											: p.revealing || p.domEl.setAttribute("style", p.styles.inline + p.styles.transform.initial);
								return (
									!o &&
										n.isSupported() &&
										(!(function(e, t, i) {
											var o = {
												target: e,
												config: t,
												interval: i
											};
											n.history.push(o);
										})(e, t, i),
										n.initTimeout && window.clearTimeout(n.initTimeout),
										(n.initTimeout = window.setTimeout(u, 0))),
									n
								);
							}),
							(o.prototype.sync = function() {
								if (n.history.length && n.isSupported()) {
									for (var e = 0; e < n.history.length; e++) {
										var t = n.history[e];
										n.reveal(t.target, t.config, t.interval, !0);
									}
									u();
								} else console.log("ScrollReveal: sync failed, no reveals found.");
								return n;
							}),
							(w.prototype.isObject = function(e) {
								return null !== e && "object" === (void 0 === e ? "undefined" : t(e)) && e.constructor === Object;
							}),
							(w.prototype.isNode = function(e) {
								return "object" === t(window.Node)
									? e instanceof window.Node
									: e && "object" === (void 0 === e ? "undefined" : t(e)) && "number" == typeof e.nodeType && "string" == typeof e.nodeName;
							}),
							(w.prototype.isNodeList = function(e) {
								var n = Object.prototype.toString.call(e);
								return "object" === t(window.NodeList)
									? e instanceof window.NodeList
									: e &&
											"object" === (void 0 === e ? "undefined" : t(e)) &&
											/^\[object (HTMLCollection|NodeList|Object)\]$/.test(n) &&
											"number" == typeof e.length &&
											(0 === e.length || this.isNode(e[0]));
							}),
							(w.prototype.forOwn = function(e, n) {
								if (!this.isObject(e)) throw new TypeError('Expected "object", but received "' + (void 0 === e ? "undefined" : t(e)) + '".');
								for (var i in e) e.hasOwnProperty(i) && n(i);
							}),
							(w.prototype.extend = function(e, t) {
								return (
									this.forOwn(
										t,
										function(n) {
											this.isObject(t[n]) ? ((e[n] && this.isObject(e[n])) || (e[n] = {}), this.extend(e[n], t[n])) : (e[n] = t[n]);
										}.bind(this)
									),
									e
								);
							}),
							(w.prototype.extendClone = function(e, t) {
								return this.extend(this.extend({}, e), t);
							}),
							(w.prototype.isMobile = function() {
								return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
							}),
							(i =
								window.requestAnimationFrame ||
								window.webkitRequestAnimationFrame ||
								window.mozRequestAnimationFrame ||
								function(e) {
									window.setTimeout(e, 1e3 / 60);
								}),
							e.exports ? (e.exports = o) : (window.ScrollReveal = o);
					})();
				})(
					(e = {
						exports: {}
					}),
					e.exports
				),
				e.exports)(),
				i = document.querySelectorAll('[data-component="fadereveal"]'),
				o = 0;
			o < i.length;
			o++
		) {
			var r = i[o].querySelectorAll('[data-ref="fadereveal[el]"]');
			n.reveal(
				r,
				{
					duration: 1e3,
					scale: 1,
					distance: "0px",
					viewFactor: 0
				},
				100
			);
		}
		for (var s = document.querySelectorAll('[data-component="slidereveal"]'), a = 0; a < s.length; a++) {
			var l = s[a].querySelectorAll('[data-ref="slidereveal[el]"]');
			n.reveal(
				l,
				{
					duration: 1e3,
					scale: 1,
					distance: "50px"
				},
				75
			);
		}
		var c = '[data-ref="hero[el]"]';
		new Glide('[data-component="hero"]', {
			focusAt: "center",
			startAt: 0,
			perView: 6,
			peek: 50,
			gap: 30,
			autoplay: 2500,
			hoverpause: 1,
			animationDuration: 1e3,
			rewindDuration: 2e3,
			touchRatio: 0.25,
			perTouch: 1,
			breakpoints: {
				480: {
					gap: 15,
					peek: 75,
					perView: 1
				},
				768: {
					perView: 2
				},
				1360: {
					perView: 3
				},
				1600: {
					perView: 4
				},
				1960: {
					perView: 5
				}
			}
		}).mount({
			Coverflow: function(e, t, n) {
				var i = {
					tilt: function(e) {
						(e.querySelector(c).style.transform = "perspective(1500px) rotateY(0deg)"), this.tiltPrevElements(e), this.tiltNextElements(e);
					},
					tiltPrevElements: function(e) {
						for (
							var t = (function(e) {
									var t = [];
									if (e) for (; (e = e.previousElementSibling); ) t.push(e);
									return t;
								})(e),
								n = 0;
							n < t.length;
							n++
						) {
							var i = t[n].querySelector(c);
							(i.style.transformOrigin = "100% 50%"), (i.style.transform = "perspective(1500px) rotateY(" + 20 * Math.max(n, 2) + "deg)");
						}
					},
					tiltNextElements: function(e) {
						for (
							var t = (function(e) {
									var t = [];
									if (e) for (; (e = e.nextElementSibling); ) t.push(e);
									return t;
								})(e),
								n = 0;
							n < t.length;
							n++
						) {
							var i = t[n].querySelector(c);
							(i.style.transformOrigin = "0% 50%"), (i.style.transform = "perspective(1500px) rotateY(" + -20 * Math.max(n, 2) + "deg)");
						}
					}
				};
				return (
					n.on(["mount.after", "run"], function() {
						i.tilt(t.Html.slides[e.index]);
					}),
					i
				);
			}
		});
	})();
}
