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
    stage('Check') {
      sh './gradle check'
    }
    stage('Unit-tests') {
      sh './gradle test'
    }
    wrap([$class: 'Xvfb', displayNameOffset: env.BUILD_NUMBER.toInteger() % 100 + 20]) {
      stage('Chrome tests') {
        sh './gradle chrome'
      }
      stage('firefox tests') {
        sh './gradle firefox'
      }
      stage('htmlunit tests') {
        sh './gradle htmlunit'
      }
      stage('phantomjs tests') {
        sh './gradle phantomjs'
      }
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