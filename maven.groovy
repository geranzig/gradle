def Build() {

    sh '''#!/bin/bash
            chmod +x mvnw
            '''


  sh './mvnw clean compile -e'
  sh './mvnw clean test -e'
}

return this