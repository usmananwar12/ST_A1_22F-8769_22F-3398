@echo off
echo Setting up database...
echo Please enter your MariaDB root password when prompted.
"E:\Program Files\MariaDB 12.2\bin\mysql.exe" -u root -p < "e:\sem 8\Text-Editor\resource\Database\EditorDBQuery.sql"
if %errorlevel% neq 0 (
    echo Failed to import database.
) else (
    echo Database imported successfully!
)
pause
