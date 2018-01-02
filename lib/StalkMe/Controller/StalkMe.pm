package StalkMe::Controller::StalkMe;
use Mojo::Base 'Mojolicious::Controller';

use StalkMe::Model::Track;

use Digest::MD5;

sub welcome {
  my $self = shift;
  $self->reply->static('index.html');
}

sub download {
    my $self = shift;
    $self->reply->static('StalkMe.apk');
}

sub share {
    my $self = shift;
    if (not $self->session('id')) {
        $self->session(id =>  Digest::MD5::md5_base64( rand ));
    }
}

sub api_share {
    my $self = shift;
    if ($self->req->method eq 'POST') {
        my $id = $self->session('id');
        my $lat = $self->param('lat');
        my $lng = $self->param('lng');
        if ($lat and $lng) {
            $self->tracks->search({ id => $id })->single(sub {
                my ($tracks, $err, $track) = @_;
                if (not $track) {
                   $track = $self->tracks->create({ id => $id });
                }
                $track->add_point({ lat => $lat, lng => $lng });
                $track->save;
                return $self->render(json => {msg =>  'thx'});
            });
        }
        $self->cleanup;
        return $self->render_later;
    } elsif ($self->req->method eq 'GET') {
        if (not $self->session('id')) {
            $self->session(id => Digest::MD5::md5_base64( rand ));
        }
        return $self->render(json => {
                                        id => $self->session('id'),
                                        url => $self->url_for('/view/'.$self->session('id'))->to_abs->scheme('https')
                                     });
    }
    return $self->render(json => { err => 'wtf?!' });
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
    $self->tracks->search({ id => $id })->single(sub {
        my ($tracks, $err, $track) = @_;
        return $self->render(jsbon => {err => $err})           if $err;
        return $self->render(json => {err => 'nope'})          if not $track;
        return $self->render(json => $track->points)           if $mode eq 'full';
        return $self->render(json => [$track->get_last_point]) if $mode eq 'last';
        return $self->render(json => {err => 'Unkown method'});
    });
    $self->render_later;
}

1;
