-- Table: public.mutter

DROP TABLE IF EXISTS public.mutter;

CREATE TABLE public.mutter
(
    id serial NOT NULL,
    name character varying(100) COLLATE pg_catalog."default" NOT NULL,
    text character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT mutter_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;
