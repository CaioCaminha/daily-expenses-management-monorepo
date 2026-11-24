# DEM - java-onboarding application
## idea
The idea is to document every decision and every improvement made measured
using a load test framework like k6 or JMeter;

The idea of this application is to expose it to high load tests and see how it performs
then compares to another spring application made with kotlin and coroutines (**kotlin-analytics-service**)

Once webclient is implemented, the application will be able to
 - create users
 - create alerts and billings
 - receive input on CSV / PDF / JSON
 - extract categories from description and location name
 - Insert those datas on database


Will be missing:
 - Authorization and Authentication
   - AWS Cognito - Federated Auth
   - Keycloak OAuth 2.0