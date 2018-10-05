FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--maxHttpThreads", "25", "--no-cluster", "--addjars", "/opt/payara/libs/", "--hzconfigfile", "/opt/payara/hazelcast.xml", "--postdeploycommandfile", "/opt/payara/post-deploy.asadmin", "--name", "javaee8-service"]

COPY src/main/docker/* /opt/payara/
COPY build/libs/javaee8-service.war /opt/payara/deployments/
