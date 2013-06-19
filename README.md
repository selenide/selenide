=======
selenide-ru
===========

Selenide blog in Russian


### For developers:
sudo apt-get install ruby1.9.3
sudo gem install jekyll
sudo gem install redcarpet
sudo gem install rake

rake theme:install name="minimal"
rake theme:switch name="minimal"

jekyll serve --watch --trace


### Modification of theme
NB! In order to modify theme files, please
1.  edit files in folder _theme_packages/minimal
2.  rake theme:install name="minimal"
3.  commit all changes


### Tagging selenide
git tag -a selenide-2.2 -m "Created tag selenide-2.2 for the last production version by now"
git tag    (lists all tags)
git push origin --tags


### Publishing javadoc
selenide> gradle clean javadoc
selenide> mv build/docs/javadoc/* ~/Dropbox/projects/selenide-web/javadoc/2.3/
selenide-web> git add javadoc/2.3
