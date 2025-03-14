@echo off
echo Running CompileEPK...
call CompileEPK.bat
if %errorlevel% neq 0 (
    echo Error running CompileEPK.bat
    exit /b %errorlevel%
)

echo Running CompileWASM...
call CompileWASM.bat
if %errorlevel% neq 0 (
    echo Error running CompileWASM.bat
    exit /b %errorlevel%
)

echo Running CompileEagRuntimeJS...
call CompileEagRuntimeJS.bat
if %errorlevel% neq 0 (
    echo Error running CompileEagRuntimeJS.bat
    exit /b %errorlevel%
)

echo Running MakeWASMClientBundle...
call MakeWASMClientBundle.bat
if %errorlevel% neq 0 (
    echo Error running MakeWASMClientBundle.bat
    exit /b %errorlevel%
)

echo All tasks completed successfully!
pause
