pipeline {
    agent any
    tools{
        gradle 'GradleTools'
        maven 'MavenTools'
    }

    environment{
        BUILD_USER = ''
    }

    parameters {
        choice(name: 'COMPILATION-TCOOLS', choices: ['maven', 'gradle'], description: 'Tool')
    }



    stages {

    stage('Carga script') {
        steps {
            script {
                code = load "./${params.COMPILATION-TCOOLS}.groovy"
            }
        }
    }

        stage('Compilación & Test') {

         steps {
                script {
                    code.Build()
                }
            }
        }

        stage("Análisis Sonarqube") {
            environment {
                scannerHome = tool "SonarScanner"
            }
            steps {
                 withSonarQubeEnv("SonarServer-1") {
                    sh "mvn clean verify sonar:sonar -Dsonar.projectKey=gradle-test -Dsonar.host.url=http://178.128.155.87:9000  -Dsonar.login=sqp_10c86add5add0c41abe71d9f0377f11070a08e57 -Dsonar.java.binaries=build"
                }
            }
            
        }
        
        stage("Comprobación Quality gate") {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }  

        stage("Run Code") {
         steps {
                script {
                    code.Run()
                }
            }            
            
        }

        stage('Build Deploy Code') {
            when {
                branch 'main'
            }
            steps {
                sh """
                echo "Building Artifact"
                """

                sh """
                echo "Deploying Code"
                """
            }
        }
    }
    post{
        success{
            setBuildStatus("Build succeeded", "SUCCESS");
        }

        failure {
            setBuildStatus("Build failed", "FAILURE");
        } 


    }
}



void setBuildStatus(String message, String state) {
    step([
        $class: "GitHubCommitStatusSetter",
        reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/Eduardo-L-R/ms-iclab"],
        contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
        errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
        statusResultSource: [$class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]]]
    ]);
}