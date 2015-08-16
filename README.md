#japicmp#

japicmp is a tool to compare two versions of a jar archive:

	java -jar japicmp-0.5.3-jar-with-dependencies.jar -n new-version.jar -o old-version.jar

It can also be used as a library:

	JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
	JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
	List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);
    
japicmp is available in the Maven Central Repository:

	<dependency>
		<groupId>com.github.siom79.japicmp</groupId>
		<artifactId>japicmp</artifactId>
		<version>0.5.3</version>
	</dependency>

##Motivation##

Every time you release a new version of a library or a product, you have to tell your clients or customers what
has changed in comparison to the last release. Without the appropriate tooling, this task is tedious and error-prone.
This tool/library helps you to determine the differences between the java class files that are contained in two given
jar archives.

This library does not use the Java Reflection API to compute the differences, as the usage of the Reflection API makes 
it necessary to include all classes the jar archive under investigation depends on are available on the classpath. 
To prevent the inclusion of all dependencies, which can be a lot of work for bigger applications, this library makes 
use of the [javassist](http://www.csg.ci.i.u-tokyo.ac.jp/~chiba/javassist/) library to inspect the class files. 
This way you only have to provide the two jar archives on the command line (and eventually libraries that contain
classes/interfaces you have extended/implemented).

This approach also detects changes in instrumented and generated classes. You can even evaluate changes in class file attributes (like synthetic) or annotations.
The comparison of annotations makes this approach suitable for annotation-based APIs like JAXB, JPA, JAX-RS, etc.

##Features##

* Comparison of two jar archives without the need to add all of their dependencies to the classpath.
* Differences are printed on the command line in a simple diff format.
* Differences can optionally be printed as XML or HTML file.
* Per default private and package protected classes and class members are not compared. If necessary, the access modifier of the classes and class members to be
  compared can be set to public, protected, package or private.
* Per default all classes are tracked. If necessary, certain packages, classes, methods or fields can be excluded or explicitly included.
* All changes between all classes/methods/fields are compared. If necessary, output can be limited to changes that are binary incompatible (as described in the [Java Language Specification](http://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html)).
* All changes between annotations are compared, hence japicmp can be used to track annotation-based APIs like JAXB, JPA, JAX-RS, etc.
* A maven plugin is available that allows you to compare the current artifact version with some older version from the repository.
* The option `--semantic-versioning` tells you which part of the version you have to increment in order to follow [semantic versioning](http://semver.org/).
* If a class is serializable, changes are evaluated regarding the [Java Object Serialization Specification](http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html).
* Per default synthetic classes and class members (e.g. [bridge methods](https://docs.oracle.com/javase/tutorial/java/generics/bridgeMethods.html)) are hidden. They can be listed by using the option `--include-synthetic`.

[melix](https://github.com/melix) has developed a [gradle plugin](https://github.com/melix/japicmp-gradle-plugin) for japicmp.

##Tools##

###Usage CLI tool###

```
SYNOPSIS
        java -jar japicmp.jar [-a <accessModifier>] [(-b | --only-incompatible)]
                [(-e <excludes> | --exclude <excludes>)] [(-h | --help)]
                [--html-file <pathToHtmlOutputFile>]
                [--html-stylesheet <pathToHtmlStylesheet>]
                [(-i <includes> | --include <includes>)] [--ignore-missing-classes]
                [--include-synthetic] [(-m | --only-modified)]
                [(-n <pathToNewVersionJar> | --new <pathToNewVersionJar>)]
                [(-o <pathToOldVersionJar> | --old <pathToOldVersionJar>)]
                [(-s | --semantic-versioning)]
                [(-x <pathToXmlOutputFile> | --xml-file <pathToXmlOutputFile>)]

OPTIONS
        -a <accessModifier>
            Sets the access modifier level (public, package, protected,
            private), which should be used.

        -b, --only-incompatible
            Outputs only classes/methods that are binary incompatible. If not
            given, all classes and methods are printed.

        -e <excludes>, --exclude <excludes>
            Semicolon separated list of elements to exclude in the form
            package.Class#classMember, * can be used as wildcard. Examples:
            mypackage;my.Class;other.Class#method(int,long);foo.Class#field

        -h, --help
            Display help information

        --html-file <pathToHtmlOutputFile>
            Provides the path to the html output file.

        --html-stylesheet <pathToHtmlStylesheet>
            Provides the path to your own stylesheet.

        -i <includes>, --include <includes>
            Semicolon separated list of elements to include in the form
            package.Class#classMember, * can be used as wildcard. Examples:
            mypackage;my.Class;other.Class#method(int,long);foo.Class#field

        --ignore-missing-classes
            Ignores superclasses/interfaces missing on the classpath.

        --include-synthetic
            Include synthetic classes and class members that are hidden per
            default.

        -m, --only-modified
            Outputs only modified classes/methods.

        -n <pathToNewVersionJar>, --new <pathToNewVersionJar>
            Provides the path to the new version of the jar.

        -o <pathToOldVersionJar>, --old <pathToOldVersionJar>
            Provides the path to the old version of the jar.

        -s, --semantic-versioning
            Tells you which part of the version to increment.

        -x <pathToXmlOutputFile>, --xml-file <pathToXmlOutputFile>
            Provides the path to the xml output file.
```

When your library implements interfaces or extends classes from other libraries than the JDK, you will
have to add these to the class path:

	java -cp japicmp-0.5.3-jar-with-dependencies.jar;otherLibrary.jar japicmp.JApiCmp -n new-version.jar -o old-version.jar
    
###Usage maven plugin###

The maven plugin can be included in the pom.xml file of your artifact in the following way (requires maven >= 3.0.3):

    <build>
        <plugins>
            <plugin>
                <groupId>com.github.siom79.japicmp</groupId>
                <artifactId>japicmp-maven-plugin</artifactId>
                <version>0.5.3</version>
                <configuration>
                    <oldVersion>
                        <dependency>
                            <groupId>japicmp</groupId>
                            <artifactId>japicmp-test-v1</artifactId>
                            <version>0.5.3</version>
                        </dependency>
                    </oldVersion>
                    <newVersion>
                        <file>
                            <path>${project.build.directory}/${project.artifactId}-${project.version}.jar</path>
                        </file>
                    </newVersion>
                    <parameter>
                        <onlyModified>true</onlyModified>
                        <includes>
                        	<include>package.to.include</include>
                        	<include>package.ClassToInclude</include>
                        	<include>package.Class#methodToInclude(long,int)</include>
                        	<include>package.Class#fieldToInclude</include>
                        </includes>
                        <excludes>
							<exclude>package.to.exclude</exclude>
							<exclude>package.ClassToExclude</exclude>
							<exclude>package.Class#methodToExclude(long,int)</exclude>
							<exclude>package.Class#fieldToExclude</exclude>
						</excludes>
                        <accessModifier>public</accessModifier>
                        <breakBuildOnModifications>false</breakBuildOnModifications>
                        <breakBuildOnBinaryIncompatibleModifications>false</breakBuildOnBinaryIncompatibleModifications>
                        <onlyBinaryIncompatible>false</onlyBinaryIncompatible>
                        <includeSynthetic>false</includeSynthetic>
                        <ignoreMissingClasses>false</ignoreMissingClasses>
                        <skipPomModules>true</skipPomModules>
                    </parameter>
					<dependencies>
						<dependency>
							<groupId>org.apache.commons</groupId>
							<artifactId>commons-math3</artifactId>
							<version>3.4</version>
						</dependency>
					</dependencies>
					<skip>false</skip>
                </configuration>
                <executions>
                    <execution>
                        <phase>verify</phase>
                        <goals>
                            <goal>cmp</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    
The elements &lt;oldVersion&gt; and &lt;newVersion&gt; elements let you specify which version you want to compare. Both elements
 support either a &lt;dependency&gt; or a &lt;file&gt; element. Through the &lt;parameter&gt; element you can provide the following options:
  
* onlyModified: Outputs only modified classes/methods. If not set to true, all classes and methods are printed.
* includes: List of package, classes, methods and field that should be included. The syntax is similar to the one use for javadoc references.
* excludes: List of package, classes, methods and field that should be excluded. The syntax is similar to the one use for javadoc references.
* accessModifier: Sets the access modifier level (public, package, protected, private).
* breakBuildOnModifications: When set to true, the build breaks in case a modification has been detected.
* breakBuildOnBinaryIncompatibleModifications: When set to true, the build breaks in case a binary incompatible modification has been detected.
* onlyBinaryIncompatible: When set to true, only binary incompatible changes are reported.
* includeSynthetic: When set to true, changes for synthetic classes and class members are tracked.
* ignoreMissingClasses: When set to true, superclasses and interfaces that cannot be resolved are ignored. Pleases note that in this case the results for the affected classes may not be accurate.
* skipPomModules: Setting this parameter to false (default: true) will not skip execution in modules with packaging type pom.
* skip: Setting this parameter to true will skip execution of the plugin.

If your library implements interfaces or extends classes from other libraries than the JDK, you can add these dependencies by using the
&lt;dependencies&gt; element:

```
<dependencies>
	<dependency>
		<groupId>org.apache.commons</groupId>
		<artifactId>commons-math3</artifactId>
		<version>3.4</version>
	</dependency>
</dependencies>
```

Dependencies declared in the enclosing pom.xml and its parents are added automatically. The dependencies declared explicitly for this plugin
are appended to the classpath before the ones from the enclosing pom.xml.

The maven plugin produces the two files `japicmp.diff` and `japicmp.xml` within the directory `${project.build.directory}/japicmp`
of your artifact. Alternatively it can be used inside the `<reporting/>` tag in order to be invoked by the
[maven-site-plugin](https://maven.apache.org/plugins/maven-site-plugin/) and therewith to be integrated into the site report:

```
<reporting>
	<plugins>
		<plugin>
			<groupId>com.github.siom79.japicmp</groupId>
			<artifactId>japicmp-maven-plugin</artifactId>
			<version>0.5.3</version>
			<reportSets>
				<reportSet>
					<reports>
						<report>cmp-report</report>
					</reports>
				</reportSet>
			</reportSets>
			<configuration>
				<!-- see above -->
			</configuration>
		</plugin>
	</plugins>
</reporting>
```
	
##Examples##

###Comparing two versions of the guava library###

In the following you see the beginning of the differences between the versions 16.0 and 17.0 of Google's guava library. The differences between the two Java APIs are also printed on the command line for a quick overview. Please note that binary incompatible changes are flagged with an exclamation mark. 

	***! MODIFIED CLASS: PUBLIC FINAL com.google.common.base.Stopwatch
		***! MODIFIED CONSTRUCTOR: PACKAGE_PROTECTED (<- PUBLIC) Stopwatch()
			===  UNCHANGED ANNOTATION: java.lang.Deprecated
		***! MODIFIED CONSTRUCTOR: PACKAGE_PROTECTED (<- PUBLIC) Stopwatch(com.google.common.base.Ticker)
			===  UNCHANGED ANNOTATION: java.lang.Deprecated
	***! MODIFIED INTERFACE: PUBLIC ABSTRACT com.google.common.util.concurrent.Service
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.Service$State startAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.Service$State stopAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.ListenableFuture start()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) ABSTRACT(-) com.google.common.util.concurrent.ListenableFuture stop()
			---  REMOVED ANNOTATION: java.lang.Deprecated
	***  MODIFIED CLASS: PUBLIC FINAL com.google.common.net.HttpHeaders
		+++  NEW FIELD: PUBLIC(+) STATIC(+) FINAL(+) java.lang.String FOLLOW_ONLY_WHEN_PRERENDER_SHOWN
	***! MODIFIED CLASS: PUBLIC ABSTRACT com.google.common.util.concurrent.AbstractScheduledService
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.ListenableFuture start()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.Service$State startAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.Service$State stopAndWait()
			---  REMOVED ANNOTATION: java.lang.Deprecated
		---! REMOVED METHOD: PUBLIC(-) STATIC(-) FINAL(-) com.google.common.util.concurrent.ListenableFuture stop()
			---  REMOVED ANNOTATION: java.lang.Deprecated
	...

Optionally japicmp can also create an HTML report. An example for such a report can be found [here](http://htmlpreview.github.io/?https://github.com/siom79/japicmp/blob/master/doc/japicmp_guava.html):

<img src="https://raw.github.com/siom79/japicmp/master/doc/japicmp_guava.png" alt="HTML Report"></img>

You can also let japicmp create an XML report like the following one:

	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	<japicmp newJar="/home/siom79/dev/guava-17.0.jar" oldJar="/home/siom79/dev/guava-16.0.jar">
		<classes>
			<class binaryCompatible="false" changeStatus="MODIFIED" fullyQualifiedName="com.google.common.base.Stopwatch" type="CLASS">
				<annotations/>
				<attributes>
					<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
				</attributes>
				<constructors>
					<constructor binaryCompatible="false" changeStatus="MODIFIED" name="Stopwatch">
						<annotations>
							<annotation fullyQualifiedName="java.lang.Deprecated">
								<elements/>
							</annotation>
						</annotations>
						<attributes>
							<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
						</attributes>
						<modifiers>
							<modifier changeStatus="UNCHANGED" newValue="NON_FINAL" oldValue="NON_FINAL"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
							<modifier changeStatus="MODIFIED" newValue="PACKAGE_PROTECTED" oldValue="PUBLIC"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_ABSTRACT" oldValue="NON_ABSTRACT"/>
						</modifiers>
						<parameters/>
					</constructor>
					<constructor binaryCompatible="false" changeStatus="MODIFIED" name="Stopwatch">
						<annotations>
							<annotation fullyQualifiedName="java.lang.Deprecated">
								<elements/>
							</annotation>
						</annotations>
						<attributes>
							<attribute changeStatus="UNCHANGED" newValue="NON_SYNTHETIC" oldValue="NON_SYNTHETIC"/>
						</attributes>
						<modifiers>
							<modifier changeStatus="UNCHANGED" newValue="NON_FINAL" oldValue="NON_FINAL"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
							<modifier changeStatus="MODIFIED" newValue="PACKAGE_PROTECTED" oldValue="PUBLIC"/>
							<modifier changeStatus="UNCHANGED" newValue="NON_ABSTRACT" oldValue="NON_ABSTRACT"/>
						</modifiers>
						<parameters>
							<parameter type="com.google.common.base.Ticker"/>
						</parameters>
					</constructor>
				</constructors>
				<fields/>
				<interfaces/>
				<methods/>
				<modifiers>
					<modifier changeStatus="UNCHANGED" newValue="FINAL" oldValue="FINAL"/>
					<modifier changeStatus="UNCHANGED" newValue="NON_STATIC" oldValue="NON_STATIC"/>
					<modifier changeStatus="UNCHANGED" newValue="PUBLIC" oldValue="PUBLIC"/>
					<modifier changeStatus="UNCHANGED" newValue="NON_ABSTRACT" oldValue="NON_ABSTRACT"/>
				</modifiers>
				<superclass binaryCompatible="true" changeStatus="UNCHANGED" superclassNew="n.a." superclassOld="n.a."/>
			</class>
		...
    
###Tracking changes of an XML document marshalled with JAXB###

The following output shows the changes of a model class with some JAXB bindings:

	***  MODIFIED CLASS: PUBLIC japicmp.test.jaxb.SimpleDocument
		***  MODIFIED METHOD: PUBLIC java.lang.String getTitle()
			---  REMOVED ANNOTATION: javax.xml.bind.annotation.XmlAttribute
			+++  NEW ANNOTATION: javax.xml.bind.annotation.XmlElement
		***  MODIFIED METHOD: PUBLIC java.lang.String getAuthor()
			---  REMOVED ANNOTATION: javax.xml.bind.annotation.XmlAttribute
			+++  NEW ANNOTATION: javax.xml.bind.annotation.XmlElement
		***  MODIFIED ANNOTATION: javax.xml.bind.annotation.XmlRootElement
			***  MODIFIED ELEMENT: name=document (<- simpleDocument)
			
As can bee seen from the output above, the XML attributes title and author have changed to an XML element. The name of the XML root element has also changed from "simpleDocument" to "document".

##Downloads##

You can download the latest version from the [release page](https://github.com/siom79/japicmp/releases) or directly from the [maven central repository](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22japicmp%22).

##Development##

* ![Build Status](https://travis-ci.org/siom79/japicmp.svg?branch=development)

###Reports###

Use the maven site plugin to generate the following reports:
 * findbugs
 * checkstyle
 * japicmp
 * cobertura test coverage

###Release###

This is the release procedure:
* Increment version in README.md
* Run release build (substitute passphrase with your GPG password):
```
mvn release:clean release:prepare -DautoVersionSubmodules=true -Dgpg.passphrase=passphrase
mvn release:perform -Dgpg.passphrase=passphrase
```
* Login to [Sonatype's Nexus repository](https://oss.sonatype.org/)
	* Download released artifact from staging repository.
	* Close and release staging repository if manual tests are successful.

##Contributions

Pull requests are welcome, but please follow these rules:

* Use `Java Conventions` as provided by your IDE for formatting with the following settings:
    * Indentation with tab
    * Newline: LF
    * Line length: 180
* Provide a unit test for every change
* Name classes/methods/fields expressively

##Related work##

The following projects have related goals:

* [Java API Compliance Checker](http://ispras.linuxbase.org/index.php/Java_API_Compliance_Checker): A Perl script that uses javap to compare two jar archives. This approach cannot compare annotations and you need to have Perl installed.
* [Clirr](http://clirr.sourceforge.net/): A tool written in Java that compares two libraries for binary compatibility. Tracking of API changes is implemented only partially, tracking of annotations is not supported.
* [JDiff](http://javadiff.sourceforge.net/): A Javadoc doclet that generates an HTML report of all API changes. The source code for both versions has to be available, the differences are not distinguished between binary incompatible or not. Comparison of annotations is not supported.
