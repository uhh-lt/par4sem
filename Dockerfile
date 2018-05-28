FROM tomcat:8.5.30-jre8

MAINTAINER SEID MUHIE YIMAM

RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y wget mysql-server mysql-client tomcat8-user tomcat8-admin

WORKDIR /opt
RUN tomcat8-instance-create -p 19090 -c 19005 semsch && \
    chown -R www-data /opt/semsch
COPY par4sem.war /opt/semsch/webapps/par4sem.war
EXPOSE 19090
ENV JAVA_OPTS="-Xmx8g -XX:+UseConcMarkSweepGC -Dfile.encoding=UTF-8"
CMD bash /opt/semsch/bin/startup.sh && tail -f /opt/semsch/logs/catalina.out 