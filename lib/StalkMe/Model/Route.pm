package StalkMe::Model::Route;

use Mandel::Document;
use Types::Standard qw( Str Int ArrayRef HashRef Num );


field last_update => (
    isa     => Str,
    builder => sub { time }
);

field points => (
    isa     => ArrayRef,
    builder => sub { [] }
);

field 'id';

sub add_point {
    my $self = shift;
    my $coords = shift;
    my $last = $self->get_last_point;
    # Deduplication!
    if (!$last or $coords->{lat} != $last->{lat}
        or $coords->{lng} != $last->{lng}) {
        push @{ $self->points },
            { lat => $coords->{lat}, lng => $coords->{lng} };
    }
    $self->last_update(time);
}

sub get_last_point {
    my $self = shift;
    return $self->points->[-1];
}


1;
