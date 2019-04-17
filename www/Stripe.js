var exec = require('cordova/exec');

function plugin() {
	this.color = '#000000';
}

function colorMultiply(hex, alpha) {
	let r = hex.substring(1, 3);
	let g = hex.substring(3, 5);
	let b = hex.substring(5, 7);

	const convertAndMultiply = (hex) => {
		let i = Math.round(parseInt(hex, 16)*alpha).toString(16);
		if (i.length < 2) {
			return '0' + i;
		}
		return i;
	};
	return `#${convertAndMultiply(r)}${convertAndMultiply(g)}${convertAndMultiply(b)}`;
}

plugin.prototype.setPublishableKey = function(key) {
	this.key = key;
	return this;
}

plugin.prototype.open = function(color = this.color, statusBarColor, toolBarColor, title = this.title, success, failure) {

	if (typeof color === 'function') {
		success = color;
		color = this.color;
		if (typeof title === 'function') {
			failure = title;
			title = this.title;
		}
	} else if (typeof title === 'function') {
		failure = success;
		success = title;
		title = this.title;
	}

	var key = this.key
	this.setColor(color);
	if (success) {
		failure = failure || function(err) {};
		exec(success, failure, "StripePlugin", "payments_activity", [color, statusBarColor, toolBarColor, title, key]);
		return;
	}
	return new Promise(function(resolve, reject) {
		exec(resolve, reject, "StripePlugin", "payments_activity", [color, statusBarColor, toolBarColor, title, key]);
	});
}

plugin.prototype.setColor = function(color) {
	this.color = color;
	return this;
}

plugin.prototype.setTitle = function(title) {
	this.title = title;
	return this;
}

module.exports = new plugin();
