package StalkMe::Controller::StalkMe;
use Mojo::Base 'Mojolicious::Controller';

use StalkMe::Model::Route;

use Digest::MD5;

my $routes = {};

sub welcome {
  my $self = shift;
  $self->reply->static('index.html');
}

sub share {
    my $self = shift;
    if (not $self->session('id')) {
        $self->session(id =>  Digest::MD5::md5_base64( rand ));
    }
}

sub api_share {
    my $self = shift;
    my $id = $self->session('id');
    my $lat = $self->param('lat');
    my $lng = $self->param('lng');
    if ($lat and $lng) {
        if (not $routes->{$id}) {
            $routes->{$id} = new StalkMe::Model::Route;
        }
        $routes->{$id}->add_point({lat => $lat, lng => $lng});
    }

    # Intermezzo: Maybe it is time for a cleanup?
    if (int(rand(999_999_999)) % 100 == 0) {
        # Yes it is
        my $current_time = time;
        for my $route_id ( keys %$routes ) {
            if ($routes->{$route_id}->last_update < ($current_time - 60*60*4) ) {
                # Last update is more than 4 hours in the past, throw away
                delete $routes->{$route_id};
            }
        }
    }

    return $self->render(text => 'thx');
}

sub view {
    my $self = shift;
    my $id = $self->stash->{id} // $self->param('id');
    if (not $id) {
        $self->render(text => 'nope');
    }
    return $self->render(id => $id);
}

sub api_view {
    my $self = shift;
    my $id = $self->stash->{id};
    my $mode = $self->stash->{mode};
    if (not $id or not $routes->{$id}) {
        return $self->render(json => {err => 'nope'});
    }
    return $self->render(json => $routes->{$id}->points) if $mode eq 'full';
    return $self->render(json => [$routes->{$id}->get_last_point]) if $mode eq 'last';
    return $self->render(json => {err => 'Unknown method'});
}

1;
