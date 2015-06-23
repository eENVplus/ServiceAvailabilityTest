# ServiceAvailabilityTest
by GraphiTech

This tool is composed by a single Java class, which tests the availability of a web service. After verifying the correct HTTP response code 200 it validates the body of the message if it is an XML document. The request is performed each hour for the period of one week. These two parameters at the moment are hard-coded but can be easily changed to provide them as parameters.

To compile this software use the provided `pom.xml` with Maven.

To run the compiled JAR type in the terminal: `java -jar AvailabilityTest.jar`

Optional parameter is `-noxml` to disable XML validation.
Otherwise the tool retrieves the XSD from the schemaLocation attribute in the XML document and tries to validate the document.

Mandatory parameter is `-u "<url>"` to provide the URL endpoint to test.

This tool requires the Apache HttpComponents™ libraries to perform the HTTP requests.