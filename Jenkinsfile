pipeline {
  agent {
    docker 'gradle:latest'
  }
  stages {
    stage('Gradle Build') {
      steps {
        sh './gradlew build'
      }
    }
    stage('Rename Jar') {
      steps {
        sh 'find . -name '*1.0.0*' -exec rename 's/1.0.0/100/' {} \;'
      }
    }
    stage('Discord') {
      steps {
        discordSend description: "Jenkins Pipeline Build", link: env.BUILD_URL, result: currentBuild.currentResult, title: JOB_NAME, webhookURL: credentials('toast-client-fabric')
      }
    }
  }
  post {
    always {
      archiveArtifacts(artifacts: 'build/libs/**/*.jar', fingerprint: true)
    }
  }
}
