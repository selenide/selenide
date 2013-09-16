=======
selenide-ru
===========

Selenide blog in Russian


### For developers:
sudo apt-get install ruby1.9.3
sudo gem install jekyll
sudo gem install redcarpet
sudo gem install rake

jekyll serve --watch --trace


### Tagging selenide
git tag -a selenide-2.2 -m "Created tag selenide-2.2 for the last production version by now"
git tag    (lists all tags)
git push origin --tags


### Publishing javadoc
selenide> gradle clean javadoc
selenide> mv build/docs/javadoc/* ~/Dropbox/projects/selenide-web/javadoc/2.5/
selenide-web> git add javadoc/2.5
