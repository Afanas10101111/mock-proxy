Mock proxy service
========
This service can be used when you need to simulate a specific response to a post request from a server.

### Usage
1. execute mvn clean package
2. place the generated jar-file and application.properties in the directory from which you want to start the service
3. configure application.properties:
    * server.port - this port the service will be listen
    * h2-console.port - on this port H2 console will be available
    * proxy.path - specify the path for redirecting
    * proxy.url - specify the url for redirecting
4. start the service by executing java -jar mock-proxy.jar
5. configure mock rules using the API - server.Url:serverPort/_admin_config
6. profit!!!

### API for setting up rules
Example of the POST-request body for setting up a rule:

{"patterns": [{"pattern": "<AMOUNT>\\d*[13579]</AMOUNT>"}],"status": 200,"contentType": "text/html;charset=UTF-8","body": "WOW","repeatLimit": 3}

* patterns - all defined patterns must be presented in the request body to trigger this rule
* status - status of the stubbed response
* contentType - contentType of the stubbed response
* body - body of the stubbed response
* repeatLimit - this rule will work for 3 requests, 4th will be forwarded on the proxy.url and counter for this rule will be reset

To configure multiple rules in a single request send POST-request with an array of rules to /_admin_config/group.

To view configured rules use GET-request.

To delete all configured rules use DELETE-request. To delete a specific rule use DELETE-request with the rule ID.
