compile:
	mvn -q -DskipTests install

run:
	mvn -q -f cli/pom.xml exec:java