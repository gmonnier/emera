#!/bin/sh
# Configuration building script
echo "Building configuration files with JAXB"

javaPath=/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/bin
"${javaPath}/java" -version

destDir="generated"

#remove the existing generated directory
rm -rf $destDir

echo "create destination root dir"
mkdir -p $destDir
mkdir -p $destDir/jar

# List all xsd files in the current directory
for xsdFile in *.xsd
{
	echo " --> Process xsd file : $xsdFile"
	xsdBaseName=${xsdFile%.xsd}
	
	sourceDir="${destDir}/sources/${xsdBaseName}"
	mkdir -p ${sourceDir}
	${javaPath}/xjc -extension -Xinject-code -d ${sourceDir} $xsdFile
	
	compileDir="${destDir}/compiled/${xsdBaseName}"
	mkdir -p "${compileDir}"
	CLASSPATH="${sourceDir}"
	
	# Compile all java sources previously listed in sourceList file
	echo "compiling sources CLASSPATH=${CLASSPATH}"
	for file in $(find ${sourceDir} -name '*.java')
	{
		echo "compiling $file"
		${javaPath}/javac -classpath "${CLASSPATH}" -d "${compileDir}" "${file}"  
	}

	echo "Generate ${xsdBaseName} jar file"
	# Compile all java sources previously listed in sourceList file
	${javaPath}/jar cvf $destDir/jar/${xsdBaseName}.jar ${compileDir}/*
}