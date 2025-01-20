# AW2_Project

Project of the Web Applications 2 course, made in <a href='https://kotlinlang.org/' target='_blank'>Kotlin</a> by using the <a href='https://spring.io/' target='_blank'>Spring</a> framework. The goal was to develop a system for temporary job placement
services based on spring microservices as backend, and then a user-friendly front-end that provides seamless access to
all required and offered functionalities. All that was implemented by creating all the needed microservices, an API gateway as a bridge beetween them and the front-end, and by leveraging on other well-known frameworks to handle other requirements. The <a href='https://www.keycloak.org/' target='_blank'>Keycloak IAM</a> was used to handle authentication, while <a href='https://camel.apache.org/' target='_blank'>Apache Camel</a> was used to handle the reception of e-mails on gmail and forward it to the microservice. Also <a href='https://kafka.apache.org/' target='_blank'>Apache Kafka</a> has been used  to handle service downtime and <a href='https://debezium.io/' target='_blank'>Debezium</a> to implement the Outbox pattern where needed and guarantee transactionality. Finally, observability and monitoring on the system has been implemented through the usage of <a href='https://grafana.com/oss/loki/' target='_blank'>Loki</a> as log aggregation data source, and <a href='https://prometheus.io/' target='_blank'>Prometheus</a> as metric data source. All was displayed through <a href='https://grafana.com/' target='_blank'>Grafana</a> dashboards. Additional details can be found <a href='https://drive.google.com/file/d/1gqA2RrxHeYjL4Im6iysYdxFb9YDPl95v/view?usp=sharing' target='_blank'>here</a>.

# Keycloak Roles and Credentials
## customers
- Username: customer1
- Password: Customer1@2025
###
- Username: customer2
- Password: Customer2@2025
###
- Username: customer3
- Password: Customer3@2025

## professionals
- Username: professional1
- Password: Professional1@2025
###
- Username: professional2
- Password: Professional2@2025
###
- Username: professional3
- Password: Professional3@2025

## operator
- Username: operator1
- Password: operator1

## manager
- Username: manager1
- Password: manager1
