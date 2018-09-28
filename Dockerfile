FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--maxHttpThreads", "25", "--addjars", "/opt/payara/libs/", "--hzconfigfile", "/opt/payara/hazelcast.xml", "--postdeploycommandfile", "/opt/payara/post-deploy.asadmin", "--name", "javaee8-service"]

COPY src/main/docker/* /opt/payara/
COPY build/postgresql/* /opt/payara/libs/
COPY build/activemq/* /opt/payara/deployments/
COPY build/libs/javaee8-service.war /opt/payara/deployments/
