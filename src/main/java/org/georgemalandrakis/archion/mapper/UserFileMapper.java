package org.georgemalandrakis.archion.mapper;

import org.georgemalandrakis.archion.model.FileMetadata;
import org.georgemalandrakis.archion.model.FileProcedurePhase;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFileMapper {

	public static FileMetadata map(ResultSet r) throws SQLException {
		FileMetadata file = new FileMetadata();
		file.setFileid(String.valueOf(r.getString("id")));
		file.setOriginalfilename(r.getString("original_filename"));
		file.setFileextension(r.getString("file_extension"));
		file.setSizeinkbs(r.getString("size_in_kilobytes"));
		file.setFiletype(r.getString("file_scope"));
		file.setLastAccessed(r.getTimestamp("last_accessed"));
		file.setUserid(r.getString("associated_user"));
		file.setSha1Hash(r.getString("sha1_digest"));
		file.setPhase(FileProcedurePhase.valueOf(r.getString("procedure_phase")));

		return file;
	}
}
