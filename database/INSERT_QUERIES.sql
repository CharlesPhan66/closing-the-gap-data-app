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

