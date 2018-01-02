# Stalk me

Let your stalkers know where you are!

## Dependencies

* Perl
  * Mojolicious
  * Mandel
  * Digest::MD5
* MongoDB

## Installation

On any system that has already perl, cpanm and MongoDB installed simply execute:

    $ git clone https://github.com/jbob/stalk_me.git
    $ cd stalk_me
    $ cpanm -n --installdeps .
    $ morbo script/stalk_me     # development environment, or
    $ hypnotoad script/stalk_me # production environment

## Android app

The repository also contains an Android app. Thanks to @banana\_theo for fixing
the disaster that my implementation was.
