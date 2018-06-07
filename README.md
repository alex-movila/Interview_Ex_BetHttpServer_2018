Purpose
The goal of this home assignment is to have a common code base on which
we can have a technical discussion in the coming interview. Its purpose is to
provide the interviewer with an understanding of the candidate’s coding
style and skills but should not take more than a day to implement.
That being said, the ambition should not be to implement a solution which is
robust 100% in all special cases. What the candidate needs to focus on
mostly is that the required functionality is error free when used correctly.
How everything else is handled is up to the candidate.
We will pay attention to code structure, threading and understanding of the
problem. Also, there will be a discussion around the design of the solution
and the choice of data structures.
Description
Write a HTTP-based backend in Java which stores and provides the stakes
for different customers and bet offers, with the capability to return the
highest stakes on a bet offer. Also, the service needs to have a simple
session management system as well, but with no authentication.
The implementation needs to be delivered as an archive containing the
following:
- The code in the src folder
- A compiled version in an executable jar file in the root folder
- An optional readme file with thoughts around the chosen solution
2
Nonfunctional Requirements
- There is no persistence and the application needs to be able to run
indefinitely without crashing
- The service needs to be able to handle a lot of simultaneous requests,
so bear in mind the memory and CPU resources at your disposal
- The data needs to stay consistent
- Do not use any external frameworks, except maybe for testing. For
HTTP the server it’s up to you on how you wish to implement it. If you
wish to use an already implemented one, you can use
com.sun.net.httpserver.HttpServer.
Functional Requirements
- <value> means a call parameter value or a return value
- All calls need to result in a 200 HTTP status code, unless something
goes wrong, when anything but 200 must be returned
- Number parameters and returned values are sent in decimal ASCII as
expected
- Customers and bet offers are created on the spot, the first time they
are referenced
Create a session
- Create a “reasonably” unique session key as a string (letters and digits
only) which is valid for use with other functions for 10 minutes
Request: GET /<customerid>/session
Response: <sessionkey>
<customerid>: 31 bit unsigned integer number
<sessionkey>: a string identifying the session (valid for 10
minutes)
Example: http://localhost:8001/1234/session --> QWER12A
3
Post a customer’s stake on a bet offer
- Only requests with valid session keys will be processed
- The response is empty
- This method can be called several times per customer and bet offer
Request: POST /<betofferid>/stake?sessionkey=<sessionkey>
Request body: <stake>
Response: (empty)
<stake>: 31 bit unsigned integer number
<betofferid>: 31 bit unsigned integer number
<sessionkey>: a session key retrieved from the session
function
Example: POST
http://localhost:8001/888/stake?sessionkey=QWER12A with post
body: 4500
Get a high stakes list for a bet offer
- Retrieve the high stakes for a specific bet offer.
- The result is a list sorted by descending order. The values are
separated using commas.
- We are interested in the top 20 stakes on the bet offer and only the
highest stake per bet offer counts for a specific customer: a customer
id can only appear at most once in the returned list.
- If a customer hasn’t submitted any stakes on that bet offer, then no
stake is present for the customer.
- If there are no submitted stakes for a bet offer the response for calling
this method will be an empty string.
Request: GET /<betofferid>/highstakes
Response: CSV of <customerid>=<stake>
<betofferid>: 31 bit unsigned integer number
<stake>: 31 bit unsigned integer number
<customerid>: 31 bit unsigned integer number
4
Example: http://localhost:8001/888/highstakes ->
1234=4500,57453=1337


1) implement HTTP server with Executors.newCachedThreadPool
(now the fashion is with nonblocking IO but is complicated to implement)
2) use the new HTTPClient from jdk 9 which is in incubating phase
3) project needs jdk 10
4) use 32 bit unsigned integer number ( Integer.parseUnsignedInt, Integer.toUnsignedString)
5) import modules
   requires jdk.httpserver;
   requires jdk.incubator.httpclient;
6) use Executors.newSingleThreadScheduledExecutor for session exipration cleanup thread
7) use ReentrantReadWriteLock on each customer stake list
8) use TreeSet for topCustomerListWithHighestStakes to keep list always sorted with log n modifications
