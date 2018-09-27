FROM qaware/zulu-centos-payara-micro:8u181-5.183

COPY build/libs/javaee8-service.war /opt/payara/deployments/
