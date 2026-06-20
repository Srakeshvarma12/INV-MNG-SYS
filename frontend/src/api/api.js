import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8085/inventory/api',
  withCredentials: true,       // required for session cookies
  headers: { 'Content-Type': 'application/json' }
});

// Add response interceptor: redirect to /login on 401
API.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      // Avoid infinite redirects if we are already on /login
      if (!window.location.pathname.endsWith('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(err);
  }
);

export default API;
