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

    stage('Discord') {
      steps {
        discordSend(webhookURL: 'https://discordapp.com/api/webhooks/738156591232319600/f4Gw4bKlHG842uawnEF3TLvqTpSSZ5eOIUzbtZIHnhKyZXjqwaTRpmxuof1gFu0mibsV', successful: true)
      }
    }

  }
  post {
    always {
      archiveArtifacts(artifacts: 'build/libs/**/*.jar', fingerprint: true)
    }

  }
}