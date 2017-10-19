package StalkMe;
use Mojo::Base 'Mojolicious';
use StalkMe::Model;


# This method will run once at server start
sub startup {
  my $self = shift;

  my $config = $self->plugin('Config');
  $self->secrets($config->{secret});
  
  $self->plugin('StalkMe::Helpers');

  # Router
  my $r = $self->routes;

  # Normal route to controller
  $r->get('/')->to('StalkMe#welcome');

  $r->get('/share')->to('StalkMe#share');
  $r->any('/api/share')->to('StalkMe#api_share');

  $r->get('/view/*id')->to('StalkMe#view');
  $r->get('/api/view/:mode/*id')->to('StalkMe#api_view');
}

1;
