import { create } from 'zustand'
import { Alert } from '@/types/Alert'

interface AlertState {
  alertasActivas: Alert[]
  setAlertasActivas: (alertas: Alert[]) => void
  agregarAlerta: (alerta: Alert) => void
  limpiarAlertas: () => void
}

const useAlertStore = create<AlertState>((set) => ({
  alertasActivas: [],
  setAlertasActivas: (alertas: Alert[]) => set({ alertasActivas: alertas }),
  agregarAlerta: (alerta: Alert) =>
    set((state: AlertState) => ({ alertasActivas: [...state.alertasActivas, alerta] })),
  limpiarAlertas: () => set({ alertasActivas: [] }),
}))

export default useAlertStore