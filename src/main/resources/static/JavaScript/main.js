// ── Toast notification (global) ───────────────────────────────────────────────
window.showToast = function showToast(message, type) {
  type = type || "success";
  let container = document.getElementById("toast-container");
  if (!container) {
    container = document.createElement("div");
    container.id = "toast-container";
    document.body.appendChild(container);
  }

  const toast = document.createElement("div");
  toast.className = "toast toast-" + type;
  toast.innerHTML =
    '<span class="toast-icon">' +
    (type === "success" ? "✓" : "✕") +
    "</span>" +
    '<span class="toast-message">' +
    message +
    "</span>";
  container.appendChild(toast);

  requestAnimationFrame(function () {
    toast.classList.add("toast-show");
  });

  setTimeout(function () {
    toast.classList.remove("toast-show");
    toast.classList.add("toast-hide");
    setTimeout(function () {
      toast.remove();
    }, 400);
  }, 3000);
};

document.addEventListener("DOMContentLoaded", function () {
  // ── Cart count helper ─────────────────────────────────────────────────────
  function setCartCount(count) {
    var n = Number(count);
    if (!Number.isFinite(n)) return;
    var cartBadge = document.querySelector(".cart-count");
    if (!cartBadge) return;
    cartBadge.textContent = String(n);
    // Dùng toggle để chắc chắn class được quản lý đúng: ẩn khi = 0, hiện khi > 0
    cartBadge.classList.toggle("is-empty", n === 0);
  }

  window.updateCartCount = function updateCartCount() {
    return fetch("/cart/count", { headers: { Accept: "application/json" } })
      .then(function (r) {
        return r.json();
      })
      .then(function (data) {
        if (data && data.count != null) setCartCount(data.count);
      })
      .catch(function () {});
  };

  window.updateCartCount();

  // ── Search form ───────────────────────────────────────────────────────────
  var searchForm = document.getElementById("search-form");
  if (searchForm) {
    searchForm.addEventListener("submit", function (e) {
      e.preventDefault();
      var q = document.getElementById("q").value.trim();
      if (!q) return;
      window.location.href = "/search?q=" + encodeURIComponent(q);
    });
  }

  // ── Add-to-cart buttons ───────────────────────────────────────────────────
  document.querySelectorAll(".add-to-cart-btn").forEach(function (btn) {
    btn.addEventListener("click", function (e) {
      e.preventDefault();
      var productId = btn.dataset.id;
      var originalText = btn.innerHTML;
      btn.disabled = true;
      btn.innerHTML = "Đang thêm...";

      fetch("/cart/add-ajax", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "productId=" + productId + "&quantity=1",
      })
        .then(function (r) {
          return r.json();
        })
        .then(function (data) {
          if (data.success) {
            btn.innerHTML = "✓ Đã thêm";
            btn.classList.add("btn-success");
            btn.classList.remove("btn-primary");
            if (data.count != null) setCartCount(data.count);
            else window.updateCartCount();
            window.showToast(
              data.message || "Đã thêm vào giỏ hàng!",
              "success",
            );
            setTimeout(function () {
              btn.innerHTML = originalText;
              btn.disabled = false;
              btn.classList.remove("btn-success");
              btn.classList.add("btn-primary");
            }, 2000);
          } else {
            window.showToast(
              data.message || "Có lỗi xảy ra khi thêm vào giỏ hàng.",
              "error",
            );
            btn.innerHTML = originalText;
            btn.disabled = false;
          }
        })
        .catch(function () {
          window.showToast(
            "Có lỗi xảy ra. Vui lòng đăng nhập và thử lại.",
            "error",
          );
          btn.innerHTML = originalText;
          btn.disabled = false;
        });
    });
  });
});
