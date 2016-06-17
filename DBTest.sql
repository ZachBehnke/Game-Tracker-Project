drop database if exists DBTest;
create database DBTest;
use DBTest;

create table MasterGameList (
	Game_Title VARCHAR(100) not null,
    Game_System VARCHAR(10) not null,
    Complete VARCHAR(5) not null,
    Game_Beaten VARCHAR(5) not null
	);