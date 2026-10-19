// Firebase WEB configuration. These values are public by design (they identify the
// project, they are not secrets). Do NOT put a service-account key or passwords here.
export const firebaseConfig = {
  apiKey: "AIzaSyAOkvP35qetifTuFVCgG4O9pH6vGXkPr4c",
  authDomain: "mamelodi-initiative-ngo.firebaseapp.com",
  projectId: "mamelodi-initiative-ngo",
  storageBucket: "mamelodi-initiative-ngo.firebasestorage.app",
  messagingSenderId: "264918646014",
  appId: "1:264918646014:web:089ba97d7a501b1683f9af"
};

// Address of the PHP API that Thula deploys on Azure, WITHOUT a trailing slash.
// Example: "https://mamelodi-api.azurewebsites.net/api"
// Leave as "" until the API exists; the forms will then say the server is not connected yet.
export const API_BASE = "";