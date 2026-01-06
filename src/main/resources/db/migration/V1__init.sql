create table users (
                       id bigserial primary key,
                       email varchar(320) not null unique,
                       full_name varchar(200) not null,
                       created_at timestamptz not null default now(),
                       updated_at timestamptz not null default now()
);

create table contacts (
                          id bigserial primary key,
                          user_id bigint not null references users(id) on delete cascade,
                          type varchar(40) not null,
                          value varchar(500) not null,
                          created_at timestamptz not null default now(),
                          updated_at timestamptz not null default now()
);

create table listings (
                          id bigserial primary key,
                          owner_user_id bigint not null references users(id) on delete cascade,
                          title varchar(300) not null,
                          description text null,
                          price numeric(19,2) not null,
                          currency varchar(10) not null,
                          status varchar(40) not null,
                          created_at timestamptz not null default now(),
                          updated_at timestamptz not null default now()
);

create table transactions (
                              id bigserial primary key,
                              listing_id bigint not null references listings(id) on delete restrict,
                              buyer_user_id bigint not null references users(id) on delete restrict,
                              seller_user_id bigint not null references users(id) on delete restrict,
                              amount numeric(19,2) not null,
                              status varchar(40) not null,
                              created_at timestamptz not null default now(),
                              updated_at timestamptz not null default now()
);

create index idx_contacts_user_id on contacts(user_id);
create index idx_listings_owner_user_id on listings(owner_user_id);
create index idx_transactions_listing_id on transactions(listing_id);
