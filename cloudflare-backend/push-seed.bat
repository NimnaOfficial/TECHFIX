@echo off
echo ===================================================
echo   TECHFIX - Cloudflare D1 Database Seed Script
echo ===================================================
echo.
echo WARNING: You MUST be logged in to Cloudflare!
echo We will now run "npx wrangler login". Please authorize it in your browser.
echo.
call npx wrangler login
echo.
echo Logged in! Now pushing database schema and seed data to LIVE Cloudflare Database...
echo.
call npx wrangler d1 execute techfix-db --remote --file=./schema.sql
call npx wrangler d1 execute techfix-db --remote --file=./seed.sql
echo.
echo ===================================================
echo Done! Please READ any error messages above! 
echo If it says 'success', restart your Android App and try adding a device again!
echo ===================================================
pause
