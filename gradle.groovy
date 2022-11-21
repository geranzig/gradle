
def Build() {
  sh './gradlew build'
}
def Test(){
  sh './gradlew test'
}

return this