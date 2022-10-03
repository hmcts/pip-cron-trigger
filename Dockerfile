ARG APP_INSIGHTS_AGENT_VERSION=3.2.10
FROM hmctspublic.azurecr.io/base/java:11-distroless

COPY build/libs/pip-cron-trigger.jar /opt/app/

EXPOSE 4550
CMD [ "pip-cron-trigger.jar" ]
