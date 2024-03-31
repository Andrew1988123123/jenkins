# Use a Debian-based image with Maven, Node.js, Java, and Maven
FROM ubuntu:22.04

# Define version variables
ARG NODE_VERSION=14.20.0
ARG NVM_VERSION=0.39.7
ARG JAVA_VERSION=11.0.22-amzn
ARG MAVEN_VERSION=3.6.3
ARG YQ_VERSION=4.13.4

# Install necessary packages
RUN apt-get update \
    && apt-get install -y curl wget unzip zip git jq \
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

RUN useradd -m jvm_user && \
    mkdir -p /etc/sudoers.d && \
    echo 'jvm_user ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers.d/jvm_user

USER jvm_user
# Install SDKMAN, Java, and Maven
RUN curl -s "https://get.sdkman.io" | bash \
    && /bin/bash -c "source \"$HOME/.sdkman/bin/sdkman-init.sh\" \
    && sdk install java $JAVA_VERSION \
    && sdk install maven $MAVEN_VERSION"

# Set environment variables
ENV JAVA_HOME="$HOME/.sdkman/candidates/java/current"
ENV MAVEN_HOME="$HOME/.sdkman/candidates/maven/current"
ENV NODE_HOME="$HOME/.nvm/versions/node/$NODE_VERSION"
ENV NPM_HOME="$NODE_HOME/lib/node_modules"

# Combine the specified PATH components
ENV PATH="$MAVEN_HOME/bin:$JAVA_HOME/bin:$NODE_HOME/bin:$NPM_HOME/bin:/root/.sdkman/candidates/java/current/bin:/root/.sdkman/candidates/maven/current/bin:/root/.nvm/versions/node/v$NODE_VERSION/bin:/.sdkman/candidates/java/current/bin:/.sdkman/candidates/maven/current/bin:/.nvm/versions/node/$NODE_VERSION/bin:/.nvm/versions/node/$NODE_VERSION/lib/node_modules/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:$PATH"
