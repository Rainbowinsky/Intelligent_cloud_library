package com.guanbean.inteligentcloudbackend.controller;

import com.guanbean.inteligentcloudbackend.annotation.AuthCheck;
import com.guanbean.inteligentcloudbackend.common.BaseResponse;
import com.guanbean.inteligentcloudbackend.common.ResultUtils;
import com.guanbean.inteligentcloudbackend.constant.UserConstant;
import com.guanbean.inteligentcloudbackend.exception.BusinessException;
import com.guanbean.inteligentcloudbackend.exception.ErrorCode;
import com.guanbean.inteligentcloudbackend.manager.CosManager;
import com.guanbean.inteligentcloudbackend.manager.FileManager;
import com.guanbean.inteligentcloudbackend.model.dto.picture.PictureUploadRequest;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.enums.UserRoleEnum;
import com.guanbean.inteligentcloudbackend.model.vo.PictureVO;
import com.guanbean.inteligentcloudbackend.service.PictureService;
import com.guanbean.inteligentcloudbackend.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @Title 文件上传接口
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午3:52
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private CosManager cosManager;
    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    /**
     * 测试文件上传
     * @param multipartFile
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping
    public BaseResponse<String> testUploadFile(@RequestPart("file")MultipartFile multipartFile){
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s",filename);

        File file = null;

        try {
            //上传文件
            file = File.createTempFile(filepath,null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath,file);
            //返回可以访问的地址
            return ResultUtils.success(filepath);
        } catch (IOException e) {
            log.error("文件传输出现问题,path = {}",filepath,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"上传失败");
        } finally {
            if(file!=null){
                boolean deleted = file.delete();
                if(deleted){
                    log.error("文件上传失败 filepath = {}",filepath);
                }
            }

        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }



}
