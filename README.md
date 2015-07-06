# ServiceAvailabilityTest
by GraphiTech

This tool is composed by a single Java class, which tests the availability of a web service. After verifying the correct HTTP response code 200 it validates the body of the message if it is an XML document. The request is performed each hour for the period of one week. These two parameters at the moment are hard-coded but can be easily changed to provide them as parameters.

To compile this software use Apache Maven, inside project directory type: `mvn`

To run the compiled JAR go inside `target` folder and type: `java -jar ServiceAvailabilityTest-<version>.jar`

Optional parameter is `-noxml` to disable XML validation.
Otherwise the tool retrieves the XSD from the schemaLocation attribute in the XML document and tries to validate the document.

Mandatory parameter is `-u "<url>"` to provide the URL endpoint to test.

This tool uses the [Apache HttpComponents™](https://hc.apache.org/) libraries to perform the HTTP requests.
