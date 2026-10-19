// auth.js - Firebase Authentication for the whole website.
// Loaded automatically by components.js. If Firebase fails to load, the rest of the
// site keeps working (the header and footer are drawn before this file runs).
import { initializeApp } from "https://www.gstatic.com/firebasejs/11.0.2/firebase-app.js";
import {
  getAuth, onAuthStateChanged, createUserWithEmailAndPassword, signInWithEmailAndPassword,
  signInWithPopup, GoogleAuthProvider, sendPasswordResetEmail, updateProfile, signOut
} from "https://www.gstatic.com/firebasejs/11.0.2/firebase-auth.js";
import { firebaseConfig, API_BASE } from "./firebase-config.js";

export const auth = getAuth(initializeApp(firebaseConfig));
const page = document.body.dataset.page || "";
const root = document.body.dataset.root || "";

/* ---------- small helpers ---------- */
export function showMessage(form, text, type = "error") {
  const box = form.querySelector(".form-message");
  if (!box) return;
  box.className = text ? `form-message show ${type}` : "form-message";
  box.textContent = text;
}

function friendlyError(error) {
  switch (error && error.code) {
    case "auth/email-already-in-use": return "An account with this email already exists. Try signing in instead.";
    case "auth/invalid-email": return "That email address does not look right.";
    case "auth/weak-password": return "Choose a stronger password (at least 8 characters).";
    case "auth/invalid-credential":
    case "auth/wrong-password":
    case "auth/user-not-found": return "Incorrect email or password.";
    case "auth/too-many-requests": return "Too many attempts. Please wait a few minutes and try again.";
    case "auth/network-request-failed": return "Network problem. Check your internet connection and try again.";
    case "auth/operation-not-allowed": return "This sign-in method is not switched on in the Firebase console yet.";
    case "auth/unauthorized-domain": return "This website address is not in Firebase's authorised domains list.";
    case "auth/popup-closed-by-user":
    case "auth/cancelled-popup-request": return "";
    default: return "Something went wrong. Please try again.";
  }
}

// Only allow a redirect to another page of this site, e.g. ?next=volunteer.html
function nextPage() {
  const next = new URLSearchParams(location.search).get("next") || "";
  return /^[a-z0-9-]+\.html$/i.test(next) ? next : "my-account.html";
}

export async function apiFetch(path, options = {}) {
  if (!API_BASE) throw new Error("NO_API");
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  if (auth.currentUser) headers.Authorization = "Bearer " + (await auth.currentUser.getIdToken());
  return fetch(API_BASE + path, { ...options, headers });
}

export async function currentUser() {
  await auth.authStateReady();
  return auth.currentUser;
}

async function busy(form, work) {
  const button = form.querySelector('button[type="submit"]');
  if (button) button.disabled = true;
  try { await work(); } finally { if (button) button.disabled = false; }
}

/* ---------- header: "Sign in" becomes "My account" ---------- */
onAuthStateChanged(auth, (user) => {
  document.querySelectorAll(".main-navigation a").forEach((link) => {
    const href = link.getAttribute("href") || "";
    if (href.endsWith("login.html") || href.endsWith("my-account.html")) {
      link.textContent = user ? "My account" : "Sign in";
      link.setAttribute("href", root + (user ? "pages/my-account.html" : "pages/login.html"));
    }
  });
});

/* ---------- Google button (any page with a [data-google] button) ---------- */
document.querySelectorAll("[data-google]").forEach((button) => {
  button.addEventListener("click", async () => {
    const form = button.closest("form");
    try {
      await signInWithPopup(auth, new GoogleAuthProvider());
      location.href = nextPage();
    } catch (error) {
      if (form) showMessage(form, friendlyError(error));
    }
  });
});

