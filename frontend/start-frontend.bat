@echo off
echo Starting Frontend Server...
echo.
echo Server manzili: http://localhost:5500
echo.
cd /d "%~dp0"
echo Python bilan server ishga tushirilmoqda...
python -m http.server 5500
