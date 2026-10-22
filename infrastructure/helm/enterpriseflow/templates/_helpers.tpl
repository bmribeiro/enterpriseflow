{{- define "enterpriseflow.name" -}}
{{- .Chart.Name -}}
{{- end -}}

{{- define "enterpriseflow.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "enterpriseflow.labels" -}}
app.kubernetes.io/name: {{ include "enterpriseflow.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{- end -}}