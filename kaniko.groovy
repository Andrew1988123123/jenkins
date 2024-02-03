def DOCKER_REGISTRY = '74734589924'

pipeline {
    agent {
        kubernetes {
            defaultContainer 'maven'
            yamlFile 'kaniko.yaml'
        }
    }

    stages {
        stage('Run git') {
            steps {
                git url: 'https://github.com/scriptcamp/kubernetes-kaniko.git', branch: 'main'
                container('maven') {
                    sh 'echo `pwd`'
                }
            }
        }

        stage('Run maven') {
            steps {

                    sh 'ls -la'

            }
        }

        stage('Build kaniko') {
            steps {
                git url: 'https://github.com/Andrew1988123123/jenkins.git', branch: 'main'

                    sh """
                             mvn -v
                       """

            }
        }

        stage('Run kaniko') {
            steps {
                container('kaniko') {
                    sh """
            /kaniko/executor --context `pwd` \
                             --dockerfile=Dockerfile \
                             --cache=false \
                             --verbosity=info \
                             --log-format=color \
                             --log-timestamp=true \
                             --destination=${DOCKER_REGISTRY}/hello-kaniko:1.0
                       """
                }
            }
        }
    }
}

//
//pipeline {
//    agent {
//        kubernetes {
//            defaultContainer 'maven'
//            yamlFile 'kaniko.yaml'
//        }
//    }
//
//
//    stage('Get a Maven project') {
//        git url: 'https://github.com/scriptcamp/kubernetes-kaniko.git', branch: 'main'
//        container('maven') {
//            stage('Build a Maven project') {
//                sh '''
//                        echo pwd
//                        '''
//            }
//        }
//    }
//
//    stage('Build Java Image') {
//        container('kaniko') {
//            stage('Build a Go project') {
//                sh """
//            /kaniko/executor --context `pwd` \
//                             --dockerfile=Dockerfile \
//                             --cache=false \
//                             --verbosity=info \
//                             --log-format=color \
//                             --log-timestamp=true \
//                             --destination=${DOCKER_REGISTRY}/hello-kaniko:1.0
//                        """
//            }
//        }
//    }
//}
