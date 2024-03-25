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

        stage('Run sdkman') {
            steps {
                sh 'java -version'
            }
        }

        stage('Build version') {
            steps {
                sh 'node -v'
            }
        }
    }
}
