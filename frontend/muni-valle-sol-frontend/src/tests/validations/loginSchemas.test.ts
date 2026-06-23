import { describe, it, expect } from 'vitest'
import { z } from 'zod'

const DOMINIOS_VALIDOS = [
  'gmail.com', 'outlook.com', 'hotmail.com', 'yahoo.com',
  'icloud.com', 'live.com', 'msn.com', 'duocuc.cl',
  'uc.cl', 'usach.cl', 'uach.cl', 'puc.cl', 'udp.cl',
  'udd.cl', 'unab.cl', 'uai.cl', 'uft.cl', 'inacap.cl',
]

const emailSchema = z
  .string()
  .email('Ingresa un correo electronico valido')
  .refine((email) => DOMINIOS_VALIDOS.includes(email.split('@')[1]), 'El dominio del correo no esta permitido')

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

describe('emailSchema', () => {
  it('debería aceptar gmail.com', () => expect(emailSchema.safeParse('juan@gmail.com').success).toBe(true))
  it('debería aceptar duocuc.cl', () => expect(emailSchema.safeParse('alumno@duocuc.cl').success).toBe(true))
  it('debería aceptar uc.cl', () => expect(emailSchema.safeParse('alumno@uc.cl').success).toBe(true))
  it('debería rechazar dominio no permitido', () => expect(emailSchema.safeParse('juan@empresa.com').success).toBe(false))
  it('debería rechazar cadena sin formato email', () => expect(emailSchema.safeParse('noesuncorreo').success).toBe(false))
  it('debería rechazar email vacío', () => expect(emailSchema.safeParse('').success).toBe(false))
})

describe('passwordSchema', () => {
  it('debería aceptar contraseña válida', () => expect(passwordSchema.safeParse('Segura123!').success).toBe(true))
  it('debería rechazar menos de 8 caracteres', () => expect(passwordSchema.safeParse('Abc1!').success).toBe(false))
  it('debería rechazar sin mayúsculas', () => expect(passwordSchema.safeParse('sinmayuscula1!').success).toBe(false))
  it('debería rechazar sin caracter especial', () => expect(passwordSchema.safeParse('SinEspecial1').success).toBe(false))
  it('debería rechazar contraseña vacía', () => expect(passwordSchema.safeParse('').success).toBe(false))
})

describe('loginSchema', () => {
  it('debería validar login correcto', () => expect(loginSchema.safeParse({ email: 'juan@gmail.com', password: 'cualquiera' }).success).toBe(true))
  it('debería rechazar email inválido', () => expect(loginSchema.safeParse({ email: 'noesmail', password: '12345678' }).success).toBe(false))
  it('debería rechazar contraseña vacía', () => expect(loginSchema.safeParse({ email: 'juan@gmail.com', password: '' }).success).toBe(false))
})

describe('registerSchema', () => {
  const validos = { nombre: 'Juan Pérez', email: 'juan@gmail.com', password: 'Segura123!', confirmPassword: 'Segura123!' }

  it('debería validar registro correcto', () => expect(registerSchema.safeParse(validos).success).toBe(true))
  it('debería rechazar contraseñas que no coinciden', () => {
    const r = registerSchema.safeParse({ ...validos, confirmPassword: 'Otra123!' })
    expect(r.success).toBe(false)
    if (!r.success) expect(r.error.issues[0].path).toContain('confirmPassword')
  })
  it('debería rechazar nombre con menos de 2 caracteres', () => expect(registerSchema.safeParse({ ...validos, nombre: 'J' }).success).toBe(false))
  it('debería rechazar dominio no permitido', () => expect(registerSchema.safeParse({ ...validos, email: 'juan@empresa.com' }).success).toBe(false))
  it('debería rechazar contraseña débil', () => expect(registerSchema.safeParse({ ...validos, password: 'debil', confirmPassword: 'debil' }).success).toBe(false))
})
