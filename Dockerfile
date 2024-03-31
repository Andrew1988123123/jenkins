FROM ubuntu:22.04

# Define version variables
ARG NODE_VERSION=14.20.0
ARG NVM_VERSION=0.39.7
ARG JAVA_11_VERSION=11
ARG JAVA_11_CORRETTO_VERSION=$JAVA_11_VERSION.0.22.7
ARG JAVA_17_VERSION=17
ARG JAVA_17_CORRETTO_VERSION=$JAVA_17_VERSION.0.10.7
ARG JAVA_21_VERSION=21
ARG JAVA_21_CORRETTO_VERSION=$JAVA_21_VERSION.0.2.13
ARG MAVEN_VERSION=3.6.3
ARG YQ_VERSION=4.13.4

# Install necessary packages
RUN apt-get update \
    && apt-get install -y vim bash curl wget unzip zip git jq gnupg2 java-common \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install Java
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk && \
    apt-get install -y openjdk-17-jdk && \
    apt-get install -y openjdk-21-jdk

# Install Maven
RUN wget "https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" -O /tmp/apache-maven.tar.gz \
    && tar xzf /tmp/apache-maven.tar.gz -C /opt \
    && ln -s /opt/apache-maven-$MAVEN_VERSION /opt/maven \
    && rm /tmp/apache-maven.tar.gz

# Install NVM and Node.js
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v$NVM_VERSION/install.sh | bash \
    && /bin/bash -c "source \"$HOME/.nvm/nvm.sh\" \
    && nvm install $NODE_VERSION \
    && nvm use $NODE_VERSION \
    && nvm alias default $NODE_VERSION"

# Install yq
RUN wget "https://github.com/mikefarah/yq/releases/download/v$YQ_VERSION/yq_linux_amd64" -O /usr/bin/yq \
    && chmod +x /usr/bin/yq

# Set environment variables
#ENV JAVA_HOME=/usr/lib/jvm/java-$JAVA_11_VERSION-amazon-corretto
ENV JAVA_HOME=/usr/lib/jvm/java-1.$JAVA_11_VERSION.0-openjdk-amd64
ENV JAVA_HOME17=/usr/lib/jvm/java-1.$JAVA_17_VERSION.0-openjdk-amd64
ENV JAVA_HOME21=/usr/lib/jvm/java-1.$JAVA_21_VERSION.0-openjdk-amd64
ENV MAVEN_HOME=/opt/maven
ENV NODE_HOME="$HOME/.nvm/versions/node/$NODE_VERSION"
ENV NPM_HOME="$NODE_HOME/lib/node_modules"
ENV PATH="$JAVA_HOME/bin:$JAVA_HOME17/bin:$JAVA_HOME21/bin:$MAVEN_HOME/bin:$NODE_HOME/bin:$NPM_HOME/bin:/root/.nvm/versions/node/v$NODE_VERSION/bin:/.nvm/versions/node/$NODE_VERSION/bin:/.nvm/versions/node/$NODE_VERSION/lib/node_modules/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH"
