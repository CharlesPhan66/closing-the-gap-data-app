DELETE FROM LGA_tmp16
WHERE lga_code16 = 'lga_code16';

INSERT INTO LGA (lgaCode, year, lgaName)
SELECT lga_code16, 2016, lga_name16
FROM LGA_tmp16;

DELETE FROM LGA_tmp21 WHERE LGA_CODE21 = 'lga_code21';

INSERT INTO LGA (lgaCode, year, lgaName)
SELECT LGA_CODE21, 2021, LGA_NAME21
FROM LGA_tmp21;

UPDATE LGA
SET stateID = CAST(SUBSTR(lgaCode, 1, 1) AS INTEGER)
WHERE lgaCode GLOB '[0-9]*';

UPDATE indigStatus
SET statusID = 'indig_stat_notstated'
WHERE statusID = 'notstated';

INSERT INTO Education (year, lgaCode, sexID, statusID, ageID, levelID, populationValue)
SELECT year, lgaCode, sexID, statusID, ageID, levelID, populationValue
FROM Education_TEST_tmp;

DELETE FROM Education_TEST;
DELETE FROM Education_TEST_tmp;

INSERT INTO Population_TEST (year, lgaCode, sexID, statusID, ageID, populationValue)
SELECT year, lgaCode, sexID, statusID, ageID, populationValue
FROM Population_TEST_tmp;

INSERT INTO Population (year, lgaCode, sexID, statusID, ageID, populationValue)
SELECT year, lgaCode, sexID, statusID, ageID, populationValue
FROM Population_TEST;

DELETE FROM Population_TEST;
DELETE FROM Population_TEST_tmp;

INSERT INTO Health_TEST (year, lgaCode, sexID, statusID, ageID, conditionID, populationValue)
SELECT year, lgaCode, sexID, statusID, ageID, conditionID, populationValue
FROM Health_TEST_tmp;

INSERT INTO Health (year, lgaCode, sexID, statusID, ageID, conditionID, populationValue)
SELECT year, lgaCode, sexID, statusID, ageID, conditionID, populationValue
FROM Health_TEST;

DELETE FROM Health_TEST;
DELETE FROM Health_TEST_tmp;

INSERT INTO NonSchoolEdu_TEST (year, lgaCode, sexID, statusID, ageID, d_cID, populationValue)
SELECT year, lgaCode, sexID, statusID, ageID, d_cID, populationValue
FROM NonSchoolEdu_TEST_tmp;

INSERT INTO NonSchoolEdu (year, lgaCode, sexID, statusID, ageID, d_cID, populationValue)
SELECT year, lgaCode, sexID, statusID, ageID, d_cID, populationValue
FROM NonSchoolEdu_TEST;

DELETE FROM NonSchoolEdu_TEST;
DELETE FROM NonSchoolEdu_TEST_tmp;