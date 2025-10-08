package service;

import entity.File;

import java.io.IOException;
import java.io.InputStream;

public interface FileService extends GenericService <File>  {

    File save(File file);
    File update(File file);
    File findByName(String fileName);
    File findByFilePath(String filePath);
    boolean existByName(String fileName);
    void deleteByName(String fileName);
    File uploadFile(String filePath, InputStream fileContent, Long userId) throws IOException;
}
