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
      image: 74734589924/sdkman:1.16
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
        stage('Run git commit') {
            steps {
                script {
                    // Clone repositories into respective directories with specified branch
                    dir('CalibrationResults') {
                        git url: 'https://github.com/cloudnative-pg/cloudnative-pg.git', branch: 'main'
                        sh '''
                            git for-each-ref --format='%(refname:short)' refs/heads | while read branch; do
                                git log --since=midnight --author-date-order --date=format:'%d.%m.%Y' --format="%ad,%an,%s,$branch" $branch
                            done | sort -u -t',' -k1,3
                        '''
                    }
                    dir('Combination') {
                        git url: 'https://github.com/cloudnative-pg/charts.git', branch: 'main'
                        sh '''
                            git for-each-ref --format='%(refname:short)' refs/heads | while read branch; do
                                git log --since=midnight --author-date-order --date=format:'%d.%m.%Y' --format="%ad,%an,%s,$branch" $branch
                            done | sort -u -t',' -k1,3
                        '''
                    }
                    dir('CombinationBuilder') {
                        git url: 'https://github.com/cloudnative-pg/postgres-containers.git', branch: 'main'
                        sh '''
                            git for-each-ref --format='%(refname:short)' refs/heads | while read branch; do
                                git log --since=midnight --author-date-order --date=format:'%d.%m.%Y' --format="%ad,%an,%s,$branch" $branch
                            done | sort -u -t',' -k1,3
                        '''
                    }
                }
            }
        }
    }
}
