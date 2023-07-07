# vaadin-form-generator

This is a proof of concept for generating Vaadin form layouts from a Java beans.

You basically give it the source code of a Java bean, the application parses that and then generates a form with fields for every property of the bean. Most of the application uses "regular" / deterministic code: parsing the bean, determining field types, generating code. That process is enhanced with GPT by:
- Generating field labels in the specified language
- Bringing fields in a sensible order for filling out the form
- Grouping related fields and generating a group header
- Suggesting sensible field types for string properties (text field, text area, email field, ...)
- Suggesting how many characters might be entered in a field, which determines how many columns the field spans in the layout

**Screenshot:**

![Screenshot](/docs/screenshot.png?raw=true "Screenshot")

## Running the application

The project is a standard Maven project, using Spring Boot, Vaadin Hilla, and Lit for the frontend.
To run it from the command line, type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open http://localhost:8080 in your browser.

**You need an OpenAI API token to run this app.**
It needs to be defined as the `openai.token` Spring property.
For example, using Maven:
```
mvn -Dspring-boot.run.arguments="--openai.token=..."
```

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/myapp-1.0-SNAPSHOT.jar` (NOTE, replace
`myapp-1.0-SNAPSHOT.jar` with the name of your jar).
