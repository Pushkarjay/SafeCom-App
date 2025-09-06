document.addEventListener("DOMContentLoaded", () => {
  const themeToggle = document.getElementById("theme-toggle")
  const body = document.body
  const icon = themeToggle.querySelector("i")
  const text = themeToggle.querySelector("span")

  // Check for saved theme preference
  const savedTheme = localStorage.getItem("safecom-theme")
  if (savedTheme === "dark") {
    body.classList.remove("light-theme")
    body.classList.add("dark-theme")
    icon.classList.remove("fa-moon")
    icon.classList.add("fa-sun")
    text.textContent = "Light Mode"
  }

  // Toggle theme
  themeToggle.addEventListener("click", () => {
    if (body.classList.contains("light-theme")) {
      body.classList.remove("light-theme")
      body.classList.add("dark-theme")
      icon.classList.remove("fa-moon")
      icon.classList.add("fa-sun")
      text.textContent = "Light Mode"
      localStorage.setItem("safecom-theme", "dark")
    } else {
      body.classList.remove("dark-theme")
      body.classList.add("light-theme")
      icon.classList.remove("fa-sun")
      icon.classList.add("fa-moon")
      text.textContent = "Dark Mode"
      localStorage.setItem("safecom-theme", "light")
    }
  })
})

