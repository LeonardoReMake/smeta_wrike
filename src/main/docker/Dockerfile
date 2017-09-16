FROM tomcat:8.5.14-jre8
#ADD ./non-free.list /etc/apt/sources.list.d
RUN apt-get update
RUN apt-get -y install nano
RUN apt-get -y install mc
RUN apt-get -y install elinks
RUN apt-get -y install postgresql-client
#RUN apt-get -y install openjdk-8-jdk
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY ./ROOT.war /usr/local/tomcat/webapps/
#COPY ./ROOT.xml /usr/local/tomcat/conf/Catalina/localhost/

