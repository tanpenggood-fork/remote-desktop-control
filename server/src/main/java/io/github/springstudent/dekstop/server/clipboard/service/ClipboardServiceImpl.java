package io.github.springstudent.dekstop.server.clipboard.service;

import com.gysoft.jdbc.bean.Criteria;
import io.github.springstudent.dekstop.server.clipboard.dao.ClipboardDao;
import io.github.springstudent.dekstop.server.clipboard.pojo.Clipboard;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ZhouNing
 * @date 2024/12/31 16:19
 **/
@Service
public class ClipboardServiceImpl implements ClipboardService{

    @Resource
    private ClipboardDao clipboardDao;

    @Override
    public void clear(String deviceCode) throws Exception {
        clipboardDao.deleteWithCriteria(new Criteria().where(Clipboard::getDeviceCode,deviceCode));
    }

    @Override
    public String add(Clipboard clipboard) throws Exception {
        clipboardDao.save(clipboard);
        return clipboard.getId();
    }

    @Override
    public List<Clipboard> get(String deviceCode) throws Exception {
        return clipboardDao.queryWithCriteria(new Criteria().where(Clipboard::getDeviceCode,deviceCode));
    }
}
