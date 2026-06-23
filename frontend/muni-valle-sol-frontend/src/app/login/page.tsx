'use client'

import { useState, type FormEvent } from 'react'
import { useRouter } from 'next/navigation'
import { z } from 'zod'
import useAuthStore from '@/store/useAuthStore'
import { login as loginService, register as registerService } from '@/services/authService'

type Tab = 'login' | 'register'

const DOMINIOS_VALIDOS = [
  'gmail.com', 'outlook.com', 'hotmail.com', 'yahoo.com',
  'icloud.com', 'live.com', 'msn.com', 'duocuc.cl',
  'uc.cl', 'usach.cl', 'uach.cl', 'puc.cl', 'udp.cl',
  'udd.cl', 'unab.cl', 'uai.cl', 'uft.cl', 'inacap.cl'
]

const emailSchema = z
  .string()
  .email('Ingresa un correo electronico valido')
  .refine((email) => {
    const dominio = email.split('@')[1]
    return DOMINIOS_VALIDOS.includes(dominio)
  }, 'El dominio del correo no esta permitido')

const passwordSchema = z
  .string()
  .min(8, 'La contrasena debe tener al menos 8 caracteres')
  .regex(/[A-Z]/, 'Debe contener al menos una letra mayuscula')
  .regex(/[!@#$%^&*(),.?":{}|<>]/, 'Debe contener al menos un caracter especial')

const loginSchema = z.object({
  email: emailSchema,
  password: z.string().min(1, 'La contrasena es requerida'),
})

const registerSchema = z.object({
  nombre: z.string().min(2, 'El nombre debe tener al menos 2 caracteres'),
  email: emailSchema,
  password: passwordSchema,
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'Las contrasenas no coinciden',
  path: ['confirmPassword'],
})

type LoginForm = z.infer<typeof loginSchema>
type RegisterForm = z.infer<typeof registerSchema>
type FormErrors = Partial<Record<string, string>>

const LoginPage = () => {
  const router = useRouter()
  const { login } = useAuthStore()

  const [tab, setTab] = useState<Tab>('login')
  const [loading, setLoading] = useState(false)
  const [serverError, setServerError] = useState('')
  const [errors, setErrors] = useState<FormErrors>({})

  const [loginForm, setLoginForm] = useState<LoginForm>({
    email: '',
    password: '',
  })

  const [registerForm, setRegisterForm] = useState<RegisterForm>({
    nombre: '',
    email: '',
    password: '',
    confirmPassword: '',
  })

  const isLogin = tab === 'login'

  const handleTabChange = (newTab: Tab) => {
    setTab(newTab)
    setErrors({})
    setServerError('')
  }

  const validateLogin = (): boolean => {
    const result = loginSchema.safeParse(loginForm)
    if (!result.success) {
      const fieldErrors: FormErrors = {}
      result.error.issues.forEach((err) => {
        if (err.path[0]) fieldErrors[err.path[0] as string] = err.message
      })
      setErrors(fieldErrors)
      return false
    }
    setErrors({})
    return true
  }

  const validateRegister = (): boolean => {
    const result = registerSchema.safeParse(registerForm)
    if (!result.success) {
      const fieldErrors: FormErrors = {}
      result.error.issues.forEach((err) => {
        if (err.path[0]) fieldErrors[err.path[0] as string] = err.message
      })
      setErrors(fieldErrors)
      return false
    }
    setErrors({})
    return true
  }

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setServerError('')

    if (isLogin) {
      if (!validateLogin()) return
    } else {
      if (!validateRegister()) return
    }

    setLoading(true)
    try {
      if (isLogin) {
        const data = await loginService(loginForm.email, loginForm.password)
        login({
          id: data.id,
          nombre: data.nombre,
          email: data.email,
          rol: data.rol || 'CIUDADANO',
        })
        router.push('/dashboard')
      } else {
        await registerService(registerForm.nombre, registerForm.email, registerForm.password)
        handleTabChange('login')
      }
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Ocurrio un error'
      setServerError(message)
    } finally {
      setLoading(false)
    }
  }

  const inputClass = (field: string, dark: boolean) => {
    const base = 'px-4 py-2.5 rounded-lg text-sm outline-none border transition-colors'
    const darkStyle = dark
      ? 'bg-gray-800 border-gray-700 text-white placeholder-gray-500 focus:border-gray-400'
      : 'bg-white border-gray-200 text-gray-900 placeholder-gray-400 focus:border-gray-900'
    const errorStyle = errors[field] ? 'border-red-500' : ''
    return `${base} ${darkStyle} ${errorStyle}`
  }

  return (
    <div className="flex-1 flex items-center justify-center p-4">
      <div className="w-full max-w-md">

        <div className={`flex rounded-t-2xl overflow-hidden transition-colors duration-300 ${
          isLogin ? 'bg-gray-900' : 'bg-white'
        }`}>
          <button
            onClick={() => handleTabChange('login')}
            className={`flex-1 py-4 text-sm font-semibold transition-all ${
              isLogin
                ? 'text-white border-b-2 border-white'
                : 'text-gray-400 hover:text-gray-600'
            }`}
          >
            Iniciar sesion
          </button>
          <button
            onClick={() => handleTabChange('register')}
            className={`flex-1 py-4 text-sm font-semibold transition-all ${
              !isLogin
                ? 'text-gray-900 border-b-2 border-gray-900'
                : 'text-gray-500 hover:text-gray-300'
            }`}
          >
            Registrarse
          </button>
        </div>

        <div className={`rounded-b-2xl shadow-xl p-8 transition-colors duration-300 ${
          isLogin ? 'bg-gray-900 text-white' : 'bg-white text-gray-900'
        }`}>
          <h1 className="text-2xl font-bold mb-6">
            {isLogin ? 'Bienvenido de vuelta' : 'Crear cuenta'}
          </h1>

          {serverError && (
            <div className={`mb-4 px-4 py-3 rounded-lg text-sm border ${
              isLogin
                ? 'bg-red-900/30 border-red-500/30 text-red-400'
                : 'bg-red-50 border-red-200 text-red-600'
            }`}>
              {serverError}
            </div>
          )}

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">

            {!isLogin && (
              <div className="flex flex-col gap-1">
                <label className="text-xs font-semibold uppercase tracking-wide text-gray-500">
                  Nombre completo
                </label>
                <input
                  type="text"
                  placeholder="Juan Perez"
                  value={registerForm.nombre}
                  onChange={(e) => setRegisterForm({ ...registerForm, nombre: e.target.value })}
                  className={inputClass('nombre', false)}
                />
                {errors.nombre && <p className="text-xs text-red-400">{errors.nombre}</p>}
              </div>
            )}

            <div className="flex flex-col gap-1">
              <label className={`text-xs font-semibold uppercase tracking-wide ${
                isLogin ? 'text-gray-400' : 'text-gray-500'
              }`}>
                Email
              </label>
              <input
                type="email"
                placeholder="tu@email.com"
                value={isLogin ? loginForm.email : registerForm.email}
                onChange={(e) => isLogin
                  ? setLoginForm({ ...loginForm, email: e.target.value })
                  : setRegisterForm({ ...registerForm, email: e.target.value })
                }
                className={inputClass('email', isLogin)}
              />
              {errors.email && <p className="text-xs text-red-400">{errors.email}</p>}
            </div>

            <div className="flex flex-col gap-1">
              <label className={`text-xs font-semibold uppercase tracking-wide ${
                isLogin ? 'text-gray-400' : 'text-gray-500'
              }`}>
                Contrasena
              </label>
              <input
                type="password"
                placeholder="••••••••"
                value={isLogin ? loginForm.password : registerForm.password}
                onChange={(e) => isLogin
                  ? setLoginForm({ ...loginForm, password: e.target.value })
                  : setRegisterForm({ ...registerForm, password: e.target.value })
                }
                className={inputClass('password', isLogin)}
              />
              {errors.password && <p className="text-xs text-red-400">{errors.password}</p>}
              {!isLogin && (
                <p className="text-xs text-gray-400">
                  Minimo 8 caracteres, una mayuscula y un caracter especial
                </p>
              )}
            </div>

            {!isLogin && (
              <div className="flex flex-col gap-1">
                <label className="text-xs font-semibold uppercase tracking-wide text-gray-500">
                  Confirmar contrasena
                </label>
                <input
                  type="password"
                  placeholder="••••••••"
                  value={registerForm.confirmPassword}
                  onChange={(e) => setRegisterForm({ ...registerForm, confirmPassword: e.target.value })}
                  className={inputClass('confirmPassword', false)}
                />
                {errors.confirmPassword && <p className="text-xs text-red-400">{errors.confirmPassword}</p>}
              </div>
            )}

            {isLogin && (
              <div className="flex items-center justify-between">
                <label className="flex items-center gap-2 text-sm text-gray-400 cursor-pointer">
                  <input type="checkbox" className="rounded" />
                  Guardar sesion
                </label>
                <button
                  type="button"
                  className="text-sm text-blue-400 hover:text-blue-300 transition"
                >
                  Olvidaste tu contrasena?
                </button>
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className={`mt-2 py-3 rounded-lg font-semibold text-sm transition-all disabled:opacity-40 disabled:cursor-not-allowed ${
                isLogin
                  ? 'bg-white text-gray-900 hover:bg-gray-100'
                  : 'bg-gray-900 text-white hover:bg-gray-800'
              }`}
            >
              {loading
                ? 'Cargando...'
                : isLogin ? 'Iniciar sesion' : 'Crear cuenta'
              }
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}

export default LoginPage