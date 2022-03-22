CREATE TABLE public.file_metadata_table
(
    id uuid,
    original_filename character varying,
    associated_user uuid NOT NULL,
    date_created timestamp without time zone NOT NULL,
    last_accessed timestamp without time zone,
    file_scope character varying NOT NULL,
    crc32 character varying,
    procedure_phase character varying NOT NULL,
    file_extension character varying,
    size_in_kilobytes integer,
    sha1_digest character varying,

    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);

ALTER TABLE public.file_metadata_table
    OWNER to postgres;