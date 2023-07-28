ARG APP_INSIGHTS_AGENT_VERSION=3.4.14
FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/pip-cron-trigger.jar /opt/app/

EXPOSE 8050
CMD [ "pip-cron-trigger.jar" ]
