create table note (
    id uuid primary key,
    content text not null,
    priority int,
    label text,
    completed boolean
)