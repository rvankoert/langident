FROM maven:3.3-jdk-8

COPY . /root/langident-build

WORKDIR /root/langident-build

RUN ls
RUN mvn clean package

EXPOSE 8080
EXPOSE 8081

CMD ["./target/appassembler/bin/langident", "server", "langident.yml"]
