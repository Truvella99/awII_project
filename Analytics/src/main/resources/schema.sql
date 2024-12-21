DROP TABLE IF EXISTS public.customers_job_offers CASCADE;
DROP TABLE IF EXISTS public.professionals_job_offers CASCADE;
DROP TABLE IF EXISTS public.job_offer CASCADE;
DROP TABLE IF EXISTS public.customer CASCADE;
DROP TABLE IF EXISTS public.professional CASCADE;

-- Table for job offers
CREATE TABLE IF NOT EXISTS public.job_offer
(
    id  BIGINT UNIQUE NOT NULL
);

-- Set ownership for job_offer table
ALTER TABLE public.job_offer
    OWNER TO myuser;

-- Table for customers
CREATE TABLE IF NOT EXISTS public.customer
(
    id      VARCHAR(255) UNIQUE NOT NULL,
    name    VARCHAR(255)        NOT NULL,
    surname VARCHAR(255)        NOT NULL
);

-- Set ownership for customer table
ALTER TABLE public.customer
    OWNER TO myuser;

-- Table for customer-job_offer relationships
CREATE TABLE IF NOT EXISTS public.customers_job_offers
(
    customer_id  VARCHAR(255) NOT NULL ,
    job_offer_id BIGINT NOT NULL ,
    final_status_customer SMALLINT NOT NULL,
        CONSTRAINT job_offer_final_status_check
            CHECK (final_status_customer >= 0 AND final_status_customer <= 3),
    PRIMARY KEY (customer_id, job_offer_id),
    constraint fk_customer_id foreign key (customer_id) references public.customer (id),
    constraint fk_customer_job_offer_id foreign key (job_offer_id) references public.job_offer (id)
);

-- Set ownership for customers_job_offers table
ALTER TABLE public.customers_job_offers
    OWNER TO myuser;

-- Table for professionals
CREATE TABLE IF NOT EXISTS public.professional
(
    id      VARCHAR(255) NOT NULL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL
);

-- Set ownership for professional table
ALTER TABLE public.professional
    OWNER TO myuser;

-- Table for professional-job_offer relationships
CREATE TABLE IF NOT EXISTS public.professionals_job_offers
(
    professional_id VARCHAR(255) NOT NULL,
    job_offer_id    BIGINT       NOT NULL,
    final_status_professional SMALLINT NOT NULL
        CONSTRAINT job_offer_final_status_professional_check
            CHECK (final_status_professional >= 0 AND final_status_professional <= 4),
    PRIMARY KEY (job_offer_id, professional_id),
    CONSTRAINT fk_professional_job_offer_id FOREIGN KEY (job_offer_id) REFERENCES public.job_offer (id),
    CONSTRAINT fk_professional_id FOREIGN KEY (professional_id) REFERENCES public.professional (id)
);

-- Set ownership for professionals_job_offers table
ALTER TABLE public.professionals_job_offers
    OWNER TO myuser;

-- Minimal Pre population to check if works TODO remove
INSERT INTO public.customer VALUES ('1e19391c-134e-49cd-89cd-863bba1bf58f','ale','constumer');
INSERT INTO public.professional VALUES ('6189efed-3c1f-4f81-b838-04371721804f','ale','constescional');
INSERT INTO public.job_offer(id) VALUES (1),(2),(3);
INSERT INTO public.customers_job_offers VALUES ('1e19391c-134e-49cd-89cd-863bba1bf58f',1,0),('1e19391c-134e-49cd-89cd-863bba1bf58f',2,1),('1e19391c-134e-49cd-89cd-863bba1bf58f',3,2);
INSERT INTO public.job_offer(id) VALUES (4),(5),(6);
INSERT INTO public.professionals_job_offers VALUES ('6189efed-3c1f-4f81-b838-04371721804f',4,0),('6189efed-3c1f-4f81-b838-04371721804f',5,1),('6189efed-3c1f-4f81-b838-04371721804f',6,2);