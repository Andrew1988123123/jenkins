FROM ubuntu:22.04

# Define version variables
ARG JAVA_11_CORRETTO_VERSION=11.0.22-amzn
ARG JAVA_17_CORRETTO_VERSION=17.0.10-amzn
ARG JAVA_21_CORRETTO_VERSION=21.0.2-amzn
ARG MAVEN_VERSION=3.6.3
ARG NODE_VERSION=14.20.0
ARG NVM_VERSION=0.39.7
ARG YQ_VERSION=4.13.4

ENV SHELL=/bin/bash

SHELL ["/bin/bash", "-c"]

# Install necessary packages
RUN apt-get update \
    && apt-get install -y sudo curl wget unzip zip git jq fonts-dejavu fontconfig\
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Install NVM and Node.js
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v$NVM_VERSION/install.sh | bash \
    && /bin/bash -c "source \"$HOME/.nvm/nvm.sh\" \
    && nvm install $NODE_VERSION \
    && nvm use $NODE_VERSION \
    && nvm alias default $NODE_VERSION"

# Install yq
RUN wget "https://github.com/mikefarah/yq/releases/download/v$YQ_VERSION/yq_linux_amd64" -O /usr/bin/yq && \
    chmod +x /usr/bin/yq

# Install SDKMAN, Java, and Maven
RUN curl -s "https://get.sdkman.io" | bash && \
    source "/root/.sdkman/bin/sdkman-init.sh" && \
    sdk install java $JAVA_11_CORRETTO_VERSION && \
    sdk install java $JAVA_17_CORRETTO_VERSION && \
    sdk install java $JAVA_21_CORRETTO_VERSION && \
    sdk install maven $MAVEN_VERSION && \
    echo "yes" | sdk default java $JAVA_11_CORRETTO_VERSION && \
    echo sdkman_auto_answer=true >> ~/.sdkman/etc/config

# Set environment variables
ENV JAVA_HOME="$HOME/.sdkman/candidates/java/current"
ENV MAVEN_HOME="$HOME/.sdkman/candidates/maven/current"
ENV NODE_HOME="$HOME/.nvm/versions/node/$NODE_VERSION"
ENV NPM_HOME="$NODE_HOME/lib/node_modules"

# Combine the specified PATH components
ENV PATH="$MAVEN_HOME/bin:$JAVA_HOME/bin:$NODE_HOME/bin:$NPM_HOME/bin:/root/.nvm/versions/node/v$NODE_VERSION/bin:/.nvm/versions/node/$NODE_VERSION/bin:/.nvm/versions/node/$NODE_VERSION/lib/node_modules/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH"