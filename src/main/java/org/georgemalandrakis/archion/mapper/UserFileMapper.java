package org.georgemalandrakis.archion.mapper;

import org.georgemalandrakis.archion.model.UserFile;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFileMapper {

	public static UserFile map(ResultSet r) throws SQLException {
		UserFile file = new UserFile();
		file.setFileid(String.valueOf(r.getString("fileid")));
		file.setOriginalfilename(r.getString("originalfilename"));
		file.setLocalfilename(r.getString("localfilename"));
		file.setLocation(r.getString("location"));
		file.setFileextension(r.getString("fileextension"));
		file.setSizeinkbs(r.getString("sizeinkbs"));
		file.setFiletype(r.getString("filetype"));
		file.setLastmodified(r.getTimestamp("lastmodified"));

		return file;
	}
}
