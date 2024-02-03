FROM ubuntu:22.04

# Define versions
ENV NODE_VERSION=14.20.0
ENV JAVA_VERSION=11.0.22-amzn
ENV MAVEN_VERSION=3.6.3

# Install necessary packages
RUN apt-get update \
    && apt-get install -y curl wget unzip zip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \

# Install NVM and Node.js
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash \
    && /bin/bash -c "source \"$HOME/.nvm/nvm.sh\" \
    && nvm install $NODE_VERSION \
    && nvm use $NODE_VERSION \
    && nvm alias default $NODE_VERSION" \

# Install SDKMAN, Java, and Maven
RUN curl -s "https://get.sdkman.io" | bash \
    && /bin/bash -c "source \"$HOME/.sdkman/bin/sdkman-init.sh\" \
    && sdk install java $JAVA_VERSION \
    && sdk install maven $MAVEN_VERSION" \

ENV JAVA_HOME="$HOME/.sdkman/candidates/java/current"
ENV MAVEN_HOME="$HOME/.sdkman/candidates/maven/current"
ENV NODE_HOME="$HOME/.nvm/versions/node/$NODE_VERSION"
ENV NPM_HOME="$NODE_HOME/lib/node_modules"
ENV PATH="$JAVA_HOME/bin:$MAVEN_HOME/bin:$NODE_HOME/bin:$NPM_HOME/bin:$PATH"