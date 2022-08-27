package com.lmk.yygh.oss.controller;

import com.lmk.yygh.common.result.Result;
import com.lmk.yygh.oss.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 李明康
 * @create 2022/8/27 19:17
 */
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {
    @Autowired
    private FileService fileService;

    /**
     * 上传到阿里云oss
     * @param file
     * @return
     */
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) {
        String url = fileService.upload(file);
        return Result.ok(url);

    }
}
