// forms.js - contact, volunteer and donation-interest forms.
// Loaded by auth.js on pages that contain a form[data-demo-form].
import { apiFetch, currentUser, showMessage } from "./auth.js";

const page = document.body.dataset.page || "";

// Turn an API error response into one readable sentence.
async function describeFailure(response) {
  if (response.status === 401) return "Please sign in again, then resubmit.";
  if (response.status === 409) return "You already have an active application for this programme.";
  if (response.status === 413) return "That submission is too large.";
  if (response.status === 422) {
    const data = await response.json().catch(() => ({}));
    const fields = data.fields ? Object.values(data.fields).join(" ") : "";
    return fields || data.error || "Please check the form and try again.";
  }
  return "The server had a problem. Please try again later.";
}

async function send(form, path, payload, successText) {
  try {
    const response = await apiFetch(path, { method: "POST", body: JSON.stringify(payload) });
    if (response.ok) {
      showMessage(form, successText, "success");
      form.reset();
    } else {
      showMessage(form, await describeFailure(response));
    }
  } catch (error) {
    showMessage(form, error.message === "NO_API"
      ? "The server connection has not been set up yet, so nothing was sent."
      : "Could not reach the server. Check your internet connection and try again.");
  }
  form.querySelector(".form-message").scrollIntoView({ behavior: "smooth", block: "nearest" });
}

document.querySelectorAll("form[data-demo-form]").forEach((form) => {
  form.addEventListener("input", () => showMessage(form, ""));
  form.addEventListener("change", () => showMessage(form, ""));

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    showMessage(form, "");
    if (!form.checkValidity()) { form.reportValidity(); return; }

    const button = form.querySelector('button[type="submit"]');
    const value = (name) => form.elements[name].value.trim();
    button.disabled = true;
    try {
      if (page === "contact") {
        await send(form, "/contact",
          { name: value("name"), email: value("email"), subject: value("subject"), message: value("message") },
          "Thank you. Your enquiry has been sent.");
      } else if (page === "donate") {
        const support = form.elements.support.value;
        await send(form, "/contact", {
          name: value("name"), email: value("email"),
          subject: "Donation interest: " + support,
          message: "Support type: " + support + ". " + (value("message") || "No extra message.")
        }, "Thank you. Your interest has been recorded. No payment was taken.");
      } else if (page === "volunteer") {
        const user = await currentUser();
        if (!user) {
          showMessage(form, "Please sign in or create an account first. Redirecting you to the sign-in page...");
          setTimeout(() => { location.href = "login.html?next=volunteer.html"; }, 1800);
          return;
        }
        const picked = (name) => [...form.querySelectorAll(`input[name="${name}"]:checked`)].map((box) => box.value);
        await send(form, "/volunteer-applications", {
          full_name: value("full_name"), email: value("email"), phone: value("phone"),
          qualification: value("qualification"), field_of_study: value("field_of_study"),
          programme_preference: value("programme_preference"),
          subjects: picked("subjects"), availability: picked("availability"),
          motivation: value("motivation"), consent_background_check: form.elements.consent_background_check.checked
        }, "Thank you. Your volunteer application has been saved. You can track it on your account page.");
      }
    } finally {
      button.disabled = false;
    }
  });
});

// Pre-fill the volunteer form from the signed-in account.
if (page === "volunteer") {
  currentUser().then((user) => {
    const form = document.querySelector("form[data-demo-form]");
    if (!user || !form) return;
    if (!form.elements.full_name.value) form.elements.full_name.value = user.displayName || "";
    if (!form.elements.email.value) form.elements.email.value = user.email || "";
  });
}