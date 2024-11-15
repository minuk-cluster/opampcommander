FROM golang:1.23 as builder
WORKDIR /
RUN wget https://github.com/open-telemetry/opentelemetry-collector-contrib/archive/refs/tags/v0.112.0.tar.gz
RUN tar -zxvf v0.112.0.tar.gz
WORKDIR opentelemetry-collector-contrib-0.112.0/cmd/opampsupervisor
RUN CGO_ENABLED=0 go build
RUN echo '\
server:\n\
  endpoint: ws://127.0.0.1:8080/ws/v1/opamp\n\
  tls:\n\
    insecure_skip_verify: true\n\
capabilities:\n\
  reports_effective_config: true\n\
  reports_own_metrics: true\n\
  reports_health: true\n\
  accepts_remote_config: true\n\
  reports_remote_config: true\n\
agent:\n\
  executable: /otelcol-contrib\n\
storage:\n\
  directory: /tmp'\
>> /config.yaml

FROM otel/opentelemetry-collector-contrib:0.112.0
COPY --from=builder /opentelemetry-collector-contrib-0.112.0/cmd/opampsupervisor/opampsupervisor /opampsupervisor
COPY --from=builder /config.yaml /etc/opampsupervisor/config.yaml
ENTRYPOINT ["/opampsupervisor"]
CMD ["--config", "/etc/opampsupervisor/config.yaml"]
