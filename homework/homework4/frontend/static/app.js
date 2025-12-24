let ws = null;

function $(id) {
  return document.getElementById(id);
}

function log(line) {
  const el = $("log");
  const ts = new Date().toISOString();
  el.textContent += `[${ts}] ${line}\n`;
  el.scrollTop = el.scrollHeight;
}

function toast(title, body) {
  const container = $("toastContainer");
  const t = document.createElement("div");
  t.className = "toast";
  t.innerHTML = `<div class="title"></div><div class="body"></div>`;
  t.querySelector(".title").textContent = title;
  t.querySelector(".body").textContent = body;
  container.appendChild(t);
  setTimeout(() => t.remove(), 4500);
}

async function api(path, options = {}) {
  const userId = $("userId").value.trim();
  const headers = new Headers(options.headers || {});
  headers.set("X-User-Id", userId);
  if (options.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  const resp = await fetch(path, { ...options, headers });
  if (!resp.ok) {
    const txt = await resp.text();
    throw new Error(`${resp.status} ${resp.statusText}: ${txt}`);
  }
  const ct = resp.headers.get("content-type") || "";
  if (ct.includes("application/json")) {
    return resp.json();
  }
  return resp.text();
}

async function createAccount() {
  try {
    await api("/api/accounts", { method: "POST" });
    toast("Payments", "Счёт создан");
    log("Account created");
  } catch (e) {
    toast("Payments", e.message);
    log(`ERROR createAccount: ${e.message}`);
  }
}

async function topup() {
  try {
    const amount = Number($("topupAmount").value);
    await api("/api/accounts/topup", {
      method: "POST",
      body: JSON.stringify({ amount })
    });
    toast("Payments", `Пополнение: +${amount}`);
    log(`Topup +${amount}`);
  } catch (e) {
    toast("Payments", e.message);
    log(`ERROR topup: ${e.message}`);
  }
}

async function balance() {
  try {
    const res = await api("/api/accounts/balance", { method: "GET" });
    $("balanceResult").textContent = `Баланс: ${res.balance}`;
    toast("Payments", `Баланс: ${res.balance}`);
    log(`Balance = ${res.balance}`);
  } catch (e) {
    toast("Payments", e.message);
    log(`ERROR balance: ${e.message}`);
  }
}

function wsUrlFor(orderId) {
  const userId = $("userId").value.trim();
  const proto = location.protocol === "https:" ? "wss" : "ws";
  return `${proto}://${location.host}/ws/orders?userId=${encodeURIComponent(userId)}&orderId=${encodeURIComponent(orderId)}`;
}

function closeWs() {
  if (ws) {
    try { ws.close(); } catch (_) {}
    ws = null;
  }
  $("wsState").textContent = "disconnected";
}

function connectWs(orderId) {
  closeWs();

  const url = wsUrlFor(orderId);
  log(`WS connect: ${url}`);
  ws = new WebSocket(url);

  ws.onopen = () => {
    $("wsState").textContent = "connected";
    toast("WebSocket", "Подключено");
  };

  ws.onmessage = (evt) => {
    try {
      const msg = JSON.parse(evt.data);
      if (msg.type === "ORDER_STATUS") {
        $("orderStatus").textContent = msg.status;
        toast("Статус заказа", `${msg.orderId}: ${msg.status}`);
        log(`ORDER_STATUS: ${msg.orderId} -> ${msg.status}`);

        // Browser push (Notification API)
        if ("Notification" in window && Notification.permission === "granted") {
          new Notification("GoZon: статус заказа", {
            body: `${msg.orderId}: ${msg.status}`
          });
        }
      } else {
        log(`WS message: ${evt.data}`);
      }
    } catch (e) {
      log(`WS parse error: ${e.message}, raw=${evt.data}`);
    }
  };

  ws.onerror = () => {
    $("wsState").textContent = "error";
    toast("WebSocket", "Ошибка соединения");
  };

  ws.onclose = () => {
    $("wsState").textContent = "closed";
    toast("WebSocket", "Отключено");
  };
}

async function createOrder() {
  try {
    const amount = Number($("orderAmount").value);
    const description = $("orderDescription").value;
    const order = await api("/api/orders", {
      method: "POST",
      body: JSON.stringify({ amount, description })
    });

    $("orderId").textContent = order.id;
    $("orderStatus").textContent = order.status;

    toast("Orders", `Заказ создан: ${order.id} (status=${order.status})`);
    log(`Order created: ${order.id} status=${order.status}`);

    connectWs(order.id);
  } catch (e) {
    toast("Orders", e.message);
    log(`ERROR createOrder: ${e.message}`);
  }
}

async function requestNotifications() {
  if (!("Notification" in window)) {
    toast("Push", "Notification API не поддерживается в этом браузере");
    return;
  }

  try {
    const permission = await Notification.requestPermission();
    toast("Push", `Permission: ${permission}`);
    log(`Notification permission = ${permission}`);
  } catch (e) {
    toast("Push", e.message);
  }
}

window.addEventListener("load", () => {
  $("btnCreateAccount").addEventListener("click", createAccount);
  $("btnTopup").addEventListener("click", topup);
  $("btnBalance").addEventListener("click", balance);
  $("btnCreateOrder").addEventListener("click", createOrder);
  $("btnNotify").addEventListener("click", requestNotifications);
});
