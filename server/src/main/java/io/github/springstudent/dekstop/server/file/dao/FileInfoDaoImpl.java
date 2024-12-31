package io.github.springstudent.dekstop.server.file.dao;

import com.gysoft.jdbc.dao.EntityDaoImpl;
import io.github.springstudent.dekstop.server.file.pojo.FileInfo;
import org.springframework.stereotype.Repository;

/**
 * @author ZhouNing
 * @date 2024/12/31 9:24
 **/
@Repository
public class FileInfoDaoImpl extends EntityDaoImpl<FileInfo, String> implements FileInfoDao{
}
