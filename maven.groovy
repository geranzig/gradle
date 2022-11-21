def Build() {
  sh 'mvnw clean compile -e'
  sh 'mvnw clean test -e'
}

return this