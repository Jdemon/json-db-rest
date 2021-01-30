export VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
export ARTIFACT_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
export GROUP_ID=$(mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout)
echo version=$VERSION
echo artifactId=$ARTIFACT_ID
echo groupId=$GROUP_ID
mvn clean package -P release -DskipTests
mvn install:install-file -DgroupId=$GROUP_ID -DartifactId=$ARTIFACT_ID -Dversion=$VERSION -Dfile=./target/$ARTIFACT_ID.jar -Dpackaging=jar -Djavadoc=./target/$ARTIFACT_ID-javadoc.jar -Dsources=./target/$ARTIFACT_ID-sources.jar -DgeneratePom=true