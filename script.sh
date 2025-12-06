rm ./wildfly-30.0.1.Final/standalone/deployments/person-management.war
rm ./wildfly-30.0.1.Final/standalone/deployments/person-management.war.deployed

mv ./target/person-management.war ./wildfly-30.0.1.Final/standalone/deployments/

./wildfly-30.0.1.Final/bin/standalone.sh
