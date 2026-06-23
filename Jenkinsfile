pipeline {

    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser to run tests against')
        choice(name: 'SUITE_FILE', choices: ['testng.xml', 'testng-crossbrowser.xml'], description: 'TestNG suite to execute')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run browser in headless mode')
    }

    environment {
        ALLURE_RESULTS_DIR = 'allure-results'
        ALLURE_REPORT_DIR  = 'allure-report'
        MAVEN_OPTS = '-Xmx1024m'
    }

    options {
        timestamps()
        timeout(time: 45, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '15'))
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code from repository...'
                checkout scm
            }
        }

        stage('Environment Info') {
            steps {
                sh '''
                    echo "Java Version:"
                    java -version
                    echo "Maven Version:"
                    mvn -version
                '''
            }
        }

        stage('Build') {
            steps {
                echo 'Building project and resolving dependencies...'
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo "Executing automated tests with suite: ${params.SUITE_FILE}, browser: ${params.BROWSER}"
                sh """
                    mvn clean test \
                        -DsuiteXmlFile=${params.SUITE_FILE} \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -Dallure.results.directory=${ALLURE_RESULTS_DIR}
                """
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Allure Report') {
            steps {
                echo 'Generating Allure report...'
                sh 'mvn allure:report'
            }
        }

        stage('Archive Artifacts') {
            steps {
                echo 'Archiving test artifacts: screenshots, logs, exported data, reports...'
                archiveArtifacts artifacts: 'test-output/screenshots/**', allowEmptyArchive: true
                archiveArtifacts artifacts: 'test-output/data/**', allowEmptyArchive: true
                archiveArtifacts artifacts: 'test-output/logs/**', allowEmptyArchive: true
                archiveArtifacts artifacts: 'allure-results/**', allowEmptyArchive: true
                archiveArtifacts artifacts: 'allure-report/**', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            echo 'Publishing Allure HTML report...'
            allure([
                includeProperties: true,
                jdk: '',
                results: [[path: 'allure-results']]
            ])
        }
        success {
            echo 'Build and test execution completed SUCCESSFULLY.'
        }
        failure {
            echo 'Build or test execution FAILED. Check Allure report and logs for details.'
        }
        unstable {
            echo 'Build is UNSTABLE — some tests failed. Review the Allure report.'
        }
        cleanup {
            echo 'Cleaning up workspace temp files (retaining archived artifacts)...'
            sh 'rm -rf target/tmp || true'
        }
    }
}
