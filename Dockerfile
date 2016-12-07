FROM openjdk:8-jre-alpine

ARG MAVEN_VERSION=3.3.9

WORKDIR /build
COPY src ./src
COPY pom.xml .
COPY langident.yml .

ENV maven_dir=/build/apache-maven-$MAVEN_VERSION

# Install Maven using proper wget, rather than its limited busybox cousin.
RUN apk add --no-cache openjdk8 wget \
  && wget -q https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
  && wget -q https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz.sha1 \
  && echo -e "$(cat apache-maven-$MAVEN_VERSION-bin.tar.gz.sha1)  apache-maven-$MAVEN_VERSION-bin.tar.gz" \
    | sha1sum -c - \
  && tar xzf apache-maven-$MAVEN_VERSION-bin.tar.gz \
  && ${maven_dir}/bin/mvn package \
  && cp -R target/appassembler/* /usr/local \
  && mv langident.yml /etc \
  && cd / \
  && apk del openjdk8 wget \
  && rm -rf /build ~/.m2

EXPOSE 8080 8081

CMD ["/usr/local/bin/langident", "server", "/etc/langident.yml"]
