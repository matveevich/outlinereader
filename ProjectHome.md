**OutlineReader** is a Java package (jar) made to extract Essbase outline structure (parent-child format) and its properties (alias, formula, uda, attribute...). It is then a good way to document your database outline.

**OutlineReader** is inspired by [Outline Extractor](http://www.appliedolap.com/free-tools/outline-extractor) utility, currently maintained and supported by [AppliedOlap](http://www.appliedolap.com/) and originally developed by **OlapUnderground**. OutlineReader is also widely inspired by the ReadOutline.java class that is available in the Provider Services JAPI samples (please refer to Hyperion\products\Essbase\aps\samples directory).

**OutlineReader** is based on Essbase Java API v.11.1.1.3 (JAPI) and was pre-compiled using JDK 1.5. As IDE I used Netbeans IDE 6.8 and lately 6.9. I also used Apache CLI (Command Line Interface) in order to simplify the development of the command line interface.

### Wish/To do list ###
  * graphical user interface please refer to [jrightlog](http://code.google.com/p/jrightlog/)
  * further export formats (xml, pdf, ...)

### Usage ###
`java -jar OutlineReader.jar -s server -u user -p password -a application -d database [OPTIONS]`

### Options ###
  * -a --essapp => application name (**required**)
  * -d --essdb => database name (**required**)
  * -e --delimiter => delimiter string - default is tab
  * -h --help => display this help
  * -i --indent => indent output
  * -m --dimension => dimension name
  * -p --esspwd => password (**required**)
  * -s --esssvr => Essbase server (**required**)
  * -u --essusr => user login (**required**)
  * -v --essaps => http://ProviderServer:port/aps/JAPI   Provider server URL - if not specified default URL is used http://EssbaseServer:13080/aps/JAPI

### Usage notes ###
Use at your own risk!
You will be solely responsible for any damage to your computer system or loss of data
that may result from the download or the use of the following application.

### Release note ###
  * 1.4.1 - corrected connection bug (-v switch)
  * 1.4    - added solve order column (ASO databases only)
  * 1.3    - added data storage properties
  * 1.2.1 - minor changes in command line usage documentation
  * 1.2    - public release

### From the same author ###
  * [jssauditmerger](http://code.google.com/p/jssauditmerger/) - Merge your spreadsheet audit logs for better analysis (Java version)

  * [ssauditmerger](http://code.google.com/p/ssauditmerger/) - Merge your spreadsheet audit logs for better analysis (PERL version)

  * [essbaserightlog](http://code.google.com/p/essbaserightlog/) - Parse ANY Oracle Hyperion Essbase® server or application logs and generates a full, custom delimited, output for enhanced analysis (database, spreadsheet) (PERL version)

  * [jrightlog](http://code.google.com/p/jrightlog/) - Parse ANY Oracle Hyperion Essbase® server or application logs and generates a full, custom delimited, output for enhanced analysis (database, spreadsheet) (Java version)


FEEL FREE TO GIVE FEEDBACKS AND TO CONTRIBUTE!