FROM java:8-jdk-alpine

ARG MAVEN_VERSION=3.3.9
ENV MAVEN_HOME=/usr/lib/mvn

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
  && apk del tar wget \
  && rm -rf /var/cache/apk/* \
  && rm apache-maven-$MAVEN_VERSION-bin.tar.gz apache-maven-$MAVEN_VERSION-bin.tar.gz.sha1

ENV PATH $PATH:$MAVEN_HOME/bin

WORKDIR /build

COPY . /build

RUN mvn package \
# && du -hs ~/.m2/repository \
  && rm -rf ~/.m2/repository

EXPOSE 8080 8081

CMD ["target/appassembler/bin/langident", "server", "langident.yml"]
