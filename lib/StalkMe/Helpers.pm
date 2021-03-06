package StalkMe::Helpers;

use Mojo::Base 'Mojolicious::Plugin';
use Mojo::IOLoop;

sub register {
    # Registers the plugin. Is called when it's used
    my ($self, $app) = @_;

    $app->helper(model => sub {
        state $model = StalkMe::Model->connect($app->config->{mongouri});
    });
    $app->helper(tracks => sub {
        $_[0]->app->model->collection('track');
    });
    $app->helper(cleanup => sub {
        if (int(rand(100)) % 10 == 0) {
            my $current_time = time;
            $_[0]->app->tracks->search({
                last_update => { '$lt' => ($current_time - 60*60*4) }
            })->all(sub {
                my ($tracks, $err, $track) = @_;
                warn "Deleting " . $track->id;
                $track->remove;
            });
        }
    });
}

1;
