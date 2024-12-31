package io.github.springstudent.dekstop.server.clipboard.service;

import io.github.springstudent.dekstop.server.clipboard.pojo.Clipboard;

import java.util.List;

/**
 * @author ZhouNing
 * @date 2024/12/31 16:19
 **/
public interface ClipboardService {
    void clear(String deviceCode)throws Exception;

    String add(Clipboard clipboard)throws Exception;

    List<Clipboard> get(String deviceCode)throws Exception;
}
