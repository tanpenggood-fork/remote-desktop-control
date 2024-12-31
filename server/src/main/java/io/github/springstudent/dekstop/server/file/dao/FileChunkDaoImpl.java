package io.github.springstudent.dekstop.server.file.dao;

import com.gysoft.jdbc.dao.EntityDaoImpl;
import io.github.springstudent.dekstop.server.file.pojo.FileChunk;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author ZhouNing
 * @date 2024/12/31 9:30
 **/
@Repository
public class FileChunkDaoImpl extends EntityDaoImpl<FileChunk,String> implements FileChunkDao {

    @Override
    public void uploadFile(FileChunk fileChunk, MultipartFile chunk) throws Exception {
        Connection conn = jdbcTemplate.getDataSource().getConnection();
        String SQL = "INSERT INTO file_chunk (id,chunkNo,chunkSize,chunkName,chunkBlob)VALUES(?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(SQL);
        try {
            ps.setString(1,fileChunk.getId());
            ps.setInt(2,fileChunk.getChunkNo());
            ps.setLong(3,fileChunk.getChunkSize());
            ps.setString(4,fileChunk.getChunkName());
            ps.setBinaryStream(5,chunk.getInputStream());
            ps.executeUpdate();
        }finally {
            if(conn!=null){
                conn.close();
            }
            if(ps!=null){
                ps.close();
            }
        }
    }
}
