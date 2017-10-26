var exec = require("cordova/exec");

var DocRecognize = function () {};

DocRecognize.prototype.recognise = function (onSuccess, onFail) {
  exec(onSuccess, onFail, "DocRecognize", "recognise");
};

module.exports = new DocRecognize();