/* Common JavaScript for responsive design */
(function (designWidth) {
    var resizeEvt = "orientationchange" in window ? "orientationchange" : "resize";
    var recalc = function () {
        var size = (window.innerWidth || document.documentElement.clientWidth) / designWidth * 100;
        document.documentElement.style.fontSize = size + "px";
        document.body.style.fontSize = "16px";
    };
    window.addEventListener(resizeEvt, recalc, false);
    document.addEventListener("DOMContentLoaded", recalc, false);
})(750);