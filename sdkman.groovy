pipeline {
    agent {
        kubernetes {
            defaultContainer 'sdkman'
            yamlFile 'sdkman.yaml'
        }
    }

    stages {
        stage('Run git') {
            steps {
                git url: 'https://github.com/Andrew1988123123/jenkins.git', branch: 'master'
                container('sdkman') {
                    sh 'echo `pwd`'
                    sh 'whoami'
                }
            }
        }

        stage('Run java version') {
            steps {
                sh 'java -version'
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
                    sh 'update-java-alternatives --set /usr/lib/jvm/java-17-amazon-corretto'
                    sh 'java -version'
                }

            }
        }
        stage('Check java17 version') {
            steps {
                sh 'java -version'
            }
        }
    }
}
