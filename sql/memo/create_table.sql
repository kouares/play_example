create table memo.tag_mst (
  id int auto_increment primary key, 
  name varchar(100)
);

create table memo.tag_mapping (
  memo_id int,
  tag_id int,
  foreign key (memo_id) references memo(id),
  foreign key (tag_id) references tag_mst(id)
);

create table memo.memo (
  id int auto_increment primary key,
  title varchar(200) not null, 
  main_text varchar(10000),
  upadted_at datetime,
  created_at datetime not null
);
