INSERT INTO hashtag (hashtag_name) VALUES ('식사');
INSERT INTO hashtag (hashtag_name) VALUES ('카페');
INSERT INTO hashtag (hashtag_name) VALUES ('공부');
INSERT INTO hashtag (hashtag_name) VALUES ('문화생활');
INSERT INTO hashtag (hashtag_name) VALUES ('쇼핑');
INSERT INTO hashtag (hashtag_name) VALUES ('자연');
INSERT INTO hashtag (hashtag_name) VALUES ('산책');
INSERT INTO hashtag (hashtag_name) VALUES ('친목');
INSERT INTO hashtag (hashtag_name) VALUES ('여럿이');
INSERT INTO hashtag (hashtag_name) VALUES ('어린이');
INSERT INTO hashtag (hashtag_name) VALUES ('혼자');

-- 테스트용 db

INSERT INTO member (member_pw, member_name, member_email, member_nickname) VALUES ('password1', 'yeonjaechoi', '0yeonjae2@naver.com', 'yj');
INSERT INTO member (member_pw, member_name, member_email, member_nickname) VALUES ('password2', 'soluction', 'soluction@gmail.com', 'sc');
INSERT INTO member (member_pw, member_name, member_email, member_nickname) VALUES ('password3', 'soluctionttt', 'soluctiotttn@gmail.com', 'scttt');

INSERT INTO post (member_id, title, content, create_date, update_date) VALUES (1, 'Example Post1', 'This is an example post content.', '2024-07-11 10:00:00', '2024-07-11 10:00:00');

INSERT INTO post (member_id, title, content, create_date, update_date) VALUES (2, 'Example Post2', 'This is an example post content.', '2024-07-11 10:00:00', '2024-07-11 10:00:00');

INSERT INTO post_hashtag (post_id, hashtag_id) VALUES (1, 1),(1, 2);

INSERT INTO post_hashtag (post_id, hashtag_id) VALUES (2, 6),(2, 7);

INSERT INTO image (image_url, post_id, create_date, member_id) VALUES ('https://example.com/image1.jpg', 1,'2024-07-11 10:00:00',1), ('https://example.com/image2.jpg', 1, '2024-07-11 10:00:00',1);

INSERT INTO image (image_url, post_id, create_date, member_id) VALUES ('https://example.com/image3.jpg', 2,'2024-07-11 10:00:00',2),  ('https://example.com/image4.jpg', 2, '2024-07-11 10:00:00',2);

INSERT INTO likes (post_id, member_id, is_like, create_date, update_date) VALUES (1, 1, true,  '2024-07-12 10:00:00', '2024-07-12 10:00:00');
INSERT INTO likes (post_id, member_id, is_like, create_date, update_date) VALUES (1, 2, true,  '2024-07-12 10:00:00', '2024-07-12 10:00:00');
INSERT INTO likes (post_id, member_id, is_like, create_date, update_date) VALUES (1, 3, true,  '2024-07-12 10:00:00', '2024-07-12 10:00:00');