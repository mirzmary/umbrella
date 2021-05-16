# umbrella
The service is intended to suggest taking or not an umbrella to the user along with some more information on the current and historical weather conditions:
For the weather the https://openweathermap.org/api is used

## Prerequisites to run
1. Java 15
2. JDK

## How to run
1. The main class in the application is the UmbrellaAppilcation class, which contains the static main method, which is the entry point to the app
2. In IntelliJ (probably in other code editor apps like eclipse as well) one needs to set the main class to start up the app
3. The app starts at http://localhost:8080

## Implementation
The service is implemented as a Java Spring boot application, with 2 rest GET APIs for fetching current and historical weather details for a city.
For weather API call the Java Http client is used.

## Description
The service has 2 APIs:
1. For getting the current weather for a city, along with the necessity to take an umbrella
   GET /current?location=Berlin
   
The API returns the current weather model in case of being able to fetch the data:
{
"temperature": 17.93,
"airPressure": 1016,
"umbrellaNeeded": false
}

The API can also respond with errors:
BAD_REQUEST - Issue with the weather API
GATEWAY_TIMEOUT - When nwe are not able to fetch the data from weather API withing defined (inn this case 10 seconds) timeout
BAD_GATEWAY - If the response of the weather API is changed and we are no longer able to map it to our desired type

Besides fetching and mapping the response provided by the weather API, the current weather fetching method also pushes the result to a cache for later history.

2. For fetching the historical data (last 5 responses) of the weather, along with the statistics (average of the temperature and air pressure)
   GET /history?location=London
   
The API returns the statistics of average temperature and air pressure for the city for the last maximum 5 requests, in case of any requests were made:
{
"averageTemperature": 18.15,
"averageAirPressure": 1016.00,
"history": [
{
"temperature": 18.15,
"airPressure": 1016,
"umbrellaNeeded": false
},
{
"temperature": 18.15,
"airPressure": 1016,
"umbrellaNeeded": false
},
{
"temperature": 18.15,
"airPressure": 1016,
"umbrellaNeeded": false
},
{
"temperature": 18.15,
"airPressure": 1016,
"umbrellaNeeded": false
},
{
"temperature": 18.15,
"airPressure": 1016,
"umbrellaNeeded": false
}
]
}

The data for the history endpoint is fetched from the cache, which is filled every time when the current weather API is called for a city.
There is a possibility to use the history API provided by the weather API itself, which was intended to be used initially by me, but which appeared to be only available for the paid version.
So the data is saved in the cache on each current weather call (initially normalizing the city name for the key) and is fetched from the cache per city name on each history call.
For the cache the EhCache implementation is used.

In case, there was no data requested for the city, the API responds with NO_

## Testing
Some simple unit tests were added for the WeatherForecastService just as an example.
No good unit test coverage due to the time constraints.
No functional, integration tests were added due to the time constraints.

## CI/CD
For the CI/CD we would have used Terraform, to have infrastructure as code, to be able to track it and change easily.
For the deployment in the multi-environment case, we would have multiple .tf terraform files with the environment name, and each would have kept the corresponding to the environment values for the variables.
In the real world example, we would also have veriable.tf, where we would keep all the variable declarations, as well as a main.tf file, which would bind the config values necessary for the normal deployment to the variables.
We would also need a Jenkinsfile (in case of deploying to Jenkins) for defining the build necessary parameters, e.g. java version...
