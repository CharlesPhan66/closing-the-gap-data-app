INSERT INTO ageGroup (ageID, ageStart, ageEnd) VALUES
('0_4_yrs', 0, 4),
('5_9_yrs', 5, 9),
('10_14_yrs', 10, 14),
('15_19_yrs', 15, 19),
('20_24_yrs', 20, 24),
('25_29_yrs', 25, 29),
('30_34_yrs', 30, 34),
('35_39_yrs', 35, 39),
('40_44_yrs', 40, 44),
('45_49_yrs', 45, 49),
('50_54_yrs', 50, 54),
('55_59_yrs', 55, 59),
('60_64_yrs', 60, 64),
('65_yrs_ov', 65, NULL);

INSERT INTO Edu (levelID, level) VALUES
('did_not_go_to_school', 'Did not attend School'),
('y8_below', 'Year 8 or Below'),
('y9_equivalent', 'Year 9 or Equivalent'),
('y10_equivalent', 'Year 10 or Equivalent'),
('y11_equivalent', 'Year 11 or Equivalent'),
('y12_equivalent', 'Year 12 or Equivalent');

INSERT INTO healthCondition (conditionID, diseaseName, description) VALUES
('arthritis', 'Arthritis', 'Those suffering from Arthritis'),
('asthma', 'Asthma', 'Those suffering from Asthma'),
('cancer', 'Cancer', 'Those suffering from Cancer (any type) including remission'),
('dementia', 'Dementia', 'Those suffering from Dementia (at any stage) including Alzheimers'),
('diabetes', 'Diabetes', 'Those suffering from Diabetes excluding Gestational Diabetes'),
('heartdisease', 'Heart Disease', 'Those suffering from Heart Disease including Heart Attack or Angina'),
('kidneydisease', 'Kidney Disease', 'Those suffering from Kidney Disease'),
('lungcondition', 'Lung Condition', 'Those suffering from any form of Lung Condition including Chronic obstructive pulmonary disease (COPD) or Emphysema'),
('mentalhealth', 'Mental Health', 'Those reporting any type of Mental Health Condition including depression or anxiety'),
('stroke', 'Stroke', 'Those who suffered a Stroke and ongoing long-term health impacts'),
('other', 'Other', 'Those suffering from a Long-Term Health Condition not listed above');

INSERT INTO indigStatus (statusID, status) VALUES
('indig', 'Indigenous'),
('non_indig', 'Non-indigenous'),
('indig_stat_notstated', 'Indigenous status was not specified');

INSERT INTO nonSchool (d_cID, name) VALUES
('pd_gd_gc', 'Postgraduate Degree Level, Graduate Diploma and Graduate Certificate Level'),
('bd', 'Bachelor Degree Level'),
('adip_dip', 'Advanced Diploma and Diploma Level'),
('ct_iii_iv', 'Certificate III & IV Level'),
('ct_i_ii', 'Certificate I & II Level');

INSERT INTO Sex (sexID, sex) VALUES
('f', 'Female'),
('m', 'Male');

INSERT INTO States (stateID, name) VALUES
(1, 'New South Wales'),
(2, 'Victoria'),
(3, 'Queensland'),
(4, 'South Australia'),
(5, 'Western Australia'),
(6, 'Tasmania'),
(7, 'Northern Territory'),
(8, 'Australian Capital Territory'),
(9, 'Other Australian Territories, including Offshore Island Territories, Jervis Bay Territory, and Australian Antarctic Territory');

INSERT INTO Persona (
    id,
    quote,
    background,
    needs_goals,
    pain_points,
    skills_experience
) VALUES (
    1,
    'I want a quick, clear understanding of what this campaign is about without digging through long reports. If I need to go deeper, I would like to explore the program’s progress and find credible data that makes my work, whether it is drafting an article, preparing a speech, or analyzing policy, more transparent and impactful.',
    '25 years old, single
Location: Australia
Occupation: Recently graduated and started a internship for political position, hoping to climb the ladder to senior political analyst.
Not deeply involved in policy or statistics at first, but exposure grows with time and position.
Discover the Closing the Gap initiative for the overview of plan, then dive deeper for work 
Eager to explore but easily overwhelmed by technical details',
    'Quickly understand the campaign’s goals and stage of progress
Ability to share findings with friends or community
Understand informal/non-professional words
To quickly know which stage the campaign achieve with clear progress tracking visuals
Can track data/outcomes of overall progress in recent years to report the tendency
Consistent navigation cues to track what he’s already read or used
Download charts to add to his work
Customizable charts, filters, and downloadable datasets.
Access to data of various categories, such as regions, partners, different outcomes, etc.
Compare the progress of different outcomes for considerations or partners for productivity and award with various filters
Receives notifications when news/updates are updated',
    'Long text and heavy explanations make him lose interest in initial stage
Hard to find the key message quickly on the site.
Struggles with poor navigation or unclear menus
Can''t change type of charts for better interpretation
Need to download excel files to compare manually',
    'Lack of knowledge about Indigenous Australians
Can read professional language
Conducted many other political program analysis for his study'
);

INSERT INTO Members (sID, name) VALUES ('s4131861','Charles Phan');
INSERT INTO Members (sID, name) VALUES ('s4066942','Nguyen Cong Quoc Dat');
INSERT INTO Members (sID, name) VALUES ('s4042625','Trinh Ngoc Tieu Long');
INSERT INTO Members (sID, name) VALUES ('s4119755','Nguy Cao Tri');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(1, 'Everyone enjoys long and healthy lives',
 'Close the Gap in life expectancy within a generation, by 2031');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(2, 'Children are born healthy and strong',
 'By 2031, increase the proportion of Aboriginal and Torres Strait Islander babies with a healthy birthweight to 91 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(3, 'Children are engaged in high quality, culturally appropriate early childhood education in their early years',
 'By 2025, increase the proportion of Aboriginal and Torres Strait Islander children enrolled in Year Before Fulltime Schooling (YBFS) early childhood education to 95 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(4, 'Children thrive in their early years',
 'By 2031, increase the proportion of Aboriginal and Torres Strait Islander children assessed as developmentally on track in all five domains of the Australian Early Development Census (AEDC) to 55 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(5, 'Students achieve their full learning potential',
 'By 2031, increase the proportion of Aboriginal and Torres Strait Islander people (age 20-24) attaining year 12 or equivalent qualification to 96 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(6, 'Students reach their full potential through further education pathways',
 'By 2031, increase the proportion of Aboriginal and Torres Strait Islander people aged 25-34 years who have completed a tertiary qualification (Certificate III and above) to 70 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(7, 'Youth are engaged in employment or education',
 'By 2031, increase the proportion of Aboriginal and Torres Strait Islander youth (15-24 years) who are in employment, education or training to 67 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(8, 'Strong economic participation and development of people and their communities',
 'By 2031, increase the proportion of Aboriginal and Torres Strait Islander people aged 25-64 who are employed to 62 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(9, 'People can secure appropriate, affordable housing that is aligned with their priorities and need',
 '9a: By 2031, increase the proportion of Aboriginal and Torres Strait Islander people living in appropriately sized (not overcrowded) housing to 88 per cent.\n\n9b: By 2031, all Aboriginal and Torres Strait Islander households:\n- within discrete Aboriginal and Torres Strait Islander communities receive essential services that meet or exceed the relevant jurisdictional standard\n- in or near to a town receive essential services that meet or exceed the same standard as applies generally within the town (including if the household might be classified for other purposes as a part of a discrete settlement such as a “town camp” or “town based reserve”))');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(10, 'Adults are not overrepresented in the criminal justice system',
 'By 2031, reduce the rate of Aboriginal and Torres Strait Islander adults held in incarceration by at least 15 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(11, 'Young people are not overrepresented in the criminal justice system',
 'By 2031, reduce the rate of Aboriginal and Torres Strait Islander young people (10-17 years) in detention by at least 30 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(12, 'Children are not overrepresented in the child protection system',
 'By 2031, reduce the rate of over-representation of Aboriginal and Torres Strait Islander children in out-of-home care by 45 per cent');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(13, 'Families and households are safe',
 'By 2031, the rate of all forms of family violence and abuse against Aboriginal and Torres Strait Islander women and children is reduced at least by 50%, as progress towards zero');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(14, 'People enjoy high levels of social and emotional wellbeing',
 'Significant and sustained reduction in suicide of Aboriginal and Torres Strait Islander people towards zero');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(15, 'People maintain a distinctive cultural, spiritual, physical and economic relationship with their land and waters',
 '15a: By 2030, a 15 per cent increase in Australia’s landmass subject to Aboriginal and Torres Strait Islander people’s legal rights or interests.\n\n15b: By 2030, a 15 per cent increase in areas covered by Aboriginal and Torres Strait Islander people’s legal rights or interests in the sea');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(16, 'Cultures and languages are strong, supported and flourishing',
 'By 2031, there is a sustained increase in number and strength of Aboriginal and Torres Strait Islander languages being spoken');

INSERT INTO Outcomes (outcomeID, outcome, target) VALUES
(17, 'People have access to information and services enabling participation in informed decision-making regarding their own lives',
 'By 2026, Aboriginal and Torres Strait Islander people have equal levels of digital inclusion');
 
UPDATE Outcomes
SET outcome = 'People can secure appropriate, affordable housing that is aligned with their priorities and need'
WHERE outcomeID = 9;

UPDATE Outcomes
SET target = 'By 2030, a 15 per cent increase in Australia’s landmass subject to Aboriginal and Torres Strait Islander people’s legal rights or interests. By 2030, a 15 per cent increase in areas covered by Aboriginal and Torres Strait Islander people’s legal rights or interests in the sea.'
WHERE outcomeID = 15;

INSERT INTO Priorities (priorityID, description) VALUES
(1, 'Formal Partnerships and Shared Decision Making'),
(2, 'Building the Community-Controlled Sector'),
(3, 'Transforming Government Organizations'),
(4, 'Shared Access to Data and Information at a Regional Level');

UPDATE LGA
SET 
    area_sqkm = (SELECT t.area_sqkm FROM LGA_tmp t WHERE t.lgaCode = LGA.lgaCode),
    latitude  = (SELECT t.latitude  FROM LGA_tmp t WHERE t.lgaCode = LGA.lgaCode),
    longitude = (SELECT t.longitude FROM LGA_tmp t WHERE t.lgaCode = LGA.lgaCode)
WHERE year = 2021
  AND EXISTS (SELECT 1 FROM LGA_tmp t WHERE t.lgaCode = LGA.lgaCode);
  
INSERT INTO LGAtype (typeID, description) VALUES
('C',    'City'),
('A',    'Area'),
('RC',   'Rural City'),
('B',    'Borough'),
('S',    'Shire'),
('T',    'Town'),
('R',    'Regional Council'),
('M',    'Municipality / Municipal Council'),
('DC',   'District Council'),
('RegC', 'Regional Council (South Australia)'),
('AC',   'Aboriginal Council');