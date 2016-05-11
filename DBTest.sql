drop database if exists DBTest;
create database DBTest;
use DBTest;

create table MasterGameList (
	Game_Title VARCHAR(100) not null,
    Game_System VARCHAR(25) not null,
    Complete VARCHAR(5) not null,
    Game_Beaten VARCHAR(5) not null
	);
    
create table NotBeatenGames (
	Game_Title VARCHAR(100) not null,
    Game_System VARCHAR(25) not null,
    Complete VARCHAR(5) not null,
    Game_Beaten VARCHAR(5) not null
);

load data infile 'c:/Users/Zach/My Documents/Game Tracker Project/GameTrackerDB.txt'
into table MasterGameList
ignore 1 rows;

Insert into NotBeatenGames (SELECT * FROM MasterGameList WHERE INSTR(Game_Beaten, 'No') > 0);

select * from MasterGameList;