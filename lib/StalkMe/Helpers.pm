package StalkMe::Helpers;

use Mojo::Base 'Mojolicious::Plugin';
use Mojo::IOLoop;

sub register {
    # Registers the plugin. Is called when it's used
    my ($self, $app) = @_;

    $app->helper(model => sub {
        state $model = StalkMe::Model->connect($app->config->{mongouri});
    });
    $app->helper(routes => sub {
        $_[0]->app->model->collection('route');
    });
}

1;
