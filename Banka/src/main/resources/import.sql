INSERT INTO BANK(NAME,CODE,SWIFT_CODE,CLEARING_ACCOUNT) VALUES('bank1','123','12345678','123456789123456789');
INSERT INTO BANK(NAME,CODE,SWIFT_CODE,CLEARING_ACCOUNT) VALUES('bank2','456','88344696','543216789123456987');

INSERT INTO BILL(ACCOUNT_NUMBER,STATUS,DATE) VALUES('123852369874512365',true,'2016-11-11');
INSERT INTO BILL(ACCOUNT_NUMBER,STATUS,DATE) VALUES('456752378574512664',true,'2016-12-21');
INSERT INTO BILL(ACCOUNT_NUMBER,STATUS,DATE) VALUES('123758369874575393',true,'2016-03-03');


INSERT INTO BANKER (MAIL, PASSWORD, FIRSTNAME, LASTNAME) VALUES('1','1','1','1');
INSERT INTO BANKER (MAIL, PASSWORD, FIRSTNAME, LASTNAME) VALUES('2','2','1','1');


INSERT INTO CODE_BOOK_ACTIVITIES (CODE, NAME) VALUES(12365,'djelatnost1');
INSERT INTO CODE_BOOK_ACTIVITIES (CODE, NAME) VALUES(75893,'djelatnost2');


INSERT INTO COUNTRY(CODE, NAME) VALUES('srb','Srbija');
INSERT INTO COUNTRY(CODE, NAME) VALUES('bih','Bosna i Hercegovina');

INSERT INTO BANK_BILLS(BANK_ID,BILL_ID) VALUES(1,2);