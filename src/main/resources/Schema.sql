-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/UlRdt2
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.


CREATE TABLE "User" (
    "UserID" int   NOT NULL,
    "Login" varchar2(30)   NOT NULL,
    "UserName" varchar2(100)   NOT NULL,
    "Email" varchar2(40)   NOT NULL,
    "Birthday" date   NOT NULL,
    CONSTRAINT "pk_User" PRIMARY KEY (
        "UserID"
     )
);

CREATE TABLE "Friends" (
    "FriendID" int   NOT NULL,
    "UserID" int   NOT NULL,
    "StatusID" int   NOT NULL,
    CONSTRAINT "pk_Friends" PRIMARY KEY (
        "FriendID"
     )
);

CREATE TABLE "Statuses" (
    "StatusID" int   NOT NULL,
    "StatusName" varchar2(20)   NOT NULL,
    CONSTRAINT "pk_Statuses" PRIMARY KEY (
        "StatusID"
     )
);

CREATE TABLE "Films" (
    "FilmID" int   NOT NULL,
    "FilmName" varchar2(100)   NOT NULL,
    "Description" varchar2(255)   NOT NULL,
    "ReleaseDate" date   NOT NULL,
    "Duration" int   NOT NULL,
    "RatingID" int   NOT NULL,
    CONSTRAINT "pk_Films" PRIMARY KEY (
        "FilmID"
     )
);

CREATE TABLE "Ratings" (
    "RatingID" int   NOT NULL,
    "RatingName" varchar2(10)   NOT NULL,
    CONSTRAINT "pk_Ratings" PRIMARY KEY (
        "RatingID"
     )
);

CREATE TABLE "Likes" (
    "UserID" int   NOT NULL,
    "FilmID" int   NOT NULL
);

CREATE TABLE "Genres" (
    "FilmID" int   NOT NULL,
    "GenreID" int   NOT NULL
);

CREATE TABLE "GenreNames" (
    "GenreID" int   NOT NULL,
    "GenreName" varchar2(20)   NOT NULL,
    CONSTRAINT "pk_GenreNames" PRIMARY KEY (
        "GenreID"
     )
);

ALTER TABLE "Friends" ADD CONSTRAINT "fk_Friends_UserID" FOREIGN KEY("UserID")
REFERENCES "User" ("UserID");

ALTER TABLE "Friends" ADD CONSTRAINT "fk_Friends_StatusID" FOREIGN KEY("StatusID")
REFERENCES "Statuses" ("StatusID");

ALTER TABLE "Films" ADD CONSTRAINT "fk_Films_RatingID" FOREIGN KEY("RatingID")
REFERENCES "Ratings" ("RatingID");

ALTER TABLE "Likes" ADD CONSTRAINT "fk_Likes_UserID" FOREIGN KEY("UserID")
REFERENCES "User" ("UserID");

ALTER TABLE "Likes" ADD CONSTRAINT "fk_Likes_FilmID" FOREIGN KEY("FilmID")
REFERENCES "Films" ("FilmID");

ALTER TABLE "Genres" ADD CONSTRAINT "fk_Genres_FilmID" FOREIGN KEY("FilmID")
REFERENCES "Films" ("FilmID");

ALTER TABLE "Genres" ADD CONSTRAINT "fk_Genres_GenreID" FOREIGN KEY("GenreID")
REFERENCES "GenreNames" ("GenreID");

