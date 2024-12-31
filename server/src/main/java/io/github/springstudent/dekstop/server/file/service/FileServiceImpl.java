package io.github.springstudent.dekstop.server.file.service;

import cn.hutool.core.util.IdUtil;
import com.gysoft.jdbc.bean.Criteria;
import com.gysoft.jdbc.bean.Sort;
import io.github.springstudent.dekstop.common.utils.EmptyUtils;
import io.github.springstudent.dekstop.server.file.bean.FileException;
import io.github.springstudent.dekstop.server.file.dao.FileChunkDao;
import io.github.springstudent.dekstop.server.file.dao.FileInfoDao;
import io.github.springstudent.dekstop.server.file.dao.FileUploadProgressDao;
import io.github.springstudent.dekstop.server.file.pojo.FileChunk;
import io.github.springstudent.dekstop.server.file.pojo.FileInfo;
import io.github.springstudent.dekstop.server.file.pojo.FileUploadProgress;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author ZhouNing
 * @date 2024/12/31 9:18
 **/
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileInfoDao fileInfoDao;

    @Resource
    private FileUploadProgressDao fileUploadProgressDao;

    @Resource
    private FileChunkDao fileChunkDao;

    @Override
    public String quickUploadFile(String md5, Long fileSize) throws Exception {
        List<FileInfo> fileInfos = fileInfoDao.queryWithCriteria(new Criteria().where(FileInfo::getFileMd5, md5));
        if (EmptyUtils.isNotEmpty(fileInfos)) {
            FileInfo fileInfo = fileInfos.get(0);
            fileInfo.setId(IdUtil.fastSimpleUUID());
            fileInfo.setUploadTime(new Date());
            fileInfoDao.save(fileInfo);
            return fileInfo.getId();
        }
        FileUploadProgress fileUploadProgress = fileUploadProgressDao.queryOne(new Criteria().where(FileUploadProgress::getFileMd5, md5));
        if (fileUploadProgress == null) {
            FileUploadProgress progress = new FileUploadProgress();
            progress.setId(IdUtil.fastSimpleUUID());
            progress.setFileMd5(md5);
            progress.setFileSize(fileSize);
            progress.setFinishSize(0L);
            fileUploadProgressDao.save(progress);
        }
        return "-1";
    }

    @Override
    public boolean checkChunk(String md5, Integer chunkNo, Long chunkSize) throws Exception {
        FileChunk fileChunk = fileChunkDao.queryOne(new Criteria().where(FileChunk::getChunkName, md5).and(FileChunk::getChunkNo, chunkNo));
        if (fileChunk != null && fileChunk.getChunkSize() - chunkSize == 0) {
            return true;
        } else if (fileChunk != null) {
            fileChunkDao.deleteWithCriteria(new Criteria().where(FileChunk::getChunkName, md5).and(FileChunk::getChunkNo, chunkNo));
            FileUploadProgress fileUploadProgress = fileUploadProgressDao.queryOne(new Criteria().where(FileUploadProgress::getFileMd5, md5));
            if (fileUploadProgress != null) {
                fileUploadProgress.setFinishSize(fileUploadProgress.getFinishSize() - fileChunk.getChunkSize());
                fileUploadProgressDao.update(fileUploadProgress);
            }
        }
        return false;
    }

    @Override
    public String uploadFileChunk(MultipartFile chunk, String md5, Integer chunkNo, String fileName) throws Exception {
        FileUploadProgress fileUploadProgress = fileUploadProgressDao.queryOne(new Criteria().where(FileUploadProgress::getFileMd5, md5));
        if (fileUploadProgress == null) {
            throw new FileException("文件上传进度未初始化");
        }
        if (!checkChunk(md5, chunkNo, chunk.getSize())) {
            FileChunk fileChunk = new FileChunk();
            fileChunk.setChunkNo(chunkNo);
            fileChunk.setChunkName(md5);
            fileChunk.setChunkBlob(chunk.getBytes());
            fileChunk.setChunkSize(chunk.getSize());
            fileChunk.setId(IdUtil.fastSimpleUUID());
            fileChunkDao.uploadFile(fileChunk,chunk);
            Long fileSize = fileUploadProgress.getFileSize();
            Long finishSize = fileUploadProgress.getFinishSize();
            if (finishSize + chunk.getSize() == fileSize) {
                //上传完成
                fileUploadProgress.setFinishSize(fileSize);
                fileUploadProgressDao.update(fileUploadProgress);
                String fileInfoId = IdUtil.fastSimpleUUID();
                FileInfo fileInfo = new FileInfo();
                fileInfo.setId(fileInfoId);
                fileInfo.setFileMd5(md5);
                fileInfo.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                fileInfo.setFileSize(fileSize);
                fileInfo.setSuffix(fileName.substring(fileName.lastIndexOf(".") + 1));
                fileInfo.setUploadTime(new Date());
                fileInfoDao.save(fileInfo);
                return fileInfoId;
            } else if (finishSize + chunk.getSize() < fileSize) {
                fileUploadProgress.setFinishSize(fileUploadProgress.getFinishSize() + chunk.getSize());
                fileUploadProgressDao.update(fileUploadProgress);
            }
        }
        return "-1";
    }


    @Override
    public void download(String fileInfoId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileInfo fileInfo = fileInfoDao.queryOne(fileInfoId);
        if (fileInfoId == null) {
            throw new FileException("文件不存在");
        }
        List<FileChunk> fileChunks = fileChunkDao.queryWithCriteria(new Criteria().where(FileChunk::getChunkName, fileInfo.getFileMd5()).orderBy(new Sort(FileChunk::getChunkNo, "ASC")));
        if (EmptyUtils.isNotEmpty(fileChunks)) {
            response.setContentLengthLong(fileInfo.getFileSize());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileInfo.getFileName() + "." + fileInfo.getSuffix(), "UTF-8"));
            try (OutputStream out = response.getOutputStream()) {
                for (FileChunk fileChunk : fileChunks) {
                    out.write(fileChunk.getChunkBlob());
                }
            }
        }
    }

}
