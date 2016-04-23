#!/bin/sh
# Configuration building script
echo "Building configuration files with JAXB"

javaPath=/Library/Java/JavaVirtualMachines/jdk1.8.0_77.jdk/Contents/Home/bin
"${javaPath}/java" -version

destDir="com/gmo/generated"
srcDestDirRoot="../src/main/java"
srcDestDirGenerated="${srcDestDirRoot}/${destDir}"

#remove the existing generated directory
rm -rf $srcDestDirGenerated

echo "create destination root dir"
mkdir -p $srcDestDirGenerated

# List all xsd files in the current directory
for xsdFile in *.xsd
{
	echo " --> Process xsd file : $xsdFile"
	
	sourceDir="${srcDestDirRoot}"
	${javaPath}/xjc -extension -Xinject-code -d ${sourceDir} $xsdFile
	
}