-- Begin the transaction
BEGIN;

-- Customers
INSERT INTO public.customer (id) VALUES ('1e19391c-134e-49cd-89cd-863bba1bf58f');
INSERT INTO public.customer (id) VALUES ('ef1a2ace-cd11-41de-a8f7-e1ecad61bf54');
INSERT INTO public.customer (id) VALUES ('5c93dc79-2987-4486-83e4-1f0bac6e79ef');

-- Job Offers
INSERT INTO public.job_offer (current_state, id, completed_professional_id, consolidated_professional_id, current_state_note, customer_id, description, name, duration, profit_margin, value) VALUES (3, nextval('public.job_offer_seq'), null, null, null, '1e19391c-134e-49cd-89cd-863bba1bf58f', 'Ho bisogno di uno sviluppatore esperto in C++ per lo sviluppo della mi applicazione embedded in real time.', 'Creazione Applicazione Embedded Real Time', E'\\xACED0005737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000168', E'\\xACED0005737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000F', null);
INSERT INTO public.job_offer (current_state, id, completed_professional_id, consolidated_professional_id, current_state_note, customer_id, description, name, duration, profit_margin, value) VALUES (3, nextval('public.job_offer_seq'), null, null, null, 'ef1a2ace-cd11-41de-a8f7-e1ecad61bf54', 'Ho bisogno di una persona esperta per costruire la mia piccola abitazione.', 'Costruzione Piccola Abitazione', E'\\xACED0005737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000870', E'\\xACED0005737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000007', null);
INSERT INTO public.job_offer (current_state, id, completed_professional_id, consolidated_professional_id, current_state_note, customer_id, description, name, duration, profit_margin, value) VALUES (4, nextval('public.job_offer_seq'), null, null, null, '5c93dc79-2987-4486-83e4-1f0bac6e79ef', 'Ho bisogno di un idraulico esperto per creare un impianto idraulico nella mia abitazione.', 'Creazione Impianto Idraulico', E'\\xACED0005737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000A8', E'\\xACED0005737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000008', null);
INSERT INTO public.job_offer (current_state, id, completed_professional_id, consolidated_professional_id, current_state_note, customer_id, description, name, duration, profit_margin, value) VALUES (0, nextval('public.job_offer_seq'), null, null, null, '1e19391c-134e-49cd-89cd-863bba1bf58f', 'Ho bisogno di un sito web per la mia azienda.', 'Sito Web Aziendale', E'\\xACED0005737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000001E0', E'\\xACED0005737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078700000000A', null);

-- Professionals
INSERT INTO public.professional (employment_state, current_job_offer_id, id, daily_rate, geographical_location) VALUES (1, null, '6189efed-3c1f-4f81-b838-04371721804f', E'\\xACED0005737200106A6176612E6C616E672E446F75626C6580B3C24A296BFB0402000144000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870404B800000000000', E'\\xACED00057372000B6B6F746C696E2E50616972FA1B06813DE78F780200024C000566697273747400124C6A6176612F6C616E672F4F626A6563743B4C00067365636F6E6471007E00017870737200106A6176612E6C616E672E446F75626C6580B3C24A296BFB0402000144000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000000000000007371007E00030000000000000000');
INSERT INTO public.professional (employment_state, current_job_offer_id, id, daily_rate, geographical_location) VALUES (1, null, '4d79da2e-001d-42f4-9f34-9b91b7e61b47', E'\\xACED0005737200106A6176612E6C616E672E446F75626C6580B3C24A296BFB0402000144000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078704046800000000000', E'\\xACED00057372000B6B6F746C696E2E50616972FA1B06813DE78F780200024C000566697273747400124C6A6176612F6C616E672F4F626A6563743B4C00067365636F6E6471007E00017870737200106A6176612E6C616E672E446F75626C6580B3C24A296BFB0402000144000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000000000000007371007E00030000000000000000');
INSERT INTO public.professional (employment_state, current_job_offer_id, id, daily_rate, geographical_location) VALUES (0, 101, '0611954d-13a4-4462-b470-5ece6ed15af5', E'\\xACED0005737200106A6176612E6C616E672E446F75626C6580B3C24A296BFB0402000144000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B02000078704049000000000000', E'\\xACED00057372000B6B6F746C696E2E50616972FA1B06813DE78F780200024C000566697273747400124C6A6176612F6C616E672F4F626A6563743B4C00067365636F6E6471007E00017870737200106A6176612E6C616E672E446F75626C6580B3C24A296BFB0402000144000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787040468766242147787371007E0003401EB05E7352DDF3');

UPDATE public.job_offer SET consolidated_professional_id = '0611954d-13a4-4462-b470-5ece6ed15af5' WHERE id = 101;

-- job_offer_candidate_professionals
INSERT INTO public.job_offer_candidate_professionals (job_offer_id, professional_id) VALUES (1, '4d79da2e-001d-42f4-9f34-9b91b7e61b47');
INSERT INTO public.job_offer_candidate_professionals (job_offer_id, professional_id) VALUES (51, '6189efed-3c1f-4f81-b838-04371721804f');

-- job offer aborted professionals (no data)

-- job_offers_history
INSERT INTO public.job_offers_history (state, date, id, job_offer_id, note) VALUES (2, '2025-01-03 11:56:14.269000', nextval('public.job_offers_history_seq'), 1, null);
INSERT INTO public.job_offers_history (state, date, id, job_offer_id, note) VALUES (3, '2025-01-03 11:56:25.873000', nextval('public.job_offers_history_seq'), 1, null);
INSERT INTO public.job_offers_history (state, date, id, job_offer_id, note) VALUES (2, '2025-01-03 11:57:27.704000', nextval('public.job_offers_history_seq'), 51, null);
INSERT INTO public.job_offers_history (state, date, id, job_offer_id, note) VALUES (3, '2025-01-03 11:57:37.608000', nextval('public.job_offers_history_seq'), 51, null);
INSERT INTO public.job_offers_history (state, date, id, job_offer_id, note) VALUES (2, '2025-01-03 11:59:22.307000', nextval('public.job_offers_history_seq'), 101, null);
INSERT INTO public.job_offers_history (state, date, id, job_offer_id, note) VALUES (3, '2025-01-03 11:59:30.730000', nextval('public.job_offers_history_seq'), 101, null);
INSERT INTO public.job_offers_history (state, date, id, job_offer_id, note) VALUES (4, '2025-01-03 11:59:38.105000', nextval('public.job_offers_history_seq'), 101, null);

-- contact
INSERT INTO public.contact (category, id, customer_id, name, professional_id, ssncode, surname) VALUES (0, nextval('public.contact_seq'), '1e19391c-134e-49cd-89cd-863bba1bf58f', 'Giovanni', null, '123-45-6789', 'Amato');
INSERT INTO public.contact (category, id, customer_id, name, professional_id, ssncode, surname) VALUES (0, nextval('public.contact_seq'), 'ef1a2ace-cd11-41de-a8f7-e1ecad61bf54', 'Giorgio', null, '152-46-8308', 'Gagliardo');
INSERT INTO public.contact (category, id, customer_id, name, professional_id, ssncode, surname) VALUES (0, nextval('public.contact_seq'), '5c93dc79-2987-4486-83e4-1f0bac6e79ef', 'Luca', null, '762-24-9350', 'Vanni');
INSERT INTO public.contact (category, id, customer_id, name, professional_id, ssncode, surname) VALUES (1, nextval('public.contact_seq'), null, 'Gianluigi', '6189efed-3c1f-4f81-b838-04371721804f', '895-38-1308', 'Roberto');
INSERT INTO public.contact (category, id, customer_id, name, professional_id, ssncode, surname) VALUES (1, nextval('public.contact_seq'), null, 'Domenico', '4d79da2e-001d-42f4-9f34-9b91b7e61b47', '892-30-3492', 'Costantino');
INSERT INTO public.contact (category, id, customer_id, name, professional_id, ssncode, surname) VALUES (1, nextval('public.contact_seq'), null, 'Franco', '0611954d-13a4-4462-b470-5ece6ed15af5', '453-08-9513', 'Sorrentino');

-- address
INSERT INTO public.address (state, contact_id, id, address) VALUES (1, 1, nextval('public.address_seq'), 'Via Giotto, 107, 00012 Pichini RM, Italy');
INSERT INTO public.address (state, contact_id, id, address) VALUES (1, 251, nextval('public.address_seq'), 'Via S. Secondo, 10128 Torino TO, Italy');

-- email
INSERT INTO public.email (state, contact_id, id, email) VALUES (1, 101, nextval('public.email_seq'), 'lucavanni@gmail.com');
INSERT INTO public.email (state, contact_id, id, email) VALUES (1, 201, nextval('public.email_seq'), 'domecosta@gmail.com');

-- telephone
INSERT INTO public.telephone (state, contact_id, id, telephone) VALUES (1, 51, nextval('public.telephone_seq'), '+393312085941');
INSERT INTO public.telephone (state, contact_id, id, telephone) VALUES (1, 151, nextval('public.telephone_seq'), '+393893485567');

-- skill
INSERT INTO public.skill (state, id, job_offer_id, professional_id, skill) VALUES (1, nextval('public.skill_seq'), null, '6189efed-3c1f-4f81-b838-04371721804f', 'Costruzione');
INSERT INTO public.skill (state, id, job_offer_id, professional_id, skill) VALUES (1, nextval('public.skill_seq'), null, '4d79da2e-001d-42f4-9f34-9b91b7e61b47', 'C++');
INSERT INTO public.skill (state, id, job_offer_id, professional_id, skill) VALUES (1, nextval('public.skill_seq'), null, '0611954d-13a4-4462-b470-5ece6ed15af5', 'Idraulica');
INSERT INTO public.skill (state, id, job_offer_id, professional_id, skill) VALUES (1, nextval('public.skill_seq'), 1, null, 'C++');
INSERT INTO public.skill (state, id, job_offer_id, professional_id, skill) VALUES (1, nextval('public.skill_seq'), 51, null, 'Costruzione');
INSERT INTO public.skill (state, id, job_offer_id, professional_id, skill) VALUES (1, nextval('public.skill_seq'), 101, null, 'Idraulica');
INSERT INTO public.skill (state, id, job_offer_id, professional_id, skill) VALUES (1, nextval('public.skill_seq'), 151, null, 'Javascript');
INSERT INTO public.skill (state, id, job_offer_id, professional_id, skill) VALUES (1, nextval('public.skill_seq'), 151, null, 'React');

-- message (no data)
-- note (no data)
-- history (no data)

-- If any error occurs, rollback the transaction
-- Use ROLLBACK instead of COMMIT to undo all changes in case of an error
-- In PostgreSQL, rollback happens automatically if there's an error during execution
COMMIT;