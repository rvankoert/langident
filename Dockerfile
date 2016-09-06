FROM java:8

COPY langident.yml /root
COPY target/langident-*.jar /root/langident.jar

WORKDIR /root

EXPOSE 8080
EXPOSE 8081

CMD ["java", "-jar", "langident.jar", "server", "langident.yml"]
