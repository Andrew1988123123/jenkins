def reload = "source /root/.zshrc"
pipeline {
    agent {
        kubernetes {
            defaultContainer 'sdkman'
            yaml '''
apiVersion: v1
kind: Pod
metadata:
  name: sdkman
  namespace: jenkins
spec:
  securityContext:
    runAsUser: 0
    runAsGroup: 0
  containers:
    - name: sdkman
      image: 74734589924/sdkman:2.0
      command:
        - sleep
      args:
        - "99d"
    - name: kaniko
      image: gcr.io/kaniko-project/executor:debug
      command:
        - sleep
      args:
        - "9999999"
      volumeMounts:
        - name: kaniko-secret
          mountPath: /kaniko/.docker
          readOnly: true
  restartPolicy: Never
  volumes:
    - name: kaniko-secret
      secret:
        secretName: docker-registry
        items:
          - key: .dockerconfigjson
            path: config.json
'''
        }
    }

    stages {
        stage('Run git') {
            steps {
                git url: 'https://github.com/Andrew1988123123/jenkins.git', branch: 'sdkman'
                container('sdkman') {
                    sh '''whoami'''
                }
            }
        }

        stage('Run java version') {
            steps {
                sh """#!/bin/bash
                printf ${'$0'}
                cat /etc/shells
                env
                ${reload}
                sdk version
                java -version
                node -v
                mvn -v
                sdk use java 17.0.10-amzn
                mvn -v
                java -version
                node -v
                """
            }
        }

        stage('Run node version') {
            steps {
                sh 'node -v'
            }
        }
        stage('Set java17 version') {
            steps {
                container('sdkman') {
                    sh '''#!/bin/bash
                    source /root/.zshrc >/dev/null 2>&1
                    java -version
                    echo $JAVA_HOME
                    mvn -v
                    '''
                }

            }
        }
        stage('Check java17 version') {
            steps {
                sh '''#!/bin/bash
                source /root/.zshrc >/dev/null 2>&1
                source /root/.nvm/nvm.sh
                java -version
                mvn -v
                node -v
                nvm use 18.19.1
                node -v
                '''
            }
        }
        stage('Run kaniko') {
            steps {
                container('kaniko') {
                    sh """#!/bin/sh
            /kaniko/executor --context `pwd` \
                             --dockerfile=Dockerfile \
                             --cache=false \
                             --verbosity=info \
                             --log-format=color \
                             --log-timestamp=true \
                             --destination=74734589924/sdkman:latest
                      """
                }
            }
        }
    }
}
