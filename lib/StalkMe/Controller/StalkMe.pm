package StalkMe::Controller::StalkMe;
use Mojo::Base 'Mojolicious::Controller';

use StalkMe::Model::Route;

use Digest::MD5;

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
        $self->routes->search({ id => $id })->single(sub {
            my ($routes, $err, $route) = @_;
            if (not $route) {
               $route = $self->routes->create({ id => $id });
            }
            $route->add_point({ lat => $lat, lng => $lng });
            $route->save;
            return $self->render(text => 'thx');
        });
    }

    # Intermezzo: Maybe it is time for a cleanup?
    if (int(rand(999_999_999)) % 100 == 0) {
        # Yes it is
        my $current_time = time;
        $self->routes->search({
            last_update => { '$lt' => ($current_time - 60*60*4) }
        })->all(sub {
            my ($routes, $err, $route) = @_;
            warn "Deleting " . $route->id;
            $route->remove;
        });
    }

    return $self->render_later;

}

sub view {
    my $self = shift;
    my $id = $self->stash->{id};
    if (not $id) {
        $self->render(text => 'nope');
    }
    return $self->render(id => $id);
}

sub api_view {
    my $self = shift;
    my $id = $self->stash->{id};
    my $mode = $self->stash->{mode};
    $self->routes->search({ id => $id })->single(sub {
        my ($routes, $err, $route) = @_;
        return $self->render(jsbon => {err => $err})           if $err;
        return $self->render(json => {err => 'nope'})          if not $route;
        return $self->render(json => $route->points)           if $mode eq 'full';
        return $self->render(json => [$route->get_last_point]) if $mode eq 'last';
        return $self->render(json => {err => 'Unkown method'});
    });
    $self->render_later;
}

1;
