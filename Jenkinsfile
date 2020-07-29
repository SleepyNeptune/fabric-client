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
        bash 'for file in build/libs/*.jar ; do mv $file ${file//1\.0\.0/${env.BUILD_ID}} ; done'
      }
    }
    stage('Discord') {
      steps {
        discordSend description: "Jenkins Pipeline Build", link: env.BUILD_URL, result: currentBuild.currentResult, title: JOB_NAME, webhookURL: credentials('toast-client-fabric')

About
      }
    }
  }
  post {
    always {
      archiveArtifacts(artifacts: 'build/libs/**/*.jar', fingerprint: true)
    }

  }
}
