# Configurar JAVA_HOME y PATH para usar el JDK instalado
export JAVA_HOME=$PWD/jdk-17
export PATH=$JAVA_HOME/bin:$PATH

# Verificar que Java está disponible
java -version

# Ejecutar la aplicación Spring Boot
java -jar target/FCMNode-0.0.1-SNAPSHOT.jar
