# Stalk me

Let your stalkers know where you are!

## Dependencies

* Perl
  * Mojolicious
  * Mandel
  * Digest::MD5
* MongoDB

## Installation

On any system that has already perl and cpanm installed simply execute:

    $ git clone https://github.com/jbob/stalk_me.git
    $ cd stalk_me
    $ cpanm -n --installdeps .
    $ morbo script/stalk_me     # development enviroment, or
    $ hypnotoad script/stalk_me # production environment
