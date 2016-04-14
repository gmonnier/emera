Write-Host "Building configuration files with JAXB"

$javaPath = "${env:programfiles}\Java\jdk1.8.0_25\bin"
$javaPath
& "${javaPath}\java.exe" -version

$destDir = "generated"
$srcDestDir = "../src/main/java/com/gmo/generated/configurations"

Remove-Item -Recurse -Force $destDir
Remove-Item -Recurse -Force $srcDestDir

New-Item -ItemType directory -Path $srcDestDir
New-Item -ItemType directory -Path $destDir
New-Item -ItemType directory -Path $destDir/jar

# List all xsd files in the current directory
$xsdList = Get-ChildItem -name -filter *.xsd
Write-Host “xsd founds : ${xsdList}”
foreach ($xsdFile in $xsdList)
{
	Write-Host " --> Process xsd file : $xsdFile"
	$xsdBaseName = ${xsdFile} -replace '.xsd',''
	$sourceDir = "${srcDestDir}\${xsdBaseName}"
	New-Item -ItemType directory -Path "${sourceDir}"
	& "${javaPath}\xjc.exe" -extension -Xinject-code -d ${sourceDir} $xsdFile
	
	$javafiles = Get-ChildItem "${sourceDir}" -name -recurse -filter *.java 
	$compileDir = "${destDir}/compiled/${xsdBaseName}"
	New-Item -ItemType directory -Path "${compileDir}"
	$CLASSPATH = "${sourceDir}"
	
	# Compile all java sources previously listed in sourceList file
	Write-Host “compiling sources classpath=${CLASSPATH}”
	foreach ($file in $javafiles)
	{
		Write-Host “compiling $file”
		$newName = $file -replace '\\','/'
		& "${javaPath}\javac" -classpath "${CLASSPATH}" -d "${compileDir}" "${sourceDir}/${newName}"  
	}

	Write-Host "Generate ${xsdBaseName} jar file"
	# Compile all java sources previously listed in sourceList file
	& "${javaPath}\jar" cvf $destDir/jar/${xsdBaseName}.jar ${compileDir}/*
}

pause