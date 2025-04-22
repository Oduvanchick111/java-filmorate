MERGE INTO MPA (mpa_id, name) VALUES (1, 'G');
MERGE INTO MPA (mpa_id, name) VALUES (2, 'PG');
MERGE INTO MPA (mpa_id, name) VALUES (3, 'PG-13');
MERGE INTO MPA (mpa_id, name) VALUES (4, 'R');
MERGE INTO MPA (mpa_id, name) VALUES (5, 'NC-17');

MERGE INTO genre(genre_id, genre) VALUES (1, 'Комедия');
MERGE INTO genre(genre_id, genre) VALUES (2, 'Драма');
MERGE INTO genre(genre_id, genre) VALUES (3, 'Мультфильм');
MERGE INTO genre(genre_id, genre) VALUES (4, 'Триллер');
MERGE INTO genre(genre_id, genre) VALUES (5, 'Документальный');
MERGE INTO genre(genre_id, genre) VALUES (6, 'Боевик');
