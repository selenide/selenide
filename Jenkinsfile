node {
  stage('Checkout') {
    checkout scm
  }
  stage('Clean') {
    sh './gradle clean'
  }
  stage('Dependencies') {
    sh './gradle libs'
  }
  stage('Build') {
    sh './gradle compileJava'
  }

  try {
    stage('Unit-tests') {
      sh './gradlew test'
    }
    stage('Check') {
      sh './gradlew check'
    }
    stage('Chrome tests') {
      sh './gradlew chrome_headless'
    }
    stage('firefox tests') {
      sh './gradlew firefox_headless'
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
