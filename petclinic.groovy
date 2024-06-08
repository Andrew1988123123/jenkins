pipeline {
    agent {
        kubernetes {
            defaultContainer 'maven'
            yaml '''
apiVersion: v1
kind: Pod
metadata:
  name: petclinic
  namespace: jenkins
spec:
  containers:
    - name: maven
      image: maven:latest
      command:
        - sleep
      args:
        - "99d"
      volumeMounts:
        - name: maven-cache
          mountPath: /root/.m2/
          readOnly: false
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
    - name: dind
      image: docker:dind
      imagePullPolicy: Always
      tty: true
      env:
        - name: DOCKER_TLS_CERTDIR
          value: ""
      securityContext:
        privileged: true
      volumeMounts:
        - name: maven-cache
          mountPath: /root/.m2/
          readOnly: false
  volumes:
    - name: maven-cache
      persistentVolumeClaim:
        claimName: maven-cache
    - name: kaniko-secret
      secret:
        secretName: docker-registry
        items:
          - key: .dockerconfigjson
            path: config.json
'''
        }
    }
    environment {
        DOCKER_HOST = 'tcp://localhost:2375'
        TESTCONTAINERS_RYUK_DISABLED = 'true'
        DOCKER_TLS_VERIFY = '0'
    }
    stages {
        stage('Run git') {
            steps {
                git url: 'https://github.com/spring-projects/spring-petclinic.git', branch: 'main'
                container('maven') {
                    sh """#!/bin/bash
                    whoami
                    """
                }
            }
        }

        stage('Test') {
            steps {
                sh """#!/bin/bash
                mvn -B -ntp clean package -DskipTests
                """
            }
        }

        stage('maven') {
            steps {
                container('maven') {
                    sh '''#!/bin/bash
                    mvn test
                    '''
                }

            }
        }
    }
}
