async function solicitar(url, opciones = {}) {
  let response

  try {
    response = await fetch(url, opciones)
  } catch {
    throw new Error('No se pudo conectar con el servidor. Verificá que Spring Boot esté iniciado.')
  }

  if (response.status === 204) {
    return null
  }

  const contenido = await response.text()
  let datos = null

  if (contenido) {
    try {
      datos = JSON.parse(contenido)
    } catch {
      datos = contenido
    }
  }

  if (!response.ok) {
    let mensaje = `Error HTTP ${response.status}`

    if (datos && typeof datos === 'object') {
      if (datos.errores && typeof datos.errores === 'object') {
        const detalles = Object.entries(datos.errores)
          .map(([campo, error]) => `${campo}: ${error}`)
          .join(', ')
        mensaje = detalles || datos.mensaje || datos.error || mensaje
      } else {
        mensaje = datos.mensaje || datos.error || mensaje
      }
    } else if (typeof datos === 'string' && datos.trim()) {
      mensaje = datos
    }

    throw new Error(mensaje)
  }

  return datos
}

export async function listarEquipos() {
  const datos = await solicitar('/api/equipos')
  return Array.isArray(datos) ? datos : []
}

export async function registrarEquipo(equipo) {
  return solicitar('/api/equipos', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(equipo)
  })
}

export async function actualizarEquipo(id, equipo) {
  return solicitar(`/api/equipos/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(equipo)
  })
}

export async function eliminarEquipo(id) {
  return solicitar(`/api/equipos/${id}`, {
    method: 'DELETE'
  })
}
