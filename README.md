Command line instructions


Git global setup

git config --global user.name "Matteo Virgilio"
git config --global user.email "mettiu_sicilia@hotmail.com"

Create a new repository

git clone git@gitlab.com:mettiu/rest-verigraph.git
cd rest-verigraph
touch README.md
git add README.md
git commit -m "add README"
git push -u origin master

Existing folder or Git repository

cd existing_folder
git init
git remote add origin git@gitlab.com:mettiu/rest-verigraph.git
git add .
git commit
git push -u origin master