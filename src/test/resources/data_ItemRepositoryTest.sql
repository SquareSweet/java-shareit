INSERT INTO users (name, email)
VALUES ('testUser1', 'test1@mail.com');

INSERT INTO items (name, description, is_available, owner_id)
VALUES ('testItem1_nameKeyword', 'testDescr1', true, 1),
       ('testItem2_nameKeyword', 'testDescr2', false, 1),
       ('testItem3', 'testDescr3_descriptionKeyword', true, 1),
       ('testItem4', 'testDescr4_descriptionKeyword', false, 1),
       ('testItem5', 'testDescr5', true, 1);