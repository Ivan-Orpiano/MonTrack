// In production, the frontend is typically served behind the same reverse proxy /
// origin as the API (or the API's public URL is baked in at build time). Adjust
// apiUrl to wherever the Spring Boot backend is actually deployed.
export const environment = {
  production: true,
  apiUrl: '/api'
};