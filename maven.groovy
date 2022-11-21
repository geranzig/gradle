def Build() {

    sh '''#!/bin/bash
            chmod +x mvnw
            '''


  sh './mvnw clean compile -e'
  sh './mvnw clean test -e'
}

def Sonar(){
    sh "./mvnw clean verify sonar:sonar -Dsonar.projectKey=gradle-test -Dsonar.host.url=http://178.128.155.87:9000  -Dsonar.login=sqp_10c86add5add0c41abe71d9f0377f11070a08e57 -Dsonar.java.binaries=build"

}

return this