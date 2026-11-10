async function request(url, options = {}) {
  let response

  try {
    response = await fetch(url, options)
  } catch {
    throw new Error('No se pudo conectar con el backend. Comprobá que Spring Boot esté iniciado.')
  }

  if (response.status === 204) return null

  const text = await response.text()
  let data = null

  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }
  }

  if (!response.ok) {
    if (data && typeof data === 'object') {
      const validationErrors = data.errores && typeof data.errores === 'object'
        ? Object.entries(data.errores).map(([field, message]) => `${field}: ${message}`).join(', ')
        : ''
      throw new Error(validationErrors || data.mensaje || data.message || data.error || `Error HTTP ${response.status}`)
    }
    throw new Error(typeof data === 'string' && data.trim() ? data : `Error HTTP ${response.status}`)
  }

  return data
}

export function createResourceService(endpoint) {
  const base = endpoint.replace(/\/$/, '')
  return {
    list: () => request(base),
    get: (id) => request(`${base}/${id}`),
    create: (payload) => request(base, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }),
    update: (id, payload) => request(`${base}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }),
    remove: (id) => request(`${base}/${id}`, { method: 'DELETE' }),
  }
}

export async function getDashboardResumen() {
  return request('/api/dashboard/resumen')
}
