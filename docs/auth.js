// ─────────────────────────────────────────────────────────
// auth.js — funciones que comparten TODAS las páginas
// ─────────────────────────────────────────────────────────

const API = "https://botpanel-production.up.railway.app";

// Comprueba si hay sesión activa. Si no, manda al login.
function verificarSesion() {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "index.html";
    return false;
  }
  return true;
}

// Cierra la sesión y vuelve al login
function cerrarSesion() {
  localStorage.removeItem('token');
  localStorage.removeItem('nombre');
  localStorage.removeItem('email');
  localStorage.removeItem('rol');
  localStorage.removeItem('empresaId');
  window.location.href = "index.html";
}

// Hace peticiones a la API con el token automáticamente
async function apiFetch(url, opciones = {}) {
  const token = localStorage.getItem("token");

  const config = {
    ...opciones,
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`,   // Añade el token en cada petición
      ...opciones.headers
    }
  };

  const respuesta = await fetch(`${API}${url}`, config);

  // Si el token expiró (401), mandamos al login
  if (respuesta.status === 401) {
    cerrarSesion();
    return null;
  }

  return respuesta;
}

// Devuelve los datos del usuario actual desde localStorage
function getUsuario() {
  return {
    nombre:    localStorage.getItem("nombre"),
    email:     localStorage.getItem("email"),
    rol:       localStorage.getItem("rol"),
    empresaId: localStorage.getItem("empresaId")
  };
}

// Comprueba si el usuario actual es Super Admin
function esSuperAdmin() {
  return localStorage.getItem("rol") === "SUPER_ADMIN";
}
