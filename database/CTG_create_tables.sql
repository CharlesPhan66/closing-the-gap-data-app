PRAGMA foreign_keys = OFF;
drop table if exists Members;
drop table if exists Persona;
drop table if exists States;
drop table if exists Outcomes;
drop table if exists AgeGroup;
drop table if exists Sex;
drop table if exists HealthCondition;
drop table if exists SchoolCompletion;
drop table if exists Population;
PRAGMA foreign_keys = ON;

CREATE TABLE Members (
    sID     INTEGER PRIMARY KEY,
    name    TEXT    NOT NULL
);

CREATE TABLE Persona (
    id                INTEGER PRIMARY KEY,
    quote             TEXT               ,
    needs_goals       TEXT               ,
    pain_points       TEXT               ,
    skills_experience TEXT
);

CREATE TABLE States (
    stateID   INTEGER  PRIMARY KEY,
    stateName TEXT     NOT NULL
);

CREATE TABLE Outcomes (
    outcomeID   INTEGER PRIMARY KEY,
    name        TEXT    NOT NULL,
    description TEXT
);

CREATE TABLE AgeGroup (
    ageID     INTEGER PRIMARY KEY,
    ageStart  INTEGER NOT NULL,
    ageEnd    INTEGER NOT NULL
);

CREATE TABLE Sex (
    sexID         INTEGER PRIMARY KEY,
    description   TEXT    NOT NULL
);

CREATE TABLE HealthCondition (
    conditionID INTEGER PRIMARY KEY,
    status      TEXT    NOT NULL,
    diseaseName TEXT    NOT NULL
);

CREATE TABLE SchoolCompletion (
    completionID INTEGER PRIMARY KEY,
    schoolYear   TEXT    NOT NULL
);

CREATE TABLE Population (
    year         INTEGER NOT NULL,
    stateID      INTEGER NOT NULL,
    sexID        INTEGER NOT NULL,
    completionID INTEGER NOT NULL,
    ageID        INTEGER NOT NULL,
    outcomeID    INTEGER NOT NULL,
    conditionID  INTEGER NOT NULL,
    population   INTEGER NOT NULL,
    PRIMARY KEY (year, stateID, sexID, completionID, ageID, outcomeID, conditionID),
    FOREIGN KEY (stateID)      REFERENCES States(stateID),
    FOREIGN KEY (sexID)        REFERENCES Sex(sexID),
    FOREIGN KEY (completionID) REFERENCES SchoolCompletion(completionID),
    FOREIGN KEY (ageID)        REFERENCES AgeGroup(ageID),
    FOREIGN KEY (outcomeID)    REFERENCES Outcomes(outcomeID),
    FOREIGN KEY (conditionID)  REFERENCES HealthCondition(conditionID)
);