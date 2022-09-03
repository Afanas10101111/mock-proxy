[![Codacy Badge](https://app.codacy.com/project/badge/Grade/49bbb210cd074249ae900c804abd747c)](https://www.codacy.com/gh/Afanas10101111/mock-proxy/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Afanas10101111/mock-proxy&amp;utm_campaign=Badge_Grade)

Mock proxy service
========
This service can be used when you need to simulate a specific response to a post request from a server.

### Usage
1. execute mvn clean package
2. place the generated jar-file and application.properties in the directory from which you want to start the service
3. configure application.properties:
    * server.port - this port the service will be listen
    * h2-console.port - on this port H2 console will be available
    * proxy.path-pattern - specify the request path pattern that trigger the proxy logic (excluded character(s), for example _ in standard (/[^_].*)|(/), must be synchronized with swagger-ui.base-url, otherwise swagger UI will be unavailable)
    * proxy.url - specify the url for forwarding
   * springfox.documentation.swagger-ui.base-url - specify the base path to the swagger UI (for example, if you specify /_swagger full address will be serverUrl:serverPort/_swagger/swagger-ui/)
    * springfox.documentation.swagger.v2.path - specify the path to the v2 docs
4. start the service by executing java -jar mock-proxy.jar
5. configure mock rules using the swagger UI or the API - serverUrl:serverPort/_admin_config
6. profit!!!

### API for setting up rules
Example of the POST-request body for setting up a rule:

{"patterns": [{"pattern": "<AMOUNT>\\d*[13579]</AMOUNT>"}],"status": 200,"contentType": "text/html;charset=UTF-8","body": "WOW","repeatLimit": 3}

Required parameters:
* patterns - all defined patterns must be presented in the request body to trigger this rule
* status - status of the stubbed response
* contentType - contentType of the stubbed response
* body - body of the stubbed response
  
Optional parameters:
* repeatLimit - this rule will work for 3 requests, 4th will be forwarded on the proxy.url and counter for this rule will be reset
* delay - delay before returning the stub is determined in milliseconds
* host - if defined, the request will be forwarded to this host
* port - port for the previously defined host

To configure multiple rules in a single request send POST-request with an array of rules to /_admin_config/group.

To view configured rules use GET-request.

To delete all configured rules use DELETE-request. To delete a specific rule use DELETE-request with the rule ID.

### API for saving/loading rules
* POST /_admin_config/file/save/rules.bak - save the current rules to rules.bak
* GET /_admin_config/file/load/rules.bak - load the rules from rules.bak and merge with current rules
* GET /_admin_config/file - view a list of saved files
* GET /_admin_config/file/rules.bak - view the content of rules.bak
* DELETE /_admin_config/file/rules.bak - delete rules.bak
* DELETE /_admin_config/file - delete all saved files
