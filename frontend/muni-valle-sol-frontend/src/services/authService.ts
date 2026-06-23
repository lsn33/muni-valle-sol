const BASE_URL = process.env.NEXT_PUBLIC_BFF_URL

export const login = async (email: string, password: string) => {
  const response = await fetch(`${BASE_URL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
    credentials: 'include',
  })
  if (!response.ok) throw new Error('Credenciales incorrectas')
  return response.json()
}

export const register = async (nombre: string, email: string, password: string) => {
  const response = await fetch(`${BASE_URL}/api/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nombre, email, password, rol: 'CIUDADANO' }),
    credentials: 'include',
  })
  if (!response.ok) throw new Error('Error al registrar usuario')
  return response.json()
}

export const logout = async () => {
  await fetch(`${BASE_URL}/api/auth/logout`, {
    method: 'POST',
    credentials: 'include',
  })
}

export const getMe = async () => {
  const response = await fetch(`${BASE_URL}/api/auth/me`, {
    credentials: 'include',
  })
  if (!response.ok) return null
  return response.json()
}
