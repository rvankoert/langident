FROM java:8-jdk-alpine

ARG MAVEN_VERSION=3.3.9
ENV MAVEN_HOME=/usr/lib/mvn

WORKDIR /

COPY .git /build/.git

ENV PATH $PATH:$MAVEN_HOME/bin

# Install maven and git (using proper tar and wget, rather than their limited busybox cousins)
RUN apk update \
  && apk upgrade \
  && apk add git tar wget \
  && wget -q https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
  && wget -q https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz.sha1 \
  && echo -e "$(cat apache-maven-$MAVEN_VERSION-bin.tar.gz.sha1)  apache-maven-$MAVEN_VERSION-bin.tar.gz" \
    | sha1sum -c - \
  && mkdir -p $MAVEN_HOME \
  && tar xf apache-maven-$MAVEN_VERSION-bin.tar.gz -C $MAVEN_HOME --strip-components=1 \
  && rm apache-maven-$MAVEN_VERSION-bin.tar.gz apache-maven-$MAVEN_VERSION-bin.tar.gz.sha1 \
  && cd /build \
  && git reset --hard HEAD \
  && mvn package \
  && cp -R target/appassembler/* /usr/local \
  && mv langident.yml /etc \
  && cd / \
  && apk del git tar wget \
  && rm -rf /build ~/.m2 "$MAVEN_HOME" /var/cache/apk/*

EXPOSE 8080 8081

CMD ["/usr/local/bin/langident", "server", "/etc/langident.yml"]
