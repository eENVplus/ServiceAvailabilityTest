package it.graphitech.eenvplus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class AvailabilityTest {

	static boolean isDocumentValid = false;
	static int totalTests = 170;

	public static void main(String[] args) {
		String input = null;
		boolean result = false;
		boolean validateXML = true;

		if (args.length > 0) {
			if (args[0].equals("-noxml") && args[1].equals("-u")) {
				validateXML = false;
				input = new String(args[2]);
			} else {
				if (args[0].equals("-u")) {
					input = new String(args[1]);
				} else {
					printUsage();
				}
			}

			if (input != null) {
				File file = new File("eENVplus-AvailabilityTest.log");
				SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");

				for (int i = 0; i <= totalTests; i++) {
					long start = System.currentTimeMillis();
					try {
						FileWriter out = new FileWriter(file, true);

						result = makeURLRequest(input, validateXML);

						if (result) {
							out.write("[" + ft.format(System.currentTimeMillis()) + "] Test passed!\n");
							// System.out.print("[" + ft.format(System.currentTimeMillis()) + "] Test passed!\n");
						} else {
							out.write("[" + ft.format(System.currentTimeMillis()) + "] Test failed!\n");
							// System.out.print("[" + ft.format(System.currentTimeMillis()) + "] Test failed!\n");
						}

						System.out.println("Test: " + i + " of " + totalTests);
						out.close();
					} catch (IOException e) {
					}
					long end = System.currentTimeMillis();
					try {
						Thread.sleep(3600000 - (start - end));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			printUsage();
		}
	}

	static void printUsage() {
		System.out.println("USAGE: java -jar AvailabilityTest.jar [-noxml] -f \"file_path\" or -u \"URL\"");
	}

	static boolean makeURLRequest(String url, boolean validateXML) {

		HttpGet httpGet = new HttpGet(url);
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				try {
					HttpEntity httpEntity = httpResponse.getEntity();
					if (httpEntity != null) {
						isDocumentValid = true;

						if (validateXML) {
							byte[] inputHttpBytes = EntityUtils.toByteArray(httpEntity);

							FileOutputStream out = new FileOutputStream("response.xml");
							out.write(inputHttpBytes);
							out.close();

							try {
								SAXParserFactory factory = SAXParserFactory.newInstance();
								factory.setValidating(true);
								factory.setNamespaceAware(true);

								SAXParser parser = factory.newSAXParser();
								parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");

								XMLReader reader = parser.getXMLReader();
								reader.setErrorHandler(new SimpleErrorHandler());
								reader.parse(new InputSource("response.xml"));

							} catch (SAXException | ParserConfigurationException e) {
								System.out.print("[error] document is invalid!\n");
								isDocumentValid = false;
							}
						}
					} else {
						System.out.print("[error] httpEntity == null\n");
						isDocumentValid = false;
					}

					EntityUtils.consume(httpEntity);
				} finally {
					httpResponse.close();
				}

			} else {
				System.out.print("[error] Download failed, status code: " + httpResponse.getStatusLine().getStatusCode() + "\n");
				isDocumentValid = false;
			}
		} catch (IOException e) {
			System.out.print("[error] IOException\n");
			isDocumentValid = false;
		}

		return isDocumentValid;
	}

	// static boolean makeFileRequest(String file) {
	//
	// isDocumentValid = true;
	//
	// try {
	// SAXParserFactory factory = SAXParserFactory.newInstance();
	// factory.setValidating(true);
	// factory.setNamespaceAware(true);
	//
	// SAXParser parser = factory.newSAXParser();
	// parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
	//
	// XMLReader reader = parser.getXMLReader();
	// reader.setErrorHandler(new SimpleErrorHandler());
	// reader.parse(new InputSource(file));
	//
	// } catch (IOException e) {
	// System.out.print("[error] file not found!\n");
	// isDocumentValid = false;
	// } catch (SAXException | ParserConfigurationException e) {
	// System.out.print("[error] document is invalid!\n");
	// isDocumentValid = false;
	// }
	//
	// return isDocumentValid;
	// }

	static class SimpleErrorHandler implements ErrorHandler {
		public void warning(SAXParseException e) throws SAXException {
			isDocumentValid = false;
			System.out.println(e.getMessage());
		}

		public void error(SAXParseException e) throws SAXException {
			isDocumentValid = false;
			System.out.println(e.getMessage());
		}

		public void fatalError(SAXParseException e) throws SAXException {
			isDocumentValid = false;
			System.out.println(e.getMessage());
		}
	}
}
