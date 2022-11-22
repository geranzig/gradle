import groovy.json.JsonOutput

def COLOR_MAP =[
    'SUCCESS': 'good',
    'FAILURE': 'danger'
]



def getBuildUser() {
  def userCause = currentBuild.rawBuild.getCause(Cause.UserIdCause)
  def upstreamCause = currentBuild.rawBuild.getCause(Cause.UpstreamCause)

  if (userCause) {
    return userCause.getUserId()
  } else if (upstreamCause) {
    def upstreamJob = Jenkins.getInstance().getItemByFullName(upstreamCause.getUpstreamProject(), hudson.model.Job.class)
    if (upstreamJob) {
      def upstreamBuild = upstreamJob.getBuildByNumber(upstreamCause.getUpstreamBuild())
      if (upstreamBuild) {
        def realUpstreamCause = upstreamBuild.getCause(Cause.UserIdCause)
        if (realUpstreamCause) {
          return realUpstreamCause.getUserId()
        }
      }
    }
  }
}

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
        choice(name: 'COMPILATIONTOOLS', choices: ['maven', 'gradle'], description: 'Tool')
    }



    stages {

    stage('Carga script') {
        steps {
            script {
                code = load "./${params.COMPILATIONTOOLS}.groovy"
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
                    script {
                        code.Sonar()
                    }
                }
            }
            
        }
        
        stage("Comprobación Quality gate") {
            steps {
                waitForQualityGate abortPipeline: true
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
                 echo 'Build succeeded'
                /*setBuildStatus("Build succeeded", "SUCCESS");*/
            }

            failure {
                 echo 'Build failed'
                
              /*  setBuildStatus("Build failed", "FAILURE");*/
            } 

           always{
                script{
                    BUILD_USER = getBuildUser()
                }

                slackSend channel:'#devops-equipo5',
                        color:COLOR_MAP[currentBuild.currentResult],
                        message: "*${currentBuild.currentResult}:* ${env.JOB_NAME} ${params.COMPILATIONTOOLS} build ${env.BUILD_NUMBER} by ${BUILD_USER}"
           
            }
        }


    }




void setBuildStatus(String message, String state) {
    step([
        $class: "GitHubCommitStatusSetter",
        reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/geranzig/gradle.git"],
       // contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
       // errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
        statusResultSource: [$class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]]]
    ]);
}