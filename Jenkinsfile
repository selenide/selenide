node {
  try {
    stage('Check') {
      sh './gradlew check --no-daemon --console=plain'
    }
  }
  finally {
    stage("Test Report") {
      junit 'build/test-results/**/*.xml'
    }
  
    stage("Archive Artifacts") {
      archiveArtifacts artifacts: 'build/reports/**/*,build/test-results/**/*'
    }
  }
}
