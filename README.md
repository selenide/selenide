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