Release Notes
========

# 1.4.0

Release version:
* Use the swagger UI to simplify the administration of mock rules

# 1.3.0

Release version:
* Configure the delay before returning the stub
* Configure the host and port to forward the request to it
* Save/load rules to/from a file

## 1.2.0

* [FEATURE] MP-8: Add a response delay parameter

## 1.1.0

* [FEATURE] MP-5: Implement the ability to save rules

# 1.0.0

MVP version is ready:
* Configure the request body patterns to choose which request should be intercepted
* Configure the status, content type and body of the response to the intercepted request
* Configure the repeat counter (how many times will this rule work before forwarding and resetting the counter)

## 0.1.0-SNAPSHOT

* [FEATURE] MP-4: Deal with mock rules duplication
* [FEATURE] MP-2: Add the controller for mock rules setup
* [FEATURE] MP-1: Add the repository for mock rules
* [FEATURE] MP-0: Add base proxy functional
