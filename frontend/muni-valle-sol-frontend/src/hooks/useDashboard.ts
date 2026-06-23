import useReports from './useReports'
import useAlerts from './useAlerts'

const useDashboard = () => {
  const { reports, loading: loadingReports, error: errorReports } = useReports()
  const { loading: loadingAlerts, error: errorAlerts } = useAlerts()

  return {
    reports,
    loading: loadingReports || loadingAlerts,
    error: errorReports || errorAlerts,
  }
}

export default useDashboard