/* ---------- register / login / forgot password ---------- */
document.querySelectorAll("[data-auth-form]").forEach((form) => {
  form.addEventListener("input", () => showMessage(form, ""));

  form.addEventListener("submit", (event) => {
    event.preventDefault();
    showMessage(form, "");
    const email = form.elements.email.value.trim();

    busy(form, async () => {
      try {
        if (page === "register") {
          const name = form.elements.name.value.trim();
          const password = form.elements.password.value;
          if (password !== form.elements.confirmPassword.value) return showMessage(form, "The passwords do not match.");
          if (!/^(?=.*[A-Za-z])(?=.*\d).{8,}$/.test(password)) {
            return showMessage(form, "Use at least 8 characters with a letter and a number.");
          }
          const result = await createUserWithEmailAndPassword(auth, email, password);
          await updateProfile(result.user, { displayName: name });
          location.href = nextPage();
        } else if (page === "login") {
          await signInWithEmailAndPassword(auth, email, form.elements.password.value);
          location.href = nextPage();
        } else if (page === "forgot-password") {
          try { await sendPasswordResetEmail(auth, email); }
          catch (error) { if (error.code !== "auth/user-not-found") throw error; }
          // Same message whether or not the email exists, so accounts cannot be guessed.
          showMessage(form, "If an account exists for that email, a reset link has been sent. Check your inbox and spam folder.", "success");
        }
      } catch (error) {
        showMessage(form, friendlyError(error));
      }
    });
  });
});

/* ---------- my account page ---------- */
const PROGRAMME_NAMES = { summer_jam: "Summer Jam", winter_jam: "Winter Jam", road_to_finals: "Road to Finals", ylc: "Youth Leadership Council" };

function el(tag, text, className) {
  const node = document.createElement(tag);
  if (text !== undefined) node.textContent = text;
  if (className) node.className = className;
  return node;
}

async function loadApplications(list) {
  list.replaceChildren(el("p", "Loading your applications...", "muted"));
  try {
    const response = await apiFetch("/volunteer-applications");
    if (!response.ok) throw new Error("HTTP " + response.status);
    const { items } = await response.json();
    list.replaceChildren();
    if (!items.length) {
      list.append(el("p", "You have not applied to volunteer yet.", "muted"));
      return;
    }
    items.forEach((app) => {
      const card = el("div", undefined, "card");
      card.style.marginTop = "0.8rem";
      card.append(
        el("h3", PROGRAMME_NAMES[app.programme_preference] || app.programme_preference),
        el("p", "Status: " + String(app.status).replace("_", " "), "muted"),
        el("p", "Submitted: " + new Date(app.created_at.replace(" ", "T")).toLocaleDateString())
      );
      if (app.status !== "withdrawn") {
        const withdraw = el("button", "Withdraw application", "btn btn-secondary");
        withdraw.type = "button";
        withdraw.addEventListener("click", async () => {
          if (!confirm("Withdraw this application?")) return;
          withdraw.disabled = true;
          try {
            const res = await apiFetch("/volunteer-applications/" + app.id, { method: "DELETE" });
            if (!res.ok) throw new Error();
            loadApplications(list);
          } catch { withdraw.disabled = false; alert("Could not withdraw. Please try again."); }
        });
        card.append(withdraw);
      }
      list.append(card);
    });
  } catch (error) {
    list.replaceChildren(el("p", error.message === "NO_API"
      ? "The server connection has not been set up yet, so applications cannot be shown."
      : "Could not load your applications right now.", "muted"));
  }
}

if (page === "my-account") {
  const signedOut = document.getElementById("account-signed-out");
  const details = document.getElementById("account-details");
  currentUser().then((user) => {
    if (!user) { location.replace("login.html?next=my-account.html"); return; }
    signedOut.hidden = true;
    details.hidden = false;
    document.getElementById("account-name").textContent = user.displayName || "(no name set)";
    document.getElementById("account-email").textContent = user.email || "";
    document.getElementById("account-method").textContent =
      user.providerData.some((p) => p.providerId === "google.com") ? "Google" : "Email and password";
    document.getElementById("account-reset").addEventListener("click", async (event) => {
      const note = document.getElementById("account-note");
      event.target.disabled = true;
      try {
        await sendPasswordResetEmail(auth, user.email);
        note.className = "form-message show success";
        note.textContent = "A password reset email has been sent to " + user.email + ".";
      } catch (error) {
        note.className = "form-message show error";
        note.textContent = friendlyError(error);
      }
      event.target.disabled = false;
    });
    document.getElementById("account-signout").addEventListener("click", async () => {
      await signOut(auth);
      location.href = "login.html";
    });
    loadApplications(document.getElementById("applications"));
  });
}

/* ---------- contact / volunteer / donate forms ---------- */
if (document.querySelector("form[data-demo-form]")) import("./forms.js");