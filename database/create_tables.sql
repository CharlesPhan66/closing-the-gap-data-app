PRAGMA foreign_keys = OFF;
drop table if exists Members;
drop table if exists Persona;
drop table if exists ageGroup;
drop table if exists indigStatus;
drop table if exists Sex;
drop table if exists healthCondition;
drop table if exists Edu;
drop table if exists nonSchool;
drop table if exists States;
drop table if exists LGA;
drop table if exists Population;
drop table if exists Health;
drop table if exists Education;
drop table if exists NonSchoolEdu;
drop table if exists Outcomes;
drop table if exists Priorities;
drop table if exists LGAtype;
PRAGMA foreign_keys = ON;

ALTER TABLE LGA
ADD COLUMN typeID TEXT REFERENCES LGAtype(typeID);

CREATE TABLE LGAtype (
    typeID      TEXT PRIMARY KEY,
    description TEXT NOT NULL
);

ALTER TABLE LGA
ADD COLUMN area_sqkm REAL;

ALTER TABLE LGA
ADD COLUMN latitude REAL;

ALTER TABLE LGA
ADD COLUMN longitude REAL;

CREATE TABLE Priorities (
    priorityID  INTEGER PRIMARY KEY,
    description TEXT NOT NULL
);

CREATE TABLE Outcomes (
    outcomeID INTEGER PRIMARY KEY,
    outcome   TEXT NOT NULL,
    target    TEXT NOT NULL
);

CREATE TABLE Members (
    sID    TEXT PRIMARY KEY,
    name   TEXT    NOT NULL
);

CREATE TABLE Persona (
    id                  TEXT PRIMARY KEY,
    quote               TEXT,
    background          TEXT,
    needs_goals         TEXT,
    pain_points         TEXT,
    skills_experience   TEXT
);

CREATE TABLE ageGroup (
    ageID     TEXT PRIMARY KEY,
    ageStart  INTEGER NOT NULL,
    ageEnd    INTEGER
);

CREATE TABLE indigStatus (
    statusID TEXT PRIMARY KEY,
    status   TEXT NOT NULL
);

CREATE TABLE Sex (
    sexID TEXT PRIMARY KEY,
    sex   TEXT NOT NULL
);

CREATE TABLE healthCondition (
    conditionID TEXT PRIMARY KEY,
    diseaseName TEXT NOT NULL,
    description TEXT
);

CREATE TABLE Edu (
    levelID TEXT PRIMARY KEY,
    level   TEXT NOT NULL
);

CREATE TABLE nonSchool (
    d_cID TEXT PRIMARY KEY,
    name  TEXT NOT NULL
);

CREATE TABLE States (
    stateID INTEGER PRIMARY KEY,
    name    TEXT NOT NULL
);

CREATE TABLE LGA (
    lgaCode TEXT NOT NULL,
    year    INTEGER NOT NULL,
    lgaName TEXT NOT NULL,
    stateID INTEGER,
    PRIMARY KEY (lgaCode, year),
    FOREIGN KEY (stateID) REFERENCES States(stateID)
);

CREATE TABLE Population (
    populationID     INTEGER PRIMARY KEY AUTOINCREMENT,
    year             INTEGER NOT NULL,
    lgaCode          TEXT,
    sexID            TEXT,
    statusID         TEXT,
    ageID            TEXT,
    populationValue  INTEGER,
    FOREIGN KEY (lgaCode, year) REFERENCES LGA(lgaCode, year),
    FOREIGN KEY (sexID) REFERENCES Sex(sexID),
    FOREIGN KEY (statusID) REFERENCES indigStatus(statusID),
    FOREIGN KEY (ageID) REFERENCES ageGroup(ageID)
);

CREATE TABLE Health (
    healthID        INTEGER PRIMARY KEY AUTOINCREMENT,
    year            INTEGER NOT NULL,
    lgaCode         TEXT,
    sexID           TEXT,
    statusID        TEXT, 
    ageID           TEXT,
    conditionID     TEXT,
    populationValue INTEGER,
    FOREIGN KEY (lgaCode, year) REFERENCES LGA(lgaCode, year),
    FOREIGN KEY (sexID) REFERENCES Sex(sexID),
    FOREIGN KEY (statusID) REFERENCES indigStatus(statusID),
    FOREIGN KEY (ageID) REFERENCES ageGroup(ageID),
    FOREIGN KEY (conditionID) REFERENCES healthCondition(conditionID)
);

CREATE TABLE Education (
    eduID           INTEGER PRIMARY KEY AUTOINCREMENT,
    year            INTEGER NOT NULL,
    lgaCode         TEXT,
    sexID           TEXT,
    statusID        TEXT,
    ageID           TEXT,
    levelID         TEXT,
    populationValue INTEGER,
    FOREIGN KEY (lgaCode, year) REFERENCES LGA(lgaCode, year),
    FOREIGN KEY (sexID) REFERENCES Sex(sexID),
    FOREIGN KEY (statusID) REFERENCES indigStatus(statusID),
    FOREIGN KEY (ageID) REFERENCES ageGroup(ageID),
    FOREIGN KEY (levelID) REFERENCES Edu(levelID)
);

CREATE TABLE NonSchoolEdu (
    nonSchoolEduID  INTEGER PRIMARY KEY AUTOINCREMENT,
    year            INTEGER NOT NULL,
    lgaCode         TEXT,
    sexID           TEXT,
    statusID        TEXT,
    ageID           TEXT,
    d_cID           TEXT,
    populationValue INTEGER,
    FOREIGN KEY (lgaCode, year) REFERENCES LGA(lgaCode, year),
    FOREIGN KEY (sexID) REFERENCES Sex(sexID),
    FOREIGN KEY (statusID) REFERENCES indigStatus(statusID),
    FOREIGN KEY (ageID) REFERENCES ageGroup(ageID),
    FOREIGN KEY (d_cID) REFERENCES nonSchool(d_cID)
);

CREATE TABLE LGA_tmp16 (
    lga_code16 INTEGER,
    lga_name16 TEXT
);


CREATE TABLE LGA_tmp21 (
    LGA_CODE21 INTEGER,
    LGA_NAME21 TEXT
);

CREATE TABLE LGA_tmp (
    lgaCode   TEXT,
    name      TEXT,
    type      TEXT,
    area_sqkm REAL,
    latitude  REAL,
    longitude REAL
);

CREATE TABLE Education_TEST (
    eduID           INTEGER PRIMARY KEY AUTOINCREMENT,
    year            INTEGER NOT NULL,
    lgaCode         TEXT,
    sexID           TEXT,
    statusID        TEXT,
    ageID           TEXT,
    levelID         TEXT,
    populationValue INTEGER,
    FOREIGN KEY (lgaCode, year) REFERENCES LGA(lgaCode, year),
    FOREIGN KEY (sexID) REFERENCES Sex(sexID),
    FOREIGN KEY (statusID) REFERENCES indigStatus(statusID),
    FOREIGN KEY (ageID) REFERENCES ageGroup(ageID),
    FOREIGN KEY (levelID) REFERENCES Edu(levelID)
);

CREATE TABLE Education_TEST_tmp (
    year            INTEGER NOT NULL,
    lgaCode         TEXT NOT NULL,
    sexID           TEXT,
    statusID        TEXT,
    ageID           TEXT,
    levelID         TEXT,
    populationValue INTEGER
);

CREATE TABLE Population_TEST (
    populationID     INTEGER PRIMARY KEY AUTOINCREMENT,
    year             INTEGER NOT NULL,
    lgaCode          TEXT,
    sexID            TEXT,
    statusID         TEXT,
    ageID            TEXT,
    populationValue  INTEGER,
    FOREIGN KEY (lgaCode, year) REFERENCES LGA(lgaCode, year),
    FOREIGN KEY (sexID) REFERENCES Sex(sexID),
    FOREIGN KEY (statusID) REFERENCES indigStatus(statusID),
    FOREIGN KEY (ageID) REFERENCES ageGroup(ageID)
);

CREATE TABLE Population_TEST_tmp (
    year             INTEGER NOT NULL,
    lgaCode          TEXT,
    statusID         TEXT,
    ageID            TEXT,
    sexID            TEXT,
    populationValue  INTEGER
);

CREATE TABLE Health_TEST (
    healthID        INTEGER PRIMARY KEY AUTOINCREMENT,
    year            INTEGER NOT NULL,
    lgaCode         TEXT,
    sexID           TEXT,
    statusID        TEXT, 
    ageID           TEXT,
    conditionID     TEXT,
    populationValue INTEGER,
    FOREIGN KEY (lgaCode, year) REFERENCES LGA(lgaCode, year),
    FOREIGN KEY (sexID) REFERENCES Sex(sexID),
    FOREIGN KEY (statusID) REFERENCES indigStatus(statusID),
    FOREIGN KEY (ageID) REFERENCES ageGroup(ageID),
    FOREIGN KEY (conditionID) REFERENCES healthCondition(conditionID)
);

CREATE TABLE Health_TEST_tmp (
    year            INTEGER NOT NULL,
    lgaCode         TEXT,
    statusID        TEXT, 
    ageID           TEXT,
    sexID           TEXT,
    conditionID     TEXT,
    populationValue INTEGER
);


CREATE TABLE NonSchoolEdu_TEST (
    nonSchoolEduID  INTEGER PRIMARY KEY AUTOINCREMENT,
    year            INTEGER NOT NULL,
    lgaCode         TEXT,
    sexID           TEXT,
    statusID        TEXT,
    ageID           TEXT,
    d_cID           TEXT,
    populationValue INTEGER,
    FOREIGN KEY (lgaCode, year) REFERENCES LGA(lgaCode, year),
    FOREIGN KEY (sexID) REFERENCES Sex(sexID),
    FOREIGN KEY (statusID) REFERENCES indigStatus(statusID),
    FOREIGN KEY (ageID) REFERENCES ageGroup(ageID),
    FOREIGN KEY (d_cID) REFERENCES nonSchool(d_cID)
);

CREATE TABLE NonSchoolEdu_TEST_tmp (
    year            INTEGER NOT NULL,
    lgaCode         TEXT,
    sexID           TEXT,
    statusID        TEXT,
    ageID           TEXT,
    d_cID           TEXT,
    populationValue INTEGER
);

DROP TABLE LGA_tmp16;
DROP TABLE LGA_tmp21;
drop table Education_TEST;
drop table Education_TEST_tmp;
drop table Population_TEST;
drop table Population_TEST_tmp;
drop table Health_TEST;
drop table Health_TEST_tmp;
drop table NonSchoolEdu_TEST;
drop table NonSchoolEdu_TEST_tmp;
drop table LGA_tmp;