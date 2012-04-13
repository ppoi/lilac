INSERT INTO author(name, website, twitter) VALUES
	('おかゆまさき', 'http://okayumasaki.jugem.cc/index.html', 'juice_nomio'),
	('川原礫', 'http://wordgear.x0.com/', 'kunori'),
	('あざの耕平', NULL, 'k_aza'),
	('よう太', NULL, NULL),
	('和ヶ原聡司', NULL, 'wagahara211'),
	('029', NULL, 'o2929'),
	('明月千里', 'http://www.defectus-shissouyotei.saloon.jp/', 'meigetu_chisato'),
	('mebae', NULL, 'mebaeros'),
	('ま＠や', NULL, NULL);

INSERT INTO author(name, synonym_key) SELECT 'abec', id FROM author WHERE name='BUNBUN';
INSERT INTO author(name, synonym_key) SELECT 'M@YA', id FROM author WHERE name='ま＠や';
INSERT INTO author(name, synonym_key) SELECT 'MA@YA', id FROM author WHERE name='ま＠や';

