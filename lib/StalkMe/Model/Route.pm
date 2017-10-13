package StalkMe::Model::Route;

use Moo;
use namespace::clean;
use Scalar::Util qw(looks_like_number);

has last_update => (
    is      => 'rwp',
    isa     => sub { die "$_[0] is not a valid timestamp!" unless looks_like_number $_[0] },
    default => sub { time }
);

has points => (
    is      => 'rwp',
    isa     => sub { die "Points not an array reference!" unless ref $_[0] eq 'ARRAY' },
    default => sub { [] }
);

sub add_point {
    my $self = shift;
    my $coords = shift;
    my $last = $self->get_last_point;
    # Deduplication!
    if (!$last or $coords->{lat} != $last->{lat} or $coords->{lng} != $last->{lng}) {
        push @{ $self->{points} }, { lat => $coords->{lat}, lng => $coords->{lng} };
    }
    $self->_set_last_update(time);
}

sub get_last_point {
    my $self = shift;
    return $self->points->[-1];
}


1;
