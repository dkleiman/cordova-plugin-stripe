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
}

plugin.prototype.open = function(color, title, success, failure) {
	color = color || this.color;
	title = title || 'Payments';
	var key = this.key
	this.setColor(color);
	if (success) {
		failure = failure || function(err) {};
		exec(success, failure, "StripePlugin", "payments_activity", [color, colorMultiply(color, 0.8), title, key]);
		return;
	}
	return new Promise(function(resolve, reject) {
		exec(resolve, reject, "StripePlugin", "payments_activity", [color, colorMultiply(color, 0.8), title, key]);
	});
}

plugin.prototype.setColor = function(color) {
	this.color = color;
}

module.exports = new plugin();