javac -cp lib/*.jar -d bin $(find bin/net/loginto -name "*.java")

jar cf LoginTo.jar -C bin . plugin.yml
