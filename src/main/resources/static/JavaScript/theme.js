(function () {
  var KEY = "shop-theme";

  function setTheme(mode) {
    var next = mode === "light" ? "light" : "dark";
    document.documentElement.setAttribute("data-theme", next);
    try {
      localStorage.setItem(KEY, next);
    } catch (e) {}
  }

  function toggle() {
    var cur = document.documentElement.getAttribute("data-theme");
    setTheme(cur === "light" ? "dark" : "light");
  }

  document.addEventListener("click", function (e) {
    var btn = e.target.closest("[data-theme-toggle]");
    if (btn) {
      e.preventDefault();
      toggle();
    }
  });
})();
