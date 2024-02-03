pipeline {
    agent {
        kubernetes {
            defaultContainer 'maven'
            yamlFile 'kaniko.yaml'
        }
    }

    stages {
        stage('Run maven') {
            steps {
                sh 'mvn -version'
            }
        }
    }
}

//def DOCKER_REGISTRY = '74734589924'
//pipeline {
//    agent {
//        kubernetes {
//            defaultContainer 'maven'
//            yamlFile 'kaniko.yaml'
//        }
//    }
//    stages {
//        stage('Get a Maven project') {
//            git url: 'https://github.com/scriptcamp/kubernetes-kaniko.git', branch: 'main'
//            container('maven') {
//                stage('Build a Maven project') {
//                    sh '''
//                        echo pwd
//                        '''
//                }
//            }
//        }
//
//        stage('Build Java Image') {
//            container('kaniko') {
//                stage('Build a Go project') {
//                    sh """
//            /kaniko/executor --context `pwd` \
//                             --dockerfile=Dockerfile \
//                             --cache=false \
//                             --verbosity=info \
//                             --log-format=color \
//                             --log-timestamp=true \
//                             --destination=${DOCKER_REGISTRY}/hello-kaniko:1.0
//                        """
//                }
//            }
//        }
//    }
//}
