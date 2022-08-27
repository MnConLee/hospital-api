package com.lmk.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author 李明康
 * @create 2022/8/27 19:27
 */
public interface FileService {
    public String upload(MultipartFile file);
}
