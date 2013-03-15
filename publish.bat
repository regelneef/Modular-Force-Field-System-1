::ATOMIC SCIENCE BUILDER
@echo off
echo Promotion Type?
set /p PROMOTION=

set /p MODVERSION=<modversion.txt
set /p CurrentBuild=<buildnumber.txt
set /a BUILD_NUMBER=%CurrentBuild%+1
echo %BUILD_NUMBER% >buildnumber.txt

if %PROMOTION%==* (
	echo %MODVERSION% >recommendedversion.txt
)

set FILE_NAME=MFFS_v%MODVERSION%.%BUILD_NUMBER%.jar
set API_NAME=MFFS_v%MODVERSION%.%BUILD_NUMBER%_api.zip
set BACKUP_NAME=MFFS_v%MODVERSION%.%BUILD_NUMBER%_backup.zip

echo Starting to build %FILE_NAME%

::BUILD
runtime\bin\python\python_mcp runtime\recompile.py %*
runtime\bin\python\python_mcp runtime\reobfuscate.py %*

::ZIP-UP
cd reobf\minecraft\
7z a "..\..\builds\%FILE_NAME%" "\mffs\"
7z a "..\..\builds\%FILE_NAME%" "\buildcraft\"
7z a "..\..\builds\%FILE_NAME%" "\basiccomponents\"
7z a "..\..\builds\%FILE_NAME%" "\com\"
7z a "..\..\builds\%FILE_NAME%" "\dan200\"
7z a "..\..\builds\%FILE_NAME%" "\ic2\"
7z a "..\..\builds\%FILE_NAME%" "\icbm\"
7z a "..\..\builds\%FILE_NAME%" "\org\"
7z a "..\..\builds\%FILE_NAME%" "\railcraft\"
7z a "..\..\builds\%FILE_NAME%" "\thermalexpansion\"
7z a "..\..\builds\%FILE_NAME%" "\universalelectricity\"
cd ..\..\
cd resources\
7z a "..\builds\%FILE_NAME%" "*"
7z a "..\builds\%BACKUP_NAME%" "*" -pthunderdark
cd ..\
cd src\
7z a "..\builds\%BACKUP_NAME%" "*\mffs\" -pthunderdark
7z a "..\builds\%API_NAME%" "*\mffs\api\"
cd ..\

::UPDATE INFO FILE
echo %PROMOTION% %FILE_NAME% %API_NAME%>>info.txt

::GENERATE FTP Script
echo open calclavia.com>ftpscript.txt
echo mffs@calclavia.com>>ftpscript.txt
echo ICBMmod>>ftpscript.txt
echo binary>>ftpscript.txt
echo put "recommendedversion.txt">>ftpscript.txt
echo put "builds\%FILE_NAME%">>ftpscript.txt
echo put "builds\%API_NAME%">>ftpscript.txt
echo put info.txt>>ftpscript.txt
echo quit>>ftpscript.txt
ftp.exe -s:ftpscript.txt
del ftpscript.txt

echo Done building %FILE_NAME% for UE %UE_VERSION%

pause