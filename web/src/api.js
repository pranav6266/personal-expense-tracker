import axios from 'axios'

// Default to relative API path so same-origin (nginx) can proxy requests in Docker/ngrok setups
const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Attach token to requests
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

export default api
// Response interceptor to handle auth expiration globally
api.interceptors.response.use(
  response => response,
  error => {
    const status = error.response?.status
    const isAuthRequest = ['/login', '/signup'].includes(error.config?.url)
    if ((status === 401 || status === 403) && !isAuthRequest) {
      try {
        localStorage.removeItem('token')
      } catch {
        // Storage can be unavailable (e.g. private mode); the redirect still logs the user out
      }
      // Redirect to login
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
