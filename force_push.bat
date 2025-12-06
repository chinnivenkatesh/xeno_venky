@echo off
REM Using double percent for URL encoding in batch file
set "REPO_URL=https://chinnivenkatesh:Venky28%%4028@github.com/chinnivenkatesh/xeno_project.git"
git push "%REPO_URL%" main
