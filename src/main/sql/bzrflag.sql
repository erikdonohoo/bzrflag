CREATE KEYSPACE bzrflag WITH REPLICATION = {'class':'SimpleStrategy','replication_factor':1};
USE bzrflag;

CREATE TABLE pfgenetics(
	gene text PRIMARY KEY,
	generation int,
	attradius float,
	attspread float,
	attstrength float,
	rejradius float,
	rejspread float,
	rejstrength float,
	tanradius float,
	tanspread float,
	tanstrength float,
	fitness list<float>,
	parentgenes list<text>,
	mutations text
);

CREATE INDEX generation_key ON pfgenetics(generation);

CREATE TABLE pfgeneticsfitness(
	gene text,
	fitness int,
	map text,
	note text,
	PRIMARY KEY(gene, fitness, map, note)
);

INSERT INTO pfgenetics (gene, generation, attradius, attspread, attstrength, rejradius, rejspread, rejstrength, tanradius, tanspread, tanstrength, parentgenes, mutations)
VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);

INSERT INTO pfgeneticsfitness(gene, fitness, map, note) VALUES (?,?,?,?);

INSERT INTO pfgenetics (gene, generation, attradius, attspread, attstrength, rejradius, rejspread, rejstrength, tanradius, tanspread, tanstrength, parentgenes, mutations)
VALUES ('1.00-25.00-1.00-1.00-1.50-1.00-1.10-1.30-0.90',1,1.00,25.00,1.00,1.00,1.50,1.00,1.10,1.30,0.90,[],'This one came from our default setup and should have a fairly decent fitness');

UPDATE pfgenetics SET 
