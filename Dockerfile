FROM tomcat
ADD ./server.xml /usr/local/tomcat/conf/server.xml
ADD ./target/cityguide-media-0.0.1-SNAPSHOT /usr/local/tomcat/webapps/ROOT